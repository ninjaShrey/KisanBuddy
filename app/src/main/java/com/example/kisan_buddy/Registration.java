package com.example.kisan_buddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

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

        String role = selectedRadioButton != null ? selectedRadioButton.getText().toString() : "Unknown";

        // Use Firebase Auth to register the user
        // Example:
         FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
             .addOnCompleteListener(this, task -> {
                 if (task.isSuccessful()) {
                     // Registration successful, handle success
                     Toast.makeText(this, "Registered successfully as " + role, Toast.LENGTH_SHORT).show();
                     // Redirect to dashboard
                 } else {
                     // Handle registration failure
                     Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                 }
             });
    }
}
