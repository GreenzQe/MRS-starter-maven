package dk.easv.mrs.DAL.db;

import dk.easv.mrs.BE.Movie;
import dk.easv.mrs.DAL.IMovieDataAccess;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO_DB implements IMovieDataAccess {

    private DBConnector dbConnector = new DBConnector();

    public MovieDAO_DB() throws IOException {
    }

    @Override
    public List<Movie> getAllMovies() throws Exception {

        ArrayList<Movie> allMovies = new ArrayList<>();

        try (Connection conn = dbConnector.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "SELECT * FROM dbo.Movie;";
            ResultSet rs = stmt.executeQuery(sql);

            // Loop through rows from the database result set
            while (rs.next()) {

                //Map DB row to Movie object
                int id = rs.getInt("Id");
                String title = rs.getString("Title");
                int year = rs.getInt("year");

                Movie movie = new Movie(id, year, title);
                allMovies.add(movie);
            }
            return allMovies;

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new Exception("Could not get movies from database", ex);
        }
    }

    @Override
    public Movie createMovie(Movie movie) throws Exception {
        String sql = "INSERT INTO dbo.Movie (Title,Year) VALUES (?,?);";
        DBConnector dbConnector = new DBConnector();


        try (Connection conn = dbConnector.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Bind parameters
            stmt.setString(1, movie.getTitle());
            stmt.setInt(2, movie.getYear());

            // Run the specified SQL statement
            stmt.executeUpdate();

            // Get the generated ID from the DB
            ResultSet rs = stmt.getGeneratedKeys();
            int id = 0;

            if (rs.next()) {
                id = rs.getInt(1);
            }

            // Create movie object and send up the layers
            Movie createdMovie = new Movie(id, movie.getYear(), movie.getTitle());

            return createdMovie;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new Exception("Could not create movie", ex);
        }
    }

    @Override
    public void updateMovie(Movie movie) throws Exception {
// SQL command
        String sql = "UPDATE dbo.Movie SET Title = ?, Year = ? WHERE ID = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Bind parameters
            stmt.setString(1, movie.getTitle());
            stmt.setInt(2, movie.getYear());
            stmt.setInt(3, movie.getId());

            // Run the specified SQL statement
            stmt.executeUpdate();
        } catch (SQLException ex) {
            // fixme: optionally log to file, db etc.
            throw new Exception("Could not get movies from database.", ex);
        }
    }

    @Override
    public void deleteMovie(Movie movie) throws Exception {
        // SQL command
        String sql = "DELETE FROM dbo.Movie WHERE ID = ?;";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Bind parameters
            stmt.setInt(1, movie.getId());

            // Run the specified SQL statement
            stmt.executeUpdate();
        } catch (SQLException ex) {
            // fixme: optionally log to file, db etc.
            throw new Exception("Could not get movies from database.", ex);
        }
    }
}