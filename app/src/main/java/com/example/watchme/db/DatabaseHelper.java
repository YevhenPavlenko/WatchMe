package com.example.watchme.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.watchme.models.Category;
import com.example.watchme.models.Movie;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
//            dbFile.delete();
            return;
        }
        this.getReadableDatabase().close();
        copyDatabase();
        insertTestUser();
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

//    public SQLiteDatabase openDatabase() {
//        return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
//    }

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

    private void insertTestUser() {
        if (!userExists("test")) {
            SQLiteDatabase db = this.getWritableDatabase();

            String login = "test";
            String passwordHash = md5("1234");
            String email = "test@example.com";
            String phone = "+380000000000";
            String registrationDate = "0000-00-00";

            ContentValues values = new ContentValues();
            values.put("login", login);
            values.put("password_hash", passwordHash);
            values.put("email", email);
            values.put("phone_number", phone);
            values.put("registration_date", registrationDate);

            long result = db.insert("Clients", null, values);
            if (result != -1) {
                Log.d("DB", "Тестового користувача додано успішно");
            } else {
                Log.e("DB", "Не вдалося додати тестового користувача");
            }
        } else {
            Log.d("DB", "Тестовий користувач вже існує");
        }
    }

    public boolean userExists(String login) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT client_id FROM Clients WHERE login = ?", new String[]{login});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public boolean registerUser(String login, String password) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            String passwordHash = md5(password);
            String registrationDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            ContentValues values = new ContentValues();
            values.put("login", login);
            values.put("password_hash", passwordHash);
            values.put("registration_date", registrationDate);

            long result = db.insert("Clients", null, values);
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public Cursor getClientById(int clientId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Clients WHERE client_id = ?", new String[]{String.valueOf(clientId)});
    }

    public int getClientId(String login, String password) {
        String password_hash = md5(password);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT client_id FROM Clients WHERE login = ? AND password_hash = ?", new String[]{login, password_hash});
        if (cursor.moveToFirst()) {
            int clientId = cursor.getInt(0);
            cursor.close();
            return clientId;
        } else {
            cursor.close();
            return -1;
        }
    }

    public Cursor getRentalsByClientId(int clientId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT Rentals.*, Movies.title, Movies.poster_name FROM Rentals " +
                        "JOIN Movies ON Rentals.movie_id = Movies.movie_id " +
                        "WHERE Rentals.client_id = ?", new String[]{String.valueOf(clientId)}
        );
    }

    public boolean isMovieRentedByClient(int clientId, int movieId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM Rentals WHERE client_id = ? AND movie_id = ? AND is_returned = 0",
                new String[]{String.valueOf(clientId), String.valueOf(movieId)});

        boolean rented = cursor.moveToFirst();
        cursor.close();
        return rented;
    }

    public void rentMovie(int clientId, int movieId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("client_id", clientId);
        values.put("movie_id", movieId);
        values.put("rental_date", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        values.put("is_returned", false);
        db.insert("Rentals", null, values);
    }

    public void returnMovie(int clientId, int movieId) {
        SQLiteDatabase db = getWritableDatabase();
        String return_date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        db.execSQL(
                "UPDATE Rentals SET is_returned = 1, return_date = ? " +
                        "WHERE client_id = ? AND movie_id = ? AND is_returned = 0",
                new Object[]{return_date, clientId, movieId});
    }
}