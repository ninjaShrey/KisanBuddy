package com.example.kisan_buddy;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ProducerDashboard extends AppCompatActivity {

    private Button viewProfileButton;
    private Button sellButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producer_dashboard);

        viewProfileButton = findViewById(R.id.profileButton);

        viewProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Profile Activity
                Intent intent = new Intent(ProducerDashboard.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        sellButton = findViewById(R.id.sellButton);

        // Set up the OnClickListener for the Sell button
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the SellActivity
                Intent intent = new Intent(ProducerDashboard.this, SellActivity.class);
                startActivity(intent); // Start the SellActivity
            }
        });
    }
}
