package com.example.kisan_buddy;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import java.util.ArrayList;
import java.util.List;

public class CustomerListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomerAdapter customerAdapter;
    private List<Customer> customerList;
    private FirebaseFirestore firestore;
    private String producerEmail;
    private double producerLatitude;
    private double producerLongitude;
    private String bidderDocId;
    private String cropDocId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        firestore = FirebaseFirestore.getInstance();

        // Fetch logged-in producer's email and location
        producerEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        fetchProducerLocation();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        customerList = new ArrayList<>();
        customerAdapter = new CustomerAdapter(customerList);
        recyclerView.setAdapter(customerAdapter);

        // Fetch customer details based on cropDocId passed from ProducerDashboard
        cropDocId = getIntent().getStringExtra("cropDocId");
        fetchCustomersForCrop(cropDocId);

        customerAdapter.setOnItemClickListener(customer -> showSettleBidDialog(customer));
    }

    private void fetchProducerLocation() {
        // Assuming the producer's email is already fetched
        firestore.collection("users")
                .whereEqualTo("email", producerEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        GeoPoint location = task.getResult().getDocuments().get(0).getGeoPoint("location");
                        if (location != null) {
                            producerLatitude = location.getLatitude();
                            producerLongitude = location.getLongitude();
                        } else {
                            Toast.makeText(this, "Producer location coordinates not found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to fetch producer location: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchCustomersForCrop(String cropDocId) {
        firestore.collection("crops")
                .document(cropDocId)
                .collection("bidders")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        customerList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Customer customer = document.toObject(Customer.class);
                            customer.setBidderDocId(document.getId());
                            customerList.add(customer);
                        }
                        customerAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to fetch customer details: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showSettleBidDialog(Customer customer) {
        // Show a dialog asking if the user wants to settle the bid
        new AlertDialog.Builder(this)
                .setTitle("Settle Bid")
                .setMessage("Do you want to settle the bid with this customer?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Save customer email
                    String customerEmail = customer.getEmail();
                    bidderDocId = customer.getBidderDocId();

                    // Fetch location coordinates
                    fetchCustomerLocation(customerEmail);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void fetchCustomerLocation(String customerEmail) {
        firestore.collection("users")
                .whereEqualTo("email", customerEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        GeoPoint location = task.getResult().getDocuments().get(0).getGeoPoint("location");
                        if (location != null) {
                            // Use the location coordinates
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            // Pass the email and coordinates to MapViewActivity
                            Intent intent = new Intent(this, MapViewActivity.class);
                            intent.putExtra("email", customerEmail);
                            intent.putExtra("latitude", latitude);
                            intent.putExtra("longitude", longitude);

                            // Also include producer's details
                            intent.putExtra("producerEmail", producerEmail);
                            intent.putExtra("producerLatitude", producerLatitude);
                            intent.putExtra("producerLongitude", producerLongitude);
                            intent.putExtra("cropDocId",cropDocId);
                            intent.putExtra("bidderDocId",bidderDocId);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Location coordinates not found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to fetch customer location: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
