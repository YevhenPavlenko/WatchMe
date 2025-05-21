package com.example.watchme;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.watchme.db.DatabaseHelper;

public class RegisterActivity extends BaseActivity {

    private EditText etLogin, etPassword, etRepeatPassword, etBirthday;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_form);

        dbHelper = new DatabaseHelper(this);

        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        etRepeatPassword = findViewById(R.id.etRepeatPassword);
        etBirthday = findViewById(R.id.etBirthday);
        Button btnRegister = findViewById(R.id.btnRegister);

        TextView tvAccountExists = findViewById(R.id.tvAccountExists);
        TextView tvToMainPage = findViewById(R.id.tvToMainPage);

        tvAccountExists.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        tvToMainPage.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String login = etLogin.getText().toString().trim();
        String password = etPassword.getText().toString();
        String repeatPassword = etRepeatPassword.getText().toString();
        String birthday = etBirthday.getText().toString();

        if (TextUtils.isEmpty(login) || TextUtils.isEmpty(password) || TextUtils.isEmpty(repeatPassword)) {
            Toast.makeText(this, "Будь ласка, заповніть усі обов'язкові поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!login.matches("^[a-zA-Z0-9_]{3,}$")) {
            Toast.makeText(this, "Логін має містити щонайменше 3 символи: лише латинські літери, цифри або підкреслення", Toast.LENGTH_LONG).show();
            return;
        }

        if (password.length() < 4) {
            Toast.makeText(this, "Пароль має бути щонайменше 4 символи", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(repeatPassword)) {
            Toast.makeText(this, "Паролі не співпадають", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.userExists(login)) {
            Toast.makeText(this, "Користувач з таким логіном вже існує", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = dbHelper.registerUser(login, password);
        if (success) {
            Toast.makeText(this, "Реєстрація успішна", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Помилка при реєстрації. Спробуйте ще раз", Toast.LENGTH_SHORT).show();
        }
    }
}
