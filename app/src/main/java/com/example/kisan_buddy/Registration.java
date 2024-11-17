package com.example.kisan_buddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;

public class Registration extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private RadioGroup userRoleRadioGroup;
    private Button registerButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        userRoleRadioGroup = findViewById(R.id.userRoleRadioGroup);
        registerButton = findViewById(R.id.registerButton);
        loginButton = findViewById(R.id.loginButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Registration.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void registerUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        int selectedId = userRoleRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedId);

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedRadioButton == null) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }

        String role = selectedRadioButton.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("Registration", "Firebase Auth Success");
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String userId = user != null ? user.getUid() : "";

                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                        User userObj = new User(email, role);

                        firestore.collection("users").document(userId)
                                .set(userObj)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Registered successfully as " + role, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Registration.this, Login.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Registration", "Firestore Error: " + e.getMessage());
                                    Toast.makeText(this, "Error storing user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Log.e("Registration", "Firebase Auth Error: " + task.getException().getMessage());
                        Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // User data model class
    public static class User {
        private String email;
        private String userType;

        public User() { }

        public User(String email, String userType) {
            this.email = email;
            this.userType = userType;
        }

        public String getEmail() {
            return email;
        }

        public String getUserType() {
            return userType;
        }
    }
}
