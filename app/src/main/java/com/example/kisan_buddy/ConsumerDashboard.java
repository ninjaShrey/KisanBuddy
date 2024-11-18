package com.example.kisan_buddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ConsumerDashboard extends AppCompatActivity {

    private Button viewProfileButton;
    private RecyclerView recyclerView;
    private CropAdapter cropAdapter;
    private List<Crop> cropList;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_dashboard2); // Content specific to ConsumerDashboard

        // Initialize BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        BottomNavigationHelper.setupBottomNavigationView(this, bottomNavigationView);

        firestore = FirebaseFirestore.getInstance();


        recyclerView = findViewById(R.id.recyclerView);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cropList = new ArrayList<>();
        cropAdapter = new CropAdapter(cropList);
        recyclerView.setAdapter(cropAdapter);

        // Fetch all crops
        fetchAllCrops();


    }

    private void fetchAllCrops() {
        firestore.collection("crops")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        cropList.clear();
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
