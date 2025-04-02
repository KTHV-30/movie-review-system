package com.example.movie_review_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.movie_review_system.model.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MovieController {

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/movie_db";
        String username = "root";
        String password = "anand9791";
        return DriverManager.getConnection(url, username, password);
    }

    @GetMapping("/")
    public String index(Model model) throws SQLException {
        List<Movie> movies = getAllMovies();
        model.addAttribute("movies", movies);
        return "index";
    }

    private List<Movie> getAllMovies() throws SQLException {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM movies";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Movie movie = new Movie();
                movie.setId(rs.getInt("id"));
                movie.setTitle(rs.getString("title"));
                movie.setGenre(rs.getString("genre"));
                movie.setReleaseDate(rs.getDate("release_date"));
                movie.setDirector(rs.getString("director"));
                movie.setLanguage(rs.getString("language"));
                movie.setRating(rs.getDouble("rating"));
                movies.add(movie);
            }
        }
        return movies;
    }

    @GetMapping("/add")
    public String addForm() {
        return "add-movie";
    }

    @PostMapping("/add")
    public String addMovie(@RequestParam String title, @RequestParam String genre,
            @RequestParam String releaseDate, @RequestParam String director,
            @RequestParam String language, @RequestParam double rating) throws SQLException {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setGenre(genre);
        movie.setReleaseDate(Date.valueOf(releaseDate));
        movie.setDirector(director);
        movie.setLanguage(language);
        movie.setRating(rating);
        insertMovie(movie);
        return "redirect:/";
    }

    private void insertMovie(Movie movie) throws SQLException {
        String sql = "INSERT INTO movies (title, genre, release_date, director, language, rating) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, movie.getTitle());
            pstmt.setString(2, movie.getGenre());
            pstmt.setDate(3, movie.getReleaseDate());
            pstmt.setString(4, movie.getDirector());
            pstmt.setString(5, movie.getLanguage());
            pstmt.setDouble(6, movie.getRating());
            pstmt.executeUpdate();
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteMovie(@PathVariable int id) throws SQLException {
        deleteMovieById(id);
        return "redirect:/";
    }

    private void deleteMovieById(int id) throws SQLException {
        String sql = "DELETE FROM movies WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable int id, Model model) throws SQLException {
        Movie movie = getMovieById(id);
        if (movie != null) {
            model.addAttribute("movie", movie);
            return "update-movie";
        }
        return "redirect:/";
    }

    private Movie getMovieById(int id) throws SQLException {
        Movie movie = null;
        String sql = "SELECT * FROM movies WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    movie = new Movie();
                    movie.setId(rs.getInt("id"));
                    movie.setTitle(rs.getString("title"));
                    movie.setGenre(rs.getString("genre"));
                    movie.setReleaseDate(rs.getDate("release_date"));
                    movie.setDirector(rs.getString("director"));
                    movie.setLanguage(rs.getString("language"));
                    movie.setRating(rs.getDouble("rating"));
                }
            }
        }
        return movie;
    }

    @PostMapping("/update")
    public String updateMovie(@RequestParam int id, @RequestParam String title, @RequestParam String genre,
            @RequestParam String releaseDate, @RequestParam String director,
            @RequestParam String language, @RequestParam double rating) throws SQLException {
        Movie movie = new Movie();
        movie.setId(id);
        movie.setTitle(title);
        movie.setGenre(genre);
        movie.setReleaseDate(Date.valueOf(releaseDate));
        movie.setDirector(director);
        movie.setLanguage(language);
        movie.setRating(rating);
        updateMovie(movie);
        return "redirect:/";
    }

    private void updateMovie(Movie movie) throws SQLException {
        String sql = "UPDATE movies SET title = ?, genre = ?, release_date = ?, director = ?, language = ?, rating = ? WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, movie.getTitle());
            pstmt.setString(2, movie.getGenre());
            pstmt.setDate(3, movie.getReleaseDate());
            pstmt.setString(4, movie.getDirector());
            pstmt.setString(5, movie.getLanguage());
            pstmt.setDouble(6, movie.getRating());
            pstmt.setInt(7, movie.getId());
            pstmt.executeUpdate();
        }
    }

    @GetMapping("/search")
    public String searchMovie(@RequestParam(required = false) Integer id, Model model) throws SQLException {
        if (id == null) {
            return "redirect:/";
        }
        Movie movie = getMovieById(id);
        if (movie != null) {
            model.addAttribute("movie", movie);
        } else {
            model.addAttribute("message", "Movie not found");
        }
        return "search-result";
    }
}