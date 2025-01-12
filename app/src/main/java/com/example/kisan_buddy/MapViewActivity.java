package com.example.kisan_buddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class MapViewActivity extends AppCompatActivity {

    private String customerEmail;
    private double customerLatitude;
    private double customerLongitude;
    private String producerEmail;
    private double producerLatitude;
    private double producerLongitude;
    private String cropDocId;
    private String bidderDocId;

    private TextView emailTextView;
    private TextView coordinatesTextView;
    private TextView producerEmailTextView;
    private TextView producerCoordinatesTextView;
    private TextView nearestMarketTextView;

    private MapView mapView;
    private GoogleMap googleMap;
    private FirebaseFirestore db;

    // New Button to navigate to ExpenseCalculatorActivity
    private Button navigateToExpenseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);

        // Retrieve data from Intent
        customerEmail = getIntent().getStringExtra("email");
        customerLatitude = getIntent().getDoubleExtra("latitude", 0);
        customerLongitude = getIntent().getDoubleExtra("longitude", 0);
        producerEmail = getIntent().getStringExtra("producerEmail");
        producerLatitude = getIntent().getDoubleExtra("producerLatitude", 0);
        producerLongitude = getIntent().getDoubleExtra("producerLongitude", 0);
        cropDocId = getIntent().getStringExtra("cropDocId");
        bidderDocId = getIntent().getStringExtra("bidderDocId");

        // Initialize views
        emailTextView = findViewById(R.id.customerEmailTextView);
        coordinatesTextView = findViewById(R.id.customerCoordinatesTextView);
        producerEmailTextView = findViewById(R.id.producerEmailTextView);
        producerCoordinatesTextView = findViewById(R.id.producerCoordinatesTextView);
        nearestMarketTextView = findViewById(R.id.nearestMarketTextView);

        // Set initial text
        emailTextView.setText("Customer Email: " + customerEmail);
        coordinatesTextView.setText("Customer Coordinates: " + customerLatitude + ", " + customerLongitude);
        producerEmailTextView.setText("Producer Email: " + producerEmail);
        producerCoordinatesTextView.setText("Producer Coordinates: " + producerLatitude + ", " + producerLongitude);

        // Initialize MapView
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize Button to navigate to ExpenseCalculatorActivity
        navigateToExpenseButton = findViewById(R.id.navigateToExpenseButton);

        // Button click listener to navigate to ExpenseCalculatorActivity
        navigateToExpenseButton.setOnClickListener(v -> {
            Intent intent = new Intent(MapViewActivity.this, ExpenseCalculatorActivity.class);
            intent.putExtra("cropDocId", cropDocId);
            intent.putExtra("bidderDocId", bidderDocId);
            intent.putExtra("producerEmail", producerEmail);
            intent.putExtra("customerEmail", customerEmail);
            intent.putExtra("producerLatitude", producerLatitude);
            intent.putExtra("producerLongitude", producerLongitude);
            intent.putExtra("customerLatitude", customerLatitude);
            intent.putExtra("customerLongitude", customerLongitude);

            LatLng nearestMarket = findNearestMarket(new LatLng(customerLatitude, customerLongitude), getMandiLocations());
            if (nearestMarket != null) {
                intent.putExtra("nearestMarketLatitude", nearestMarket.latitude);
                intent.putExtra("nearestMarketLongitude", nearestMarket.longitude);
            }

            startActivity(intent);
        });

        // Load map and add markers
        mapView.getMapAsync(map -> {
            googleMap = map;

            LatLng customerLocation = new LatLng(customerLatitude, customerLongitude);
            LatLng producerLocation = new LatLng(producerLatitude, producerLongitude);

            googleMap.addMarker(new MarkerOptions().position(customerLocation).title("Customer Location"));
            googleMap.addMarker(new MarkerOptions().position(producerLocation).title("Producer Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(producerLocation, 10));

            List<LatLng> mandiLocations = getMandiLocations();
            LatLng nearestMarket = findNearestMarket(customerLocation, mandiLocations);

            if (nearestMarket != null) {
                googleMap.addMarker(new MarkerOptions().position(nearestMarket).title("Nearest Market")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                nearestMarketTextView.setText("Nearest Market: (" + nearestMarket.latitude + ", " + nearestMarket.longitude + ")");
            } else {
                nearestMarketTextView.setText("No markets found.");
            }
        });
    }

    private List<LatLng> getMandiLocations() {
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
        return mandiPlaces;
    }

    private LatLng findNearestMarket(LatLng customerLocation, List<LatLng> mandiLocations) {
        LatLng nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (LatLng mandi : mandiLocations) {
            double distance = calculateDistance(customerLocation.latitude, customerLocation.longitude,
                    mandi.latitude, mandi.longitude);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = mandi;
            }
        }
        return nearest;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the Earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in km
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
