package com.example.kisan_buddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base); // Base layout with BottomNavigationView
    }

    // This method handles BottomNavigationView setup
    protected void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Set up navigation item clicks with if-else
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;

                if (item.getItemId() == R.id.nav_home) {
                    intent = new Intent(BaseActivity.this, ConsumerDashboard.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.nav_profile) {
                    intent = new Intent(BaseActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getItemId() == R.id.nav_sell) {
                    intent = new Intent(BaseActivity.this, SellActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }

    // This method will be called in child activities to set the selected menu item
    protected int getBottomNavigationMenuItemId() {
        return R.id.nav_home; // Default to Home (this can be overridden by child activities if needed)
    }
}
