package com.example.kisan_buddy;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class MapViewActivity extends AppCompatActivity {

    private String customerEmail;
    private double customerLatitude;
    private double customerLongitude;
    private String producerEmail;
    private double producerLatitude;
    private double producerLongitude;
    private TextView emailTextView;
    private TextView coordinatesTextView;
    private TextView producerEmailTextView;
    private TextView producerCoordinatesTextView;
    private TextView nearestMarketTextView;
    private TextView expenditureTextView;
    private MapView mapView;
    private GoogleMap googleMap;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        // Retrieve data from the intent
        customerEmail = getIntent().getStringExtra("email");
        customerLatitude = getIntent().getDoubleExtra("latitude", 0);
        customerLongitude = getIntent().getDoubleExtra("longitude", 0);
        producerEmail = getIntent().getStringExtra("producerEmail");
        producerLatitude = getIntent().getDoubleExtra("producerLatitude", 0);
        producerLongitude = getIntent().getDoubleExtra("producerLongitude", 0);

        // Initialize TextViews
        emailTextView = findViewById(R.id.customerEmailTextView);
        coordinatesTextView = findViewById(R.id.customerCoordinatesTextView);
        producerEmailTextView = findViewById(R.id.producerEmailTextView);
        producerCoordinatesTextView = findViewById(R.id.producerCoordinatesTextView);
        nearestMarketTextView = findViewById(R.id.nearestMarketTextView);
        expenditureTextView = findViewById(R.id.expenditureTextView);

        // Set text for TextViews
        if (customerEmail != null) {
            emailTextView.setText("Customer Email: " + customerEmail);
        }
        coordinatesTextView.setText("Customer Coordinates: " + customerLatitude + ", " + customerLongitude);
        producerEmailTextView.setText("Producer Email: " + producerEmail);
        producerCoordinatesTextView.setText("Producer Coordinates: " + producerLatitude + ", " + producerLongitude);

        // Initialize MapView
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        if (customerLatitude != 0 && customerLongitude != 0 && producerLatitude != 0 && producerLongitude != 0) {
            mapView.getMapAsync(map -> {
                googleMap = map;

                // Add customer marker
                LatLng customerLocation = new LatLng(customerLatitude, customerLongitude);
                googleMap.addMarker(new MarkerOptions().position(customerLocation).title("Customer Location"));

                // Add producer marker
                LatLng producerLocation = new LatLng(producerLatitude, producerLongitude);
                googleMap.addMarker(new MarkerOptions().position(producerLocation).title("Producer Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                // Move camera to show both markers
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(producerLocation, 10));

                // List of mandi locations in Karnataka
                List<LatLng> mandiPlaces = new ArrayList<>();
                mandiPlaces.add(new LatLng(12.9716, 77.5946));  // Bengaluru
                mandiPlaces.add(new LatLng(15.3173, 75.7139));  // Hubli
                mandiPlaces.add(new LatLng(15.3150, 74.1238));  // Belagavi
                mandiPlaces.add(new LatLng(12.2958, 76.6394));  // Mysuru
                mandiPlaces.add(new LatLng(14.4599, 75.9232));  // Davanagere
                mandiPlaces.add(new LatLng(12.9141, 74.8560));  // Mangalore
                mandiPlaces.add(new LatLng(17.3265, 76.8385));  // Gulbarga
                mandiPlaces.add(new LatLng(13.3141, 75.7484));  // Chikmagalur
                mandiPlaces.add(new LatLng(13.0354, 76.1080));  // Hassan
                mandiPlaces.add(new LatLng(17.9224, 77.5275));  // Bidar
                mandiPlaces.add(new LatLng(14.8519, 74.1191));  // Karwar
                mandiPlaces.add(new LatLng(16.2010, 77.3605));  // Raichur
                mandiPlaces.add(new LatLng(15.1531, 76.9120));  // Ballari
                mandiPlaces.add(new LatLng(13.3450, 74.7459));  // Udupi
                mandiPlaces.add(new LatLng(17.3255, 76.8386));  // Kalaburagi
                mandiPlaces.add(new LatLng(13.1622, 78.0745));  // Kolar
                mandiPlaces.add(new LatLng(13.9203, 75.5610));  // Shimoga
                mandiPlaces.add(new LatLng(16.9877, 77.3604));  // Yadgir
                mandiPlaces.add(new LatLng(13.3424, 77.1006));  // Tumakuru
                mandiPlaces.add(new LatLng(16.8265, 75.7294));  // Bijapur

                LatLng nearestMarket = null;
                double minDistance = Double.MAX_VALUE;

                // Calculate distance to each mandi and find the nearest
                for (LatLng mandi : mandiPlaces) {
                    double distance = calculateDistance(customerLocation.latitude, customerLocation.longitude, mandi.latitude, mandi.longitude);
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestMarket = mandi;
                    }
                }

                if (nearestMarket != null) {
                    googleMap.addMarker(new MarkerOptions().position(nearestMarket).title("Nearest Market").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    nearestMarketTextView.setText("Nearest Market: (" + nearestMarket.latitude + ", " + nearestMarket.longitude + ")");
                } else {
                    nearestMarketTextView.setText("No markets found.");
                }

                // Fetch crop data from producer's crops collection
                db.collection("crops")
                        .document(producerEmail)
                        .get()
                        .addOnSuccessListener(cropSnapshot -> {
                            if (cropSnapshot.exists()) {
                                double cropQuantity = cropSnapshot.getDouble("cropQuantity");
                                double cropWeight = cropSnapshot.getDouble("cropWeight");

                                // Fetch bid data from customer's bidders sub-collection
                                db.collection("bidders")
                                        .document(customerEmail)
                                        .get()
                                        .addOnSuccessListener(bidSnapshot -> {
                                            if (bidSnapshot.exists()) {
                                                double bidAmount = bidSnapshot.getDouble("bidAmount");
                                                double distanceInKm = calculateDistance(customerLocation.latitude, customerLocation.longitude, producerLocation.latitude, producerLocation.longitude);
                                                double producerExpenditure = distanceInKm * 10; // Assuming 1 km = 10 rupees
                                                double consumerExpenditure = distanceInKm * 5; // Assuming 1 km = 5 rupees
                                                double totalExpenditure = producerExpenditure + consumerExpenditure;
                                                double profit = bidAmount - totalExpenditure;

                                                // Display expenditure and profit
                                                expenditureTextView.setText("Expenditure: " + totalExpenditure + " rupees\nProfit: " + profit + " rupees");
                                            } else {
                                                expenditureTextView.setText("No bid data found for customer.");
                                            }
                                        })
                                        .addOnFailureListener(e -> expenditureTextView.setText("Error fetching bid data."));
                            } else {
                                expenditureTextView.setText("No crop data found for producer.");
                            }
                        })
                        .addOnFailureListener(e -> expenditureTextView.setText("Error fetching crop data."));
            });
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;  // Radius of the Earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;  // Distance in km
    }
}
