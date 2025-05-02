package com.example.watchme;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_form);

        TextView tvAccountExists = findViewById(R.id.tvAccountExists);
        TextView tvToMainPage = findViewById(R.id.tvToMainPage);

        tvAccountExists.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        tvToMainPage.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });
    }
}