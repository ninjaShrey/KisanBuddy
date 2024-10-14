package com.example.kisan_buddy;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class activity_dashboard extends AppCompatActivity {

    private TextView welcomeTextView;
    private TextView userTypeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        welcomeTextView = findViewById(R.id.welcomeTextView);
        userTypeTextView = findViewById(R.id.userTypeTextView);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            welcomeTextView.setText("Welcome, " + user.getEmail());
            // Optionally fetch and display user type from Firestore
        }
    }
}
