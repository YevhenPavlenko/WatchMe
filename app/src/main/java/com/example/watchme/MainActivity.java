package com.example.watchme;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.watchme.db.DatabaseHelper;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
//    private DatabaseHelper dbHelper;
//    private SQLiteDatabase db;
//
//    private ImageView[] posterViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

//        dbHelper = new DatabaseHelper(this);
//        db = dbHelper.getReadableDatabase();
//
//        posterViews = new ImageView[] {
//                findViewById(R.id.ivFilmPoster1),
//                findViewById(R.id.ivFilmPoster2),
//                findViewById(R.id.ivFilmPoster3),
//                findViewById(R.id.ivFilmPoster4),
//                findViewById(R.id.ivFilmPoster5),
//                findViewById(R.id.ivFilmPoster6)
//        };

//        loadMoviePosters();

        loadToolbar();
    }

//    private void loadMoviePosters() {
//        Cursor cursor = db.rawQuery("SELECT poster_name FROM Movies LIMIT 6", null);
//        int i = 0;
//
//        while (cursor.moveToNext() && i < posterViews.length) {
//            String posterName = cursor.getString(cursor.getColumnIndexOrThrow("poster_name"));
//            int imageResId = getResources().getIdentifier(posterName, "drawable", getPackageName());
//            posterViews[i].setImageResource(imageResId);
//            i++;
//        }
//
//        cursor.close();
//    }

    private void loadToolbar() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        ImageButton btnLogo = findViewById(R.id.btnLogo);
        ImageButton btnSearch = findViewById(R.id.btnSearch);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogo.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });

        btnSearch.setOnClickListener(v -> {
            Toast.makeText(this, "Search clicked", Toast.LENGTH_SHORT).show();
        });

        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
    }
}
