package com.example.kisan_buddy;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView emailTextView, userTypeTextView;
    private EditText nameEditText, ageEditText, genderEditText, aadharEditText;
    private Button saveButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        emailTextView = findViewById(R.id.emailTextView);
        userTypeTextView = findViewById(R.id.userTypeTextView);
        nameEditText = findViewById(R.id.nameEditText);
        ageEditText = findViewById(R.id.ageEditText);
        genderEditText = findViewById(R.id.genderEditText);
        aadharEditText = findViewById(R.id.aadharEditText);
        saveButton = findViewById(R.id.saveButton);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Fetch user data from Firestore and display it
        fetchUserData();

        // Set up save button
        saveButton.setOnClickListener(v -> saveUserData());
        // Initialize BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        BottomNavigationHelper.setupBottomNavigationView(this, bottomNavigationView);
    }

    private void fetchUserData() {
        String userId = mAuth.getCurrentUser().getUid();

        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String email = task.getResult().getString("email");
                String userType = task.getResult().getString("userType");
                String name = task.getResult().getString("name");
                String age = task.getResult().getString("age");
                String gender = task.getResult().getString("gender");
                String aadhar = task.getResult().getString("aadhar");

                // Display email and userType (non-editable)
                emailTextView.setText("Email: " + email);
                userTypeTextView.setText("User Type: " + userType);

                // Show name, age, gender, and aadhar (edit or display based on whether data exists)
                if (name != null && age != null && gender != null && aadhar != null) {
                    nameEditText.setText(name);
                    ageEditText.setText(age);
                    genderEditText.setText(gender);
                    aadharEditText.setText(aadhar);
                    setEditable(false);  // If data exists, disable editing
                } else {
                    setEditable(true);  // Allow user to edit the profile if no data exists
                }
            } else {
                // Handle failure to fetch user data
                Toast.makeText(ProfileActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setEditable(boolean isEditable) {
        nameEditText.setEnabled(isEditable);
        ageEditText.setEnabled(isEditable);
        genderEditText.setEnabled(isEditable);
        aadharEditText.setEnabled(isEditable);
        saveButton.setVisibility(isEditable ? View.VISIBLE : View.GONE);
    }

    private void saveUserData() {
        String name = nameEditText.getText().toString();
        String age = ageEditText.getText().toString();
        String gender = genderEditText.getText().toString();
        String aadhar = aadharEditText.getText().toString();

        if (name.isEmpty() || age.isEmpty() || gender.isEmpty() || aadhar.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        // Save the updated data to Firestore
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.update("name", name, "age", age, "gender", gender, "aadhar", aadhar)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        setEditable(false);
                    } else {
                        Toast.makeText(ProfileActivity.this, "Error updating profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

//    @Override
//    protected int getBottomNavigationMenuItemId() {
//        return R.id.nav_profile; // Highlight Profile when in ProfileActivity
//    }
}
