package com.example.watchme;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.watchme.db.DatabaseHelper;
import com.example.watchme.models.Movie;

public class MovieDetailsActivity extends BaseActivity {
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_page);

        Log.d("DEBUG", "Before toolbar setup");
        setupToolbar();
        Log.d("DEBUG", "After toolbar setup");

        dbHelper = new DatabaseHelper(this);

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

        ivPoster.setImageResource(getResources().getIdentifier(movie.posterName, "drawable", getPackageName()));

        String info = movie.title + "\n\n"
                + "Жанр: " + movie.genre + "\n"
                + "Рік: " + movie.releaseYear + "\n"
                + "Рейтинг: " + movie.rating + "\n"
                + "Режисер: " + movie.directorName + "\n"
                + "Головна роль: " + movie.actors;

        tvCharacteristics.setText(info);
        tvDescription.setText(movie.description);
    }
}
