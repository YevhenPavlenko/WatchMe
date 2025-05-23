package com.example.watchme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) return;

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        ImageButton btnLogo = findViewById(R.id.btnLogo);
        ImageButton btnSearch = findViewById(R.id.btnSearch);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogo.setOnClickListener(v -> {
            if (this instanceof MainActivity) {
                recreate();
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

        if (isUserLoggedIn()) {
            btnLogin.setText("Профіль");
            btnLogin.setOnClickListener(v -> {
                if (this instanceof ProfileActivity) {
                    recreate();
                } else {
                    Intent intent = new Intent(this, ProfileActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            btnLogin.setText("Увійти");
            btnLogin.setOnClickListener(v -> {
                startActivity(new Intent(this, LoginActivity.class));
            });
        }
    }

    protected boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        return prefs.contains("client_id");
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public int getPosterResource(String posterName) {
        int resId = getResources().getIdentifier(posterName, "drawable", getPackageName());
        return resId != 0 ? resId : R.drawable.placeholder;
    }
}