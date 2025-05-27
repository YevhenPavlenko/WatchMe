package com.example.watchme.models;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private String name;
    private List<Movie> movies;

    public Category(String name, List<Movie> movies) {
        this.name = name;
        this.movies = movies;
    }

    public Category(String name) {
        this.name = name;
        movies = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void addMovie(Movie movie) {
        movies.add(movie);
    }
}