package com.example.kisan_buddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Login extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton,registerButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(Login.this, Registration.class);
                startActivity(registerIntent);
            }
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Authenticate with Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Authentication successful, fetch user type from Firestore
                        checkUserTypeAndRedirect();
                    } else {
                        // Handle login failure
                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserTypeAndRedirect() {
        // Get current user ID
        String userId = mAuth.getCurrentUser().getUid();

        // Fetch user type from Firestore
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userType = task.getResult().getString("userType");

                // Redirect based on user type
                if (userType != null) {
                    if (userType.equals("Producer")) {
                        // Redirect to Producer Dashboard
                        requestLocationPermission();
                        Intent producerIntent = new Intent(Login.this, ProducerDashboard.class);
                        startActivity(producerIntent);
                        finish();
                    } else if (userType.equals("Consumer")) {
                        // Redirect to Consumer Dashboard
                        requestLocationPermission();
                        Intent consumerIntent = new Intent(Login.this, ConsumerDashboard.class);
                        startActivity(consumerIntent);
                        finish();
                    } else {
                        Toast.makeText(Login.this, "Unknown user type", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Login.this, "User type not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Login.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fetchAndStoreLocation();
        }
    }

    private void fetchAndStoreLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager != null && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation == null) {
                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                if (lastKnownLocation == null) {
                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                }

                if (lastKnownLocation != null) {
                    double latitude = lastKnownLocation.getLatitude();
                    double longitude = lastKnownLocation.getLongitude();

                    GeoPoint userLocation = new GeoPoint(latitude, longitude);
                    String userId = mAuth.getCurrentUser().getUid();
                    DocumentReference userRef = db.collection("users").document(userId);
                    userRef.update("location", userLocation)
                            .addOnSuccessListener(aVoid -> Toast.makeText(Login.this, "Location updated successfully", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(Login.this, "Error updating location", Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(Login.this, "Location not found", Toast.LENGTH_SHORT).show();
                }
            } catch (SecurityException e) {
                Toast.makeText(Login.this, "Location permission error", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Login.this, "Location permissions not granted or location services disabled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchAndStoreLocation();
            } else {
                Toast.makeText(this, "Location permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}