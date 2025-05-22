package com.example.watchme;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.watchme.db.DatabaseHelper;

public class LoginActivity extends BaseActivity {

    private EditText etLogin, etPassword;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_form);

        dbHelper = new DatabaseHelper(this);

        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);

        TextView tvRegister = findViewById(R.id.tvRegister);
        TextView tvToMainPage = findViewById(R.id.tvToMainPage);

        btnLogin.setOnClickListener(v -> attemptLogin());

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        tvToMainPage.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });
    }

    private void attemptLogin() {
        String login = etLogin.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (TextUtils.isEmpty(login) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Будь ласка, заповніть усі поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!login.matches("^[a-zA-Z0-9_]{3,}$")) {
            Toast.makeText(this, "Логін може містити лише латинські літери, цифри та підкреслення (мін. 3 символи)", Toast.LENGTH_LONG).show();
            return;
        }

        if (password.length() < 4) {
            Toast.makeText(this, "Пароль має містити щонайменше 4 символи", Toast.LENGTH_SHORT).show();
            return;
        }

        int clientId = dbHelper.getClientId(login, password);
        if (clientId != -1) {
            getSharedPreferences("UserSession", MODE_PRIVATE)
                    .edit()
                    .putInt("client_id", clientId)
                    .putString("username", login)
                    .apply();

            Toast.makeText(this, "Успішний вхід", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Невірний логін або пароль", Toast.LENGTH_SHORT).show();
        }
    }

}
