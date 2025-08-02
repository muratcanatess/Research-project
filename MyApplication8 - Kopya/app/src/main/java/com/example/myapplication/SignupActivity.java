package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    EditText editTextNewUsername, editTextNewPassword;
    Button buttonSignup;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextNewUsername = findViewById(R.id.editTextNewUsername);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        buttonSignup = findViewById(R.id.buttonSignup);

        db = new Database(this);

        buttonSignup.setOnClickListener(v -> {
            String username = editTextNewUsername.getText().toString().trim();
            String password = editTextNewPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.checkUsernameExists(username)) {
                Toast.makeText(this, "Username already exists!", Toast.LENGTH_SHORT).show();
            } else {
                boolean inserted = db.insertUser(username, password);
                if (inserted) {
                    Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Signup failed. Try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}