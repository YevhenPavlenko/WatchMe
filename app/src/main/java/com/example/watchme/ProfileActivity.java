package com.example.watchme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.*;

import com.example.watchme.db.DatabaseHelper;

public class ProfileActivity extends BaseActivity {
    private DatabaseHelper dbHelper;
    private SharedPreferences prefs;
    private int clientId;
    private LinearLayout rentedMoviesLayout;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isUserLoggedIn()) {
            redirectToLogin();
            return;
        }

        setContentView(R.layout.profile_page);
        setupToolbar();

        dbHelper = new DatabaseHelper(this);
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> logoutFromAccount());

        rentedMoviesLayout = findViewById(R.id.rentedMoviesLayout);

        prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        clientId = prefs.getInt("client_id", -1);

        if (clientId == -1) {
            redirectToLogin();
            return;
        }

        setupProfileInfo();
        displayRentedMovies();
    }

    private void redirectToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void setupProfileInfo() {
        TextView tvLogin = findViewById(R.id.tvLogin);
        TextView tvRegDate = findViewById(R.id.tvRegDate);

        Cursor cursor = dbHelper.getClientById(clientId);
        if (cursor != null && cursor.moveToFirst()) {
            tvLogin.setText("Логін: " + cursor.getString(cursor.getColumnIndexOrThrow("login")));
            tvRegDate.setText("Дата реєстрації: " + cursor.getString(cursor.getColumnIndexOrThrow("registration_date")));
            cursor.close();
        }
    }

    private void logoutFromAccount() {
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void displayRentedMovies() {
        Cursor cursor = dbHelper.getRentalsByClientId(clientId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                createMovieItem(
                        cursor.getInt(cursor.getColumnIndexOrThrow("movie_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("poster_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("rental_date")),
                        cursor.getString(cursor.getColumnIndexOrThrow("return_date")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("is_returned")) == 1
                );
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void createMovieItem(int movieId, String title, String posterName, String rentalDate, String returnDate, boolean isReturned) {
        LinearLayout movieLayout = new LinearLayout(this);
        movieLayout.setOrientation(LinearLayout.HORIZONTAL);
        movieLayout.setPadding(0, 16, 0, 16);
        movieLayout.setGravity(Gravity.CENTER_VERTICAL);

        ImageView poster = createPosterView(posterName, movieId);
        LinearLayout infoLayout = createMovieInfoLayout(title, rentalDate, returnDate, isReturned, movieId);

        movieLayout.addView(poster);
        movieLayout.addView(infoLayout);

        rentedMoviesLayout.addView(movieLayout);
    }

    private ImageView createPosterView(String posterName, int movieId) {
        ImageView poster = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 350);
        params.setMargins(0, 0, 16, 0);
        poster.setLayoutParams(params);

        if (posterName != null) {
            poster.setImageResource(getResources().getIdentifier(posterName, "drawable", getPackageName()));
        } else {
            poster.setImageResource(R.drawable.placeholder);
        }

        poster.setOnClickListener(v -> openMovieDetails(movieId));
        return poster;
    }

    private LinearLayout createMovieInfoLayout(String title, String rentalDate, String returnDate, boolean isReturned, int movieId) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView infoText = new TextView(this);
        infoText.setText("«" + title + "» — орендовано: " + rentalDate);
        infoText.setTextSize(16);
        infoText.setPadding(0, 0, 0, 8);
        layout.addView(infoText);

        if (isReturned) {
            TextView returnedText = new TextView(this);
            returnedText.setText("Повернуто: " + returnDate);
            returnedText.setTextColor(Color.BLACK);
            layout.addView(returnedText);
        } else {
            Button returnButton = new Button(this);
            returnButton.setText("Повернути");
            returnButton.setOnClickListener(v -> {
                dbHelper.returnMovie(clientId, movieId);
                recreate();
            });
            layout.addView(returnButton);
        }

        return layout;
    }

    private void openMovieDetails(int movieId) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("movie_id", movieId);
        startActivity(intent);
    }
}
