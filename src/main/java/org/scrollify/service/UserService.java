package org.scrollify.service;

import org.scrollify.backend.User;
import org.scrollify.database.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    private final DatabaseManager dbManager;

    public UserService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    // Fetch all users from the database
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users WHERE isAdmin = false";

        try (ResultSet rs = dbManager.executeQuery(query)) {
            while (rs.next()) {
                // Create a User object with all the required fields
                User user = new User(
                        rs.getString("username"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("customisable_id"),
                        rs.getBoolean("isAdmin") // Pass isAdmin directly to User
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching users: " + e.getMessage());
        }

        return users;
    }


    // Delete a user from the database
    public boolean deleteUser(User user) {
        String deleteQuery = "DELETE FROM users WHERE username = ?";

        try {
            int rowsAffected = dbManager.executeUpdate(deleteQuery, user.getUsername());

            return rowsAffected > 0; // Return true if a row was deleted
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
}
