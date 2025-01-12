package com.example.kisan_buddy;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ExpenseCalculatorActivity extends AppCompatActivity {

    private TextView expenseTextView, customerEmailTextView, producerEmailTextView,
            cropQuantityTextView, cropWeightTextView, transportationCostTextView,
            additionalExpensesTextView, totalExpensesTextView, bidAmountTextView,
            profitTextView, bestVehicleTextView;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_calculator);

        // Initialize the Firestore instance
        db = FirebaseFirestore.getInstance();

        // Initialize the TextViews

        customerEmailTextView = findViewById(R.id.customerEmailTextView);
        producerEmailTextView = findViewById(R.id.producerEmailTextView);
        cropQuantityTextView = findViewById(R.id.cropQuantityTextView);
        cropWeightTextView = findViewById(R.id.cropWeightTextView);
        transportationCostTextView = findViewById(R.id.transportationCostTextView);
        additionalExpensesTextView = findViewById(R.id.additionalExpensesTextView);
        totalExpensesTextView = findViewById(R.id.totalExpensesTextView);
        bidAmountTextView = findViewById(R.id.bidAmountTextView);
        profitTextView = findViewById(R.id.profitTextView);
        bestVehicleTextView = findViewById(R.id.bestVehicleTextView);

        // Retrieve the data passed from MapViewActivity
        String cropDocId = getIntent().getStringExtra("cropDocId");
        String bidderDocId = getIntent().getStringExtra("bidderDocId");
        String producerEmail = getIntent().getStringExtra("producerEmail");
        String customerEmail = getIntent().getStringExtra("customerEmail");
        double customerLatitude = getIntent().getDoubleExtra("customerLatitude", 0);
        double customerLongitude = getIntent().getDoubleExtra("customerLongitude", 0);
        double producerLatitude = getIntent().getDoubleExtra("producerLatitude", 0);
        double producerLongitude = getIntent().getDoubleExtra("producerLongitude", 0);
        double nearestMarketLatitude = getIntent().getDoubleExtra("nearestMarketLatitude", 0);
        double nearestMarketLongitude = getIntent().getDoubleExtra("nearestMarketLongitude", 0);

        // Set email fields
        customerEmailTextView.setText("Customer Email: " + customerEmail);
        producerEmailTextView.setText("Producer Email: " + producerEmail);

        // Retrieve crop data from Firestore
        fetchCropData(cropDocId, bidderDocId, producerEmail, customerEmail, customerLatitude, customerLongitude,
                producerLatitude, producerLongitude, nearestMarketLatitude, nearestMarketLongitude);
    }

    private void fetchCropData(final String cropDocId, final String bidderDocId, final String producerEmail, final String customerEmail,
                               final double customerLatitude, final double customerLongitude, final double producerLatitude,
                               final double producerLongitude, final double nearestMarketLatitude, final double nearestMarketLongitude) {

        db.collection("crops")
                .document(cropDocId)  // Access the specific crop document by its ID
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Retrieve crop quantity and crop weight as strings
                            String cropQuantityStr = documentSnapshot.getString("cropQuantity");
                            String cropWeightStr = documentSnapshot.getString("cropWeight");

                            // Convert strings to Double, if they are not null or empty
                            Double cropQuantity = 0.0;
                            Double cropWeight = 0.0;

                            try {
                                if (cropQuantityStr != null && !cropQuantityStr.isEmpty()) {
                                    cropQuantity = Double.parseDouble(cropQuantityStr);
                                }
                            } catch (NumberFormatException e) {
                                cropQuantity = 0.0; // default value
                            }

                            try {
                                if (cropWeightStr != null && !cropWeightStr.isEmpty()) {
                                    cropWeight = Double.parseDouble(cropWeightStr);
                                }
                            } catch (NumberFormatException e) {
                                cropWeight = 0.0; // default value
                            }

                            // Set crop details
                            cropQuantityTextView.setText("Crop Quantity (kg): " + cropQuantity);
                            cropWeightTextView.setText("Crop Weight (kg): " + cropWeight);

                            // Fetch the bid amount from the bidders subcollection
                            Double finalCropQuantity = cropQuantity;
                            Double finalCropWeight = cropWeight;
                            db.collection("crops")
                                    .document(cropDocId)
                                    .collection("bidders")  // Access the bidders subcollection
                                    .document(bidderDocId)  // Access the bidder document by its ID
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot bidderDocument) {
                                            if (bidderDocument.exists()) {
                                                // Retrieve bidAmount from the bidder document
                                                Double bidAmount = bidderDocument.getDouble("bidAmount");

                                                // If bidAmount is null, set it to 0.0 as default
                                                if (bidAmount == null) {
                                                    bidAmount = 0.0;
                                                }

                                                // Calculate the distance between producer and nearest market
                                                double distance = calculateDistance(producerLatitude, producerLongitude, nearestMarketLatitude, nearestMarketLongitude);
                                                String bestVehicleSuggestion = suggestBestVehicle(distance);

                                                // Calculate transportation cost and other expenses
                                                transportationCostTextView.setText("Transportation Cost: " + (distance * 7) + " INR");
                                                additionalExpensesTextView.setText("Additional Expenses: " + (finalCropQuantity * 5) + " INR");

                                                String result = calculateExpenseAndProfit(distance, bestVehicleSuggestion, finalCropQuantity, finalCropWeight, bidAmount);

                                                totalExpensesTextView.setText("Total Expenses: " + (distance * 7 + finalCropQuantity * 5) + " INR");
                                                bidAmountTextView.setText("Bid Amount: " + bidAmount + " INR");
                                                profitTextView.setText("Profit: " + (bidAmount - (distance * 7 + finalCropQuantity * 5)) + " INR");
                                                bestVehicleTextView.setText(bestVehicleSuggestion);
                                            } else {
                                                expenseTextView.setText("No bidder data found for the provided bidder document ID.");
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        expenseTextView.setText("Error fetching bidder data: " + e.getMessage());
                                    });
                        } else {
                            expenseTextView.setText("No crop data found for the provided document ID.");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    expenseTextView.setText("Error fetching crop data: " + e.getMessage());
                });
    }

    // Method to calculate the distance between two locations using the Haversine formula
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the Earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in km
    }

    // Method to suggest the best vehicle based on the distance
    private String suggestBestVehicle(double distance) {
        // Define the cost per km for each vehicle size
        double smallVehicleCost = 5;  // INR per km (simplified)
        double mediumVehicleCost = 7; // INR per km (simplified)
        double largeVehicleCost = 10;  // INR per km (simplified)

        // Calculate the transport costs for each vehicle size
        double smallVehicleTotalCost = smallVehicleCost * distance;
        double mediumVehicleTotalCost = mediumVehicleCost * distance;
        double largeVehicleTotalCost = largeVehicleCost * distance;

        // Compare the costs and suggest the best vehicle
        if (distance <= 20) {
            return "Small Vehicle is the best option for this distance of " + distance + " km. Total cost: " + smallVehicleTotalCost + " INR";
        } else if (distance > 20 && distance <= 50) {
            if (mediumVehicleTotalCost < largeVehicleTotalCost) {
                return "Medium Vehicle is the best option for this distance of " + distance + " km. Total cost: " + mediumVehicleTotalCost + " INR";
            } else {
                return "Large Vehicle might be required, but Medium Vehicle is still a better option for this distance of " + distance + " km. Total cost: " + mediumVehicleTotalCost + " INR";
            }
        } else {
            return "Large Vehicle is the best option for this long distance of " + distance + " km. Total cost: " + largeVehicleTotalCost + " INR";
        }
    }

    // Method to calculate expense and profit
    private String calculateExpenseAndProfit(double distance, String bestVehicleSuggestion, double cropQuantity, double cropWeight, double bidAmount) {
        // Assume other expenses like packing, handling, etc. (here considered as a fixed value for simplicity)
        double additionalExpense = cropQuantity * 5;  // 5 rupees per kg for example

        // Calculate transportation cost based on the vehicle size
        double transportCost = 0;
        if (bestVehicleSuggestion.contains("Small Vehicle")) {
            transportCost = 5 * distance;
        } else if (bestVehicleSuggestion.contains("Medium Vehicle")) {
            transportCost = 7 * distance;
        } else if (bestVehicleSuggestion.contains("Large Vehicle")) {
            transportCost = 10 * distance;
        }

        // Total expense (transport cost + additional expenses based on crop quantity)
        double totalExpense = transportCost + additionalExpense;

        // Calculate profit (Bid amount - Total expense)
        double profit = bidAmount - totalExpense;

        // Return formatted result
        return "Transportation Cost: " + transportCost + " INR\n" +
                "Additional Expenses: " + additionalExpense + " INR\n" +
                "Total Expenses: " + totalExpense + " INR\n" +
                "Bid Amount: " + bidAmount + " INR\n" +
                "Profit: " + profit + " INR\n" +
                bestVehicleSuggestion;
    }
}
