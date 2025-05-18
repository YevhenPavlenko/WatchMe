package com.example.watchme;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public abstract class BaseActivity extends AppCompatActivity {

    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (this instanceof MainActivity) {
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                if (backToast != null) backToast.cancel();
                super.onBackPressed();
            } else {
                backToast = Toast.makeText(getBaseContext(), "Натисніть ще раз, щоб вийти", Toast.LENGTH_SHORT);
                backToast.show();
                backPressedTime = System.currentTimeMillis();
            }
        } else {
            super.onBackPressed();
        }
    }

    protected void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        if (toolbar == null) return;

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        ImageButton btnLogo = findViewById(R.id.btnLogo);
        ImageButton btnSearch = findViewById(R.id.btnSearch);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogo.setOnClickListener(v -> {
            if (this instanceof MainActivity) {
                finish();
                startActivity(getIntent());
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        btnSearch.setOnClickListener(v -> {
            Toast.makeText(this, "Search clicked", Toast.LENGTH_SHORT).show();
        });

        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
    }
}