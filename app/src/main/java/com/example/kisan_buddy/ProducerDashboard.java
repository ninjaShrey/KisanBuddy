package com.example.kisan_buddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProducerDashboard extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CropAdapter cropAdapter;
    private List<Crop> cropList;
    private Button viewProfileButton;
    private Button sellButton;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producer_dashboard);

        firestore = FirebaseFirestore.getInstance();

        viewProfileButton = findViewById(R.id.profileButton);
        sellButton = findViewById(R.id.sellButton);

        // Navigate to Profile Activity
        viewProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProducerDashboard.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Navigate to Sell Activity
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProducerDashboard.this, SellActivity.class);
                startActivity(intent);
            }
        });

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cropList = new ArrayList<>();
        cropAdapter = new CropAdapter(cropList);
        recyclerView.setAdapter(cropAdapter);

        // Fetch crops for the logged-in producer
        fetchCropsForProducer();
    }

    private void fetchCropsForProducer() {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (userEmail == null) {
            Toast.makeText(this, "Error: Unable to fetch user email", Toast.LENGTH_SHORT).show();
            return;
        }

        firestore.collection("crops")
                .whereEqualTo("producerEmail", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        cropList.clear(); // Clear the list before adding new data
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Crop crop = document.toObject(Crop.class);
                            cropList.add(crop);
                        }
                        cropAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to fetch crops: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
