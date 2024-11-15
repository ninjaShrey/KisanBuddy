package com.example.kisan_buddy;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SellActivity extends AppCompatActivity {

    private EditText cropNameEditText, cropQuantityEditText, cropWeightEditText;
    private Button saveProductButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

        cropNameEditText = findViewById(R.id.cropNameEditText);
        cropQuantityEditText = findViewById(R.id.cropQuantityEditText);
        cropWeightEditText = findViewById(R.id.cropWeightEditText);
        saveProductButton = findViewById(R.id.saveProductButton);

        db = FirebaseFirestore.getInstance();

        // On Save button click, save the crop details to Firestore
        saveProductButton.setOnClickListener(v -> saveCropDetails());
    }

    private void saveCropDetails() {
        String cropName = cropNameEditText.getText().toString();
        String cropQuantity = cropQuantityEditText.getText().toString();
        String cropWeight = cropWeightEditText.getText().toString();

        if (cropName.isEmpty() || cropQuantity.isEmpty() || cropWeight.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the producer's email from Firebase Authentication
        String producerEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (producerEmail != null) {
            // Save crop data to Firestore with a reference to the producer's email
            Crop crop = new Crop(cropName, cropQuantity, cropWeight, producerEmail);

            db.collection("crops")
                    .add(crop)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SellActivity.this, "Crop listed successfully", Toast.LENGTH_SHORT).show();
                            finish(); // Close the SellActivity and go back to the Producer Dashboard
                        } else {
                            Toast.makeText(SellActivity.this, "Failed to list crop", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
