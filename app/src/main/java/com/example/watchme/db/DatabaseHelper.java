package com.example.watchme.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.watchme.models.Category;
import com.example.watchme.models.Movie;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "movies.db";
    private static final int DB_VERSION = 1;
    private final Context CONTEXT;
    private final String DB_PATH;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.CONTEXT = context;
        this.DB_PATH = context.getDatabasePath(DB_NAME).getPath();
    }

    public void createDatabase() throws IOException {
        File dbFile = new File(DB_PATH);
        if (dbFile.exists()) {
            dbFile.delete();
        }
        this.getReadableDatabase().close();
        copyDatabase();
    }


    private boolean checkDatabase() {
        File dbFile = new File(DB_PATH);
        return dbFile.exists();
    }

    private void copyDatabase() throws IOException {
        InputStream is = CONTEXT.getAssets().open(DB_NAME);
        File outFile = new File(DB_PATH);
        outFile.getParentFile().mkdirs();
        OutputStream os = new FileOutputStream(outFile);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }

        os.flush();
        os.close();
        is.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public SQLiteDatabase openDatabase() {
        return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public List<Category> getAllCategoriesWithMovies() {
        List<Category> categoryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT genre FROM Movies", null);
        if (cursor.moveToFirst()) {
            do {
                String genre = cursor.getString(0);
                List<Movie> movies = getMoviesByGenre(genre);
                categoryList.add(new Category(genre, movies));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categoryList;
    }

    private List<Movie> getMoviesByGenre(String genre) {
        List<Movie> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT movie_id, title, poster_name FROM Movies WHERE genre = ?", new String[]{genre});
        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie();
                movie.movieId = cursor.getInt(0);
                movie.title = cursor.getString(1);
                movie.posterName = cursor.getString(2);
                list.add(movie);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Movie getMovieDetails(int movieId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT m.*, d.person_id AS director_person_id, p.full_name AS director_name " +
                "FROM Movies m " +
                "JOIN Directors d ON m.director_id = d.director_id " +
                "JOIN Persons p ON d.person_id = p.person_id " +
                "WHERE m.movie_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(movieId)});

        Movie movie = null;

        if (cursor.moveToFirst()) {
            movie = new Movie();
            movie.movieId = cursor.getInt(cursor.getColumnIndexOrThrow("movie_id"));
            movie.title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            movie.description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            movie.genre = cursor.getString(cursor.getColumnIndexOrThrow("genre"));
            movie.releaseYear = cursor.getInt(cursor.getColumnIndexOrThrow("release_year"));
            movie.rating = cursor.getFloat(cursor.getColumnIndexOrThrow("rating"));
            movie.posterName = cursor.getString(cursor.getColumnIndexOrThrow("poster_name"));
            movie.directorName = cursor.getString(cursor.getColumnIndexOrThrow("director_name"));

            movie.actors = getActorsForMovie(movieId);
        }

        cursor.close();
        return movie;
    }

    private String getActorsForMovie(int movieId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT p.full_name FROM MovieActors ma " +
                "JOIN Actors a ON ma.actor_id = a.actor_id " +
                "JOIN Persons p ON a.person_id = p.person_id " +
                "WHERE ma.movie_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(movieId)});

        StringBuilder builder = new StringBuilder();
        while (cursor.moveToNext()) {
            if (builder.length() > 0) builder.append(", ");
            builder.append(cursor.getString(0));
        }

        cursor.close();
        return builder.toString();
    }

}