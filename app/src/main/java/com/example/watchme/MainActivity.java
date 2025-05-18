package com.example.watchme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.watchme.db.DatabaseHelper;
import com.example.watchme.models.Category;
import com.example.watchme.models.Movie;

import java.io.IOException;
import java.util.List;

public class MainActivity extends BaseActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        setupToolbar();

        dbHelper = new DatabaseHelper(this);

        initDatabase();
        displayGenresAndMovies();
    }

    private void initDatabase() {
        try {
            dbHelper.createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Помилка ініціалізації бази даних", Toast.LENGTH_SHORT).show();
        }
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
            addMoviePostersToLayout(llMoviePosters, category.getMovies());

            layoutGenres.addView(genreView);
        }
    }

    private void addMoviePostersToLayout(LinearLayout container, List<Movie> movies) {
        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);

            ImageView poster = new ImageView(this);
            LinearLayout.LayoutParams posterParams = new LinearLayout.LayoutParams(340, 500);

            posterParams.setMargins(i > 0 ? 8 : 0, 0, 40, 0);

            poster.setLayoutParams(posterParams);
            poster.setScaleType(ImageView.ScaleType.CENTER_CROP);
            poster.setImageResource(getPosterResource(movie.posterName));

            container.addView(poster);
        }
    }

    private int getPosterResource(String posterName) {
        int resId = getResources().getIdentifier(posterName, "drawable", getPackageName());
        return resId != 0 ? resId : R.drawable.placeholder;
    }
}
