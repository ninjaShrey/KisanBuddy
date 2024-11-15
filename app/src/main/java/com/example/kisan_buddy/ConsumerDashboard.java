package com.example.kisan_buddy;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ConsumerDashboard extends AppCompatActivity {

    private Button viewProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_dashboard2);

        viewProfileButton = findViewById(R.id.profileButton);

        viewProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Profile Activity
                Intent intent = new Intent(ConsumerDashboard.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}
