package com.example.watchme;

import org.junit.Test;
import static org.junit.Assert.*;

import com.example.watchme.models.Category;
import com.example.watchme.models.Movie;

public class ExampleUnitTest {

    @Test
    public void testMovieModelData() {
        Movie movie = new Movie(
                1,
                "The Matrix",
                "Action",
                "Neo discovers the truth",
                1999,
                8.7f,
                "matrix.jpg",
                "Wachowskis",
                "Keanu Reeves",
                "matrix_b.jpg"
        );

        assertEquals("The Matrix", movie.title);
        assertEquals("Action", movie.genre);
        assertEquals("Neo discovers the truth", movie.description);
        assertEquals(1999, movie.releaseYear);
        assertEquals(8.7f, movie.rating, 0.01);
        assertEquals("matrix.jpg", movie.posterName);
        assertEquals("Wachowskis", movie.directorName);
        assertEquals("Keanu Reeves", movie.actors);
    }

    @Test
    public void testAddMovieToCategory() {
        Category category = new Category("Action");
        Movie movie = new Movie(2, "John Wick", "Action", "Revenge thriller", 2014, 7.4f, "wick.jpg", "Stahelski", "Keanu Reeves", "wick_b.jpg");
        category.addMovie(movie);

        assertEquals(1, category.getMovies().size());
        assertEquals("John Wick", category.getMovies().get(0).title);
    }

    @Test
    public void testEmptyCategory() {
        Category category = new Category("Drama");
        assertEquals("Drama", category.getName());
        assertTrue(category.getMovies().isEmpty());
    }

    @Test
    public void testAddMultipleMoviesToCategory() {
        Category category = new Category("Sci-Fi");
        category.addMovie(new Movie(1, "Interstellar", "Sci-Fi", "Space travel", 2014, 8.6f, "interstellar.jpg", "Nolan", "McConaughey", "interstellar_b.jpg"));
        category.addMovie(new Movie(2, "Inception", "Sci-Fi", "Dreams within dreams", 2010, 8.8f, "inception.jpg", "Nolan", "DiCaprio", "inception_b.jpg"));

        assertEquals(2, category.getMovies().size());
        assertEquals("Inception", category.getMovies().get(1).title);
    }
}