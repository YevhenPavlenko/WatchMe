package com.example.watchme;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager2.widget.ViewPager2;

import com.example.watchme.adapters.BannerAdapter;
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
        setupBannerSlider();
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

    private void setupBannerSlider() {
        Handler sliderHandler = new Handler(Looper.getMainLooper());
        Runnable sliderRunnable;
        int delay = 4000;
        ViewPager2 viewPager = findViewById(R.id.viewPagerBanner);
        List<Movie> bannerMovies = dbHelper.getFeaturedMovies();

        BannerAdapter adapter = new BannerAdapter(this, bannerMovies);
        viewPager.setAdapter(adapter);

        int startPos = Integer.MAX_VALUE / 2;
        startPos -= startPos % bannerMovies.size();
        viewPager.setCurrentItem(startPos, false);

        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                sliderHandler.postDelayed(this, delay);
            }
        };
        sliderHandler.postDelayed(sliderRunnable, delay);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, delay);
            }
        });
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

            poster.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
                intent.putExtra("movie_id", movie.movieId);
                startActivity(intent);
            });

            container.addView(poster);
        }
    }
}
