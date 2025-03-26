package org.scrollify.database;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.scrollify.backend.User;
import org.scrollify.enums.UserEnum;

public class AuthenticatorDB {
    private static final DatabaseManager dbManager = new DatabaseManager();

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static boolean isUsernameTaken(String username) {
        String query = "SELECT * FROM users WHERE username = ?;";
        try (PreparedStatement pstmt = dbManager.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean isCustomisableIDTaken(String customisableID) {
        String query = "SELECT * FROM users WHERE customisable_id = ?;";
        try (PreparedStatement pstmt = dbManager.prepareStatement(query)) {
            pstmt.setString(1, customisableID);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean isCustomisableIDTakenByOther(String customisableID, String currentUsername) {
        String query = "SELECT * FROM users WHERE customisable_id = ? AND username != ?;";
        try (PreparedStatement pstmt = dbManager.prepareStatement(query)) {
            pstmt.setString(1, customisableID);
            pstmt.setString(2, currentUsername);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean signupNewUser(String firstName, String lastName, String email, String phone, String username, String password, String customisable_id) {
        String hashedPassword = hashPassword(password);
        String query = """
                INSERT INTO users (username, password, first_name, last_name, email, phone, customisable_id, isAdmin)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?);
                """;
        try (PreparedStatement pstmt = dbManager.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, firstName);
            pstmt.setString(4, lastName);
            pstmt.setString(5, email);
            pstmt.setString(6, phone);
            pstmt.setString(7, customisable_id);
            pstmt.setBoolean(8, false);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean createSuperAdmin() {
        String query = """
                INSERT INTO users (username, password, first_name, last_name, email, phone, customisable_id, isAdmin)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?);
                """;
        if (isUsernameTaken("admin") || isCustomisableIDTaken("admin")) {
            return true;
        }
        try (PreparedStatement pstmt = dbManager.prepareStatement(query)) {
            pstmt.setString(1, "admin");
            pstmt.setString(2, hashPassword("admin123"));
            pstmt.setString(3, "Scrollify");
            pstmt.setString(4, "Admin");
            pstmt.setString(5, "admin@scrollify.org");
            pstmt.setString(6, "0123987465");
            pstmt.setString(7, "admin");
            pstmt.setBoolean(8, true);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static User loginUser(String username, String password) {
        String hashedPassword = hashPassword(password);
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (PreparedStatement pstmt = dbManager.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getString("username"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("customisable_id"),
                        rs.getBoolean("isAdmin") // Set UserEnum based on isAdmin
                );
            } else {
                // User not found
                System.out.println("Invalid username or password.");

            }
        } catch (SQLException e) {
            System.out.println("Error logging in: " + e.getMessage());
        }
        return null; // Return null if login fails
    }

    public static boolean updateUserProfile(String firstName, String lastName, String email, String phone, String username, String password, String customisableID) {
        String query = """
            UPDATE users 
            SET first_name = ?, last_name = ?, email = ?, phone = ?, password = ?, customisable_id = ?
            WHERE username = ?;
        """;
        try (PreparedStatement pstmt = dbManager.prepareStatement(query)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            pstmt.setString(5, hashPassword(password));
            pstmt.setString(6, customisableID);
            pstmt.setString(7, username);
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println("Error updating profile: " + e.getMessage());
            return false;
        }
    }

    public static String getPasswordForUser(String username) {
        String query = "SELECT password FROM users WHERE username = ?";
        try (PreparedStatement pstmt = dbManager.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving password: " + e.getMessage());
        }
        return null;
    }

    public static boolean verifyPassword(String inputPassword, String storedHashedPassword) {
        String hashedInputPassword = hashPassword(inputPassword);
        return hashedInputPassword.equals(storedHashedPassword);
    }
}
