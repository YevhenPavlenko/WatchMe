package com.example.watchme.models;

public class Movie {
    public int movieId;
    public String title;
    public String description;
    public String genre;
    public int releaseYear;
    public float rating;
    public String posterName;
    public String directorName;
    public String actors;
    public String bannerName;

    public Movie() {}

    public Movie(int movieId, String title, String genre, String description,
                 int releaseYear, float rating, String posterName,
                 String directorName, String actors, String bannerName) {
        this.movieId = movieId;
        this.title = title;
        this.genre = genre;
        this.description = description;
        this.releaseYear = releaseYear;
        this.rating = rating;
        this.posterName = posterName;
        this.directorName = directorName;
        this.actors = actors;
        this.bannerName = bannerName;
    }
}