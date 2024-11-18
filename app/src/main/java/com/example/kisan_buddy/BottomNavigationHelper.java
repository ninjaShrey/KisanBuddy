package com.example.kisan_buddy;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationHelper {

    private static final String TAG = "BottomNavHelper";

    public static void setupBottomNavigationView(final Context context, BottomNavigationView bottomNavigationView) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Get current user ID
        String userId = mAuth.getCurrentUser ().getUid();

        // Fetch user type from Firestore
        DocumentReference userRef = firestore.collection("users").document(userId);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    String userType = task.getResult().getString("userType");

                    // Update navigation based on user type
                    if (userType != null) {
                        boolean isProducer = "producer".equalsIgnoreCase(userType);
                        bottomNavigationView.getMenu().findItem(R.id.nav_sell).setVisible(isProducer);

                        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                            @Override
                            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                Intent intent = null;

                                if (item.getItemId() == R.id.nav_home) {
                                    intent = new Intent(context, ConsumerDashboard.class);
                                } else if (item.getItemId() == R.id.nav_profile) {
                                    intent = new Intent(context, ProfileActivity.class);
                                } else if (item.getItemId() == R.id.nav_sell) {
                                    if (isProducer) {
                                        intent = new Intent(context, SellActivity.class);
                                        Log.d(TAG, "Navigating to SellActivity");
                                    } else {
                                        Toast.makeText(context, "You do not have permission to sell crops", Toast.LENGTH_SHORT).show();
                                        return false;
                                    }
                                }

                                if (intent != null) {
                                    context.startActivity(intent);
                                }

                                return true;
                            }
                        });
                    } else {
                        Toast.makeText(context, "User  type not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Error fetching user data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}