package com.example.watchme;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.watchme.db.DatabaseHelper;
import com.example.watchme.models.Movie;

public class MovieDetailsActivity extends BaseActivity {
    private DatabaseHelper dbHelper;
    private int movieId;
    private Button btnRent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_page);
        setupToolbar();

        movieId = getIntent().getIntExtra("movie_id", -1);
        dbHelper = new DatabaseHelper(this);

        btnRent = findViewById(R.id.btnRentMovie);
        btnRent.setOnClickListener(v -> handleRent());

        int movieId = getIntent().getIntExtra("movie_id", -1);
        if (movieId != -1) {
            Movie movie = dbHelper.getMovieDetails(movieId);
            if (movie != null) {
                displayMovieInfo(movie);
            }
        }
    }

    private void displayMovieInfo(Movie movie) {
        ImageView ivPoster = findViewById(R.id.ivPoster);
        TextView tvCharacteristics = findViewById(R.id.tvCharacteristics);
        TextView tvDescription = findViewById(R.id.tvDescription);

        ivPoster.setImageResource(getPosterResource(movie.posterName));

        String info = movie.title + "\n\n"
                + "Жанр: " + movie.genre + "\n"
                + "Рік: " + movie.releaseYear + "\n"
                + "Рейтинг: " + movie.rating + "\n"
                + "Режисер: " + movie.directorName + "\n"
                + "Головна роль: " + movie.actors;

        tvCharacteristics.setText(info);
        tvDescription.setText(movie.description);

        handleRent();
    }

    private void handleRent() {
        if (!isUserLoggedIn()) {
            btnRent.setText("Авторизуйтесь щоб орендувати");
            btnRent.setOnClickListener(v -> {
                startActivity(new Intent(this, LoginActivity.class));
            });
        } else {
            int clientId = getSharedPreferences("UserSession", MODE_PRIVATE).getInt("client_id", -1);
            boolean isRented = dbHelper.isMovieRentedByClient(clientId, movieId);

            if (isRented) {
                btnRent.setText("Повернути фільм");
                btnRent.setOnClickListener(v -> {
                    dbHelper.returnMovie(clientId, movieId);
                    Toast.makeText(this, "Фільм повернуто", Toast.LENGTH_SHORT).show();
                    recreate();
                });
            } else {
                btnRent.setText("Орендувати фільм");
                btnRent.setOnClickListener(v -> {
                    dbHelper.rentMovie(clientId, movieId);
                    Toast.makeText(this, "Фільм орендовано", Toast.LENGTH_SHORT).show();
                    recreate();
                });
            }
        }
    }
}
