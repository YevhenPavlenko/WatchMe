package com.example.watchme;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_form);

        TextView tvRegister = findViewById(R.id.tvRegister);
        TextView tvToMainPage = findViewById(R.id.tvToMainPage);

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        tvToMainPage.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });
    }
}
