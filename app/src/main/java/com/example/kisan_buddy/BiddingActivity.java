package com.example.kisan_buddy;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class BiddingActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private TextView cropNameTextView, cropPriceTextView, cropQuantityTextView, maxBidderTextView;
    private EditText consumerPriceEditText, maxBidderPriceEditText, secondMaxBidderPriceEditText, thirdMaxBidderPriceEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bidding_activity);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        cropNameTextView = findViewById(R.id.cropNameTextView);
        cropPriceTextView = findViewById(R.id.cropPriceTextView);
        cropQuantityTextView = findViewById(R.id.cropQuantityTextView);
        maxBidderTextView = findViewById(R.id.maxBidderTextView);

        consumerPriceEditText = findViewById(R.id.consumerPriceEditText);
        maxBidderPriceEditText = findViewById(R.id.maxBidderPriceEditText);
        secondMaxBidderPriceEditText = findViewById(R.id.secondMaxBidderPriceEditText);
        thirdMaxBidderPriceEditText = findViewById(R.id.thirdMaxBidderPriceEditText);

        // Get cropId from Intent
        String documentId = getIntent().getStringExtra("documentId");

        // Fetch crop details using documentId
        fetchCropDetails(documentId);

        // Start Bidding Button
        findViewById(R.id.startBiddingButton).setOnClickListener(v -> placeBid());
    }

    private void fetchCropDetails(String documentId) {
        firestore.collection("crops")
                .document(documentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Crop crop = document.toObject(Crop.class);
                            crop.setId(document.getId());  // Set the document ID
                            updateUI(crop);  // Assuming you have a method to update the UI
                        } else {
                            Toast.makeText(this, "Crop not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to fetch crop details: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(Crop crop) {
        cropNameTextView.setText(crop.getCropName());
        cropPriceTextView.setText("Price: $" + crop.getPrice());
        cropQuantityTextView.setText("Quantity: " + crop.getCropQuantity());

        // Display top 3 bidders
        fetchTopBidders(crop.getId());
    }

    private void fetchTopBidders(String documentId) {
        firestore.collection("crops")
                .document(documentId)
                .collection("bidders")
                .orderBy("bidAmount", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int index = 0;
                        for (DocumentSnapshot document : task.getResult()) {
                            if (index == 0) {
                                maxBidderPriceEditText.setText("1st Bidder Price: $" + document.getDouble("bidAmount"));
                            } else if (index == 1) {
                                secondMaxBidderPriceEditText.setText("2nd Bidder Price: $" + document.getDouble("bidAmount"));
                            } else if (index == 2) {
                                thirdMaxBidderPriceEditText.setText("3rd Bidder Price: $" + document.getDouble("bidAmount"));
                            }
                            index++;
                        }
                    } else {
                        Toast.makeText(this, "Failed to fetch bidders: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void placeBid() {
        String consumerPrice = consumerPriceEditText.getText().toString();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid(); // Get the user ID
            // Fetch user details from users collection using user ID
            firestore.collection("users").document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String consumerName = document.getString("name");  // Get the name
                                String consumerEmail = document.getString("email");  // Get the email

                                if (!consumerPrice.isEmpty()) {
                                    // Create a new Bidder object with name and email
                                    Bidder newBidder = new Bidder(consumerName, consumerEmail, Double.parseDouble(consumerPrice));

                                    String documentId = getIntent().getStringExtra("documentId");

                                    firestore.collection("crops")
                                            .document(documentId)
                                            .collection("bidders")
                                            .add(newBidder)
                                            .addOnCompleteListener(bidTask -> {
                                                if (bidTask.isSuccessful()) {
                                                    Toast.makeText(this, "Bid placed successfully", Toast.LENGTH_SHORT).show();
                                                    fetchTopBidders(documentId);
                                                } else {
                                                    Toast.makeText(this, "Failed to place bid: " + bidTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(this, "Please enter a valid bid", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "User details not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Failed to fetch user details: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
