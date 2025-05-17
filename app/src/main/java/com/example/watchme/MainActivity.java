package com.example.watchme;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.watchme.db.DatabaseHelper;
import com.example.watchme.models.Category;
import com.example.watchme.models.Movie;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        dbHelper = new DatabaseHelper(this);

        try {
            dbHelper.createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Помилка ініціалізації бази даних", Toast.LENGTH_SHORT).show();
            return;
        }

        loadToolbar();
        displayGenresAndMovies();
    }

    private void loadToolbar() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        ImageButton btnLogo = findViewById(R.id.btnLogo);
        ImageButton btnSearch = findViewById(R.id.btnSearch);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogo.setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class)));

        btnSearch.setOnClickListener(v ->
                Toast.makeText(this, "Пошук недоступний", Toast.LENGTH_SHORT).show());

        btnLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));
    }

    private void displayGenresAndMovies() {
        LinearLayout layoutGenres = findViewById(R.id.layoutGenres);
        layoutGenres.removeAllViews();

        List<Category> categories = dbHelper.getAllCategoriesWithMovies();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Category category : categories) {
            View genreView = inflater.inflate(R.layout.genre_section_item, layoutGenres, false);

            TextView tvGenreName = genreView.findViewById(R.id.tvGenreName);
            tvGenreName.setText(category.getName());

            LinearLayout llMoviePosters = genreView.findViewById(R.id.llMoviePosters);

            for (int i = 0; i < category.getMovies().size(); i++) {
                Movie movie = category.getMovies().get(i);

                ImageView poster = new ImageView(this);
                LinearLayout.LayoutParams posterParams = new LinearLayout.LayoutParams(340, 500);

                if (i > 0) {
                    posterParams.setMargins(8, 0, 40, 0);
                } else {
                    posterParams.setMargins(0, 0, 40, 0);
                }

                poster.setLayoutParams(posterParams);
                poster.setScaleType(ImageView.ScaleType.CENTER_CROP);

                int resId = getResources().getIdentifier(movie.posterName, "drawable", getPackageName());
                if (resId != 0) {
                    poster.setImageResource(resId);
                } else {
                    poster.setImageResource(R.drawable.placeholder);
                }

                llMoviePosters.addView(poster);
            }

            layoutGenres.addView(genreView);
        }
    }
}
