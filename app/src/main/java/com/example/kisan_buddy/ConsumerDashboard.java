package com.example.kisan_buddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ConsumerDashboard extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CropAdapter cropAdapter;
    private List<Crop> cropList;
    private FirebaseFirestore firestore;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_dashboard2);

        // Initialize BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        BottomNavigationHelper.setupBottomNavigationView(this, bottomNavigationView);

        firestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cropList = new ArrayList<>();
        cropAdapter = new CropAdapter(cropList);
        recyclerView.setAdapter(cropAdapter);

        // Fetch all crops using documentId
        fetchAllCrops();

        // Set up SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterAndSortCrops(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterAndSortCrops(newText);
                return true;
            }
        });
    }

    private void fetchAllCrops() {
        firestore.collection("crops")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        cropList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId(); // Firestore document ID
                            Crop crop = document.toObject(Crop.class);
                            crop.setId(documentId); // Set the document ID as id
                            int bidCount = getBidCountForCrop(documentId);
                            crop.setBidCount(bidCount); // Set the bid count
                            cropList.add(crop);
                        }
                        cropAdapter.updateList(cropList);
                    } else {
                        Toast.makeText(this, "Failed to fetch crops: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        cropAdapter.setOnItemClickListener(crop -> {
            // Navigate to BiddingActivity with documentId
            Intent intent = new Intent(ConsumerDashboard.this, BiddingActivity.class);
            intent.putExtra("documentId", crop.getId());
            startActivity(intent);
        });
    }

    private void filterAndSortCrops(String query) {
        if (query == null || query.trim().isEmpty()) {
            cropAdapter.updateList(cropList);
            return;
        }

        List<Crop> filteredList = new ArrayList<>();
        for (Crop crop : cropList) {
            if (crop.getCropName() != null && crop.getCropName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(crop);
            }
        }

        // Sort by price in ascending order
        Collections.sort(filteredList, new Comparator<Crop>() {
            @Override
            public int compare(Crop c1, Crop c2) {
                double price1 = c1.getPrice() != null ? Double.parseDouble(c1.getPrice()) : 0.0; // Handle null safely
                double price2 = c2.getPrice() != null ? Double.parseDouble(c2.getPrice()) : 0.0; // Handle null safely
                return Double.compare(price1, price2);
            }
        });

        cropAdapter.updateList(filteredList);
    }

    private int getBidCountForCrop(String documentId) {
        final int[] bidCount = {0};
        firestore.collection("crops")
                .document(documentId)
                .collection("bidders")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bidCount[0] = task.getResult().size();
                        // Update the Crop object with bid count
                        updateCropBidCount(documentId, bidCount[0]);
                    }
                });
        return bidCount[0];
    }

    private void updateCropBidCount(String documentId, int bidCount) {
        for (Crop crop : cropList) {
            if (crop.getId().equals(documentId)) {
                crop.setBidCount(bidCount); // Update the crop object with bid count
                break;
            }
        }
        cropAdapter.updateList(cropList); // Notify adapter of the update
    }
}
