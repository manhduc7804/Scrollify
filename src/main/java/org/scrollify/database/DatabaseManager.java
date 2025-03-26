package org.scrollify.database;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.scrollify.model.Scroll;
import org.scrollify.service.ScrollService;
import org.scrollify.backend.User;
import org.scrollify.enums.UserEnum;
import java.time.LocalDateTime;

public class DatabaseManager {
    private static final String BASE_URL = "jdbc:postgresql://localhost:5432/"; // Base URL
    private static final String USER = "superadmin"; // PostgreSQL user
    private static final String PASSWORD = "scrollifyvsas"; // PostgreSQL password
    private static final String DB_NAME = "scrollify"; // Database name

    public void initialiseTables() {
        createUsersTable();
        createScrollsTable();
        updateFeaturedScroll();
        createDownloadTable();
    }

    public static void createDownloadTable() {
        String checkColumnQuery = "SELECT * FROM information_schema.columns WHERE table_name = 'scrolls' AND column_name = 'downloads'";
        String addColumnQuery = "ALTER TABLE scrolls ADD COLUMN downloads INT DEFAULT 0";
        String initializeDownloadsQuery = "UPDATE scrolls SET downloads = 0 WHERE downloads IS NULL";

        try (Connection conn = new DatabaseManager().connectToDatabase();
             Statement stmt = conn.createStatement()) {

            // Check if the downloads column exists
            ResultSet rs = stmt.executeQuery(checkColumnQuery);
            if (!rs.next()) {
                // If the downloads column does not exist, add it
                stmt.executeUpdate(addColumnQuery);
                System.out.println("Downloads column added to scrolls table.");
            }

            // Ensure all existing records have downloads set to 0
            stmt.executeUpdate(initializeDownloadsQuery);
            System.out.println("Downloads column initialized to 0 for all existing records.");

        } catch (SQLException e) {
            System.out.println("Error initializing downloads column: " + e.getMessage());
        }
    }

    private void createFeaturedScrollTable() {
        String query = """
            CREATE TABLE IF NOT EXISTS featured_scroll (
                scrollid VARCHAR(32) NOT NULL,
                version INT NOT NULL,
                datechosen DATE NOT NULL,
                PRIMARY KEY (scrollid, version),  -- Make scrollid and version the primary key
                CONSTRAINT fk_scroll 
                    FOREIGN KEY (scrollid, version) 
                    REFERENCES scrolls(scrollid, version)
                    ON DELETE CASCADE
            );
            """;
    
        try (Connection conn = connectToDatabase();
             Statement stmt = conn.createStatement()) {
            stmt.execute(query);
            System.out.println("Featured Scroll table created successfully!");
        } catch (SQLException e) {
            System.out.println("Error creating featured scroll table: " + e.getMessage());
        }
    }
    
    
    public void insertOrUpdateFeaturedScroll(Scroll scroll, LocalDateTime today) {
        String deleteQuery = "DELETE FROM featured_scroll";
        String insertQuery = """
                INSERT INTO featured_scroll (scrollid, version, datechosen) 
                VALUES (?, ?, ?);
                """;
    
        try (Connection conn = connectToDatabase()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(deleteQuery);
            }
            
            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                pstmt.setString(1, scroll.getScrollId());
                pstmt.setInt(2, scroll.getVersion());
                pstmt.setTimestamp(3, java.sql.Timestamp.valueOf(today));
    
                int rowsAffected = pstmt.executeUpdate();
                System.out.println("Featured scroll updated. Rows affected: " + rowsAffected);
            }
        } catch (SQLException e) {
            System.out.println("Error updating featured scroll: " + e.getMessage());
        }
    }

    public void updateFeaturedScroll() {
        createFeaturedScrollTable();
    
        String selectFeaturedScrollQuery = "SELECT * FROM featured_scroll LIMIT 1";
        LocalDateTime today = LocalDateTime.now();
    
        try (Connection conn = connectToDatabase();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectFeaturedScrollQuery)) {
    
            if (rs.next()) {
                LocalDateTime dateChosen = rs.getTimestamp("datechosen").toLocalDateTime();
                if (!today.toLocalDate().equals(dateChosen.toLocalDate())) {
                    Scroll randomScroll = getRandomScroll();
                    if (randomScroll != null) {
                        insertOrUpdateFeaturedScroll(randomScroll, today);
                    }
                } else {
                    System.out.println("Featured scroll is already selected for today.");
                }
            } else {
                Scroll randomScroll = getRandomScroll();
                if (randomScroll != null) {
                    insertOrUpdateFeaturedScroll(randomScroll, today);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error selecting featured scroll: " + e.getMessage());
        }
    }

    public Scroll getFeaturedScroll() {
        String selectFeaturedScrollQuery = """
                SELECT s.scrollid, s.scrollname, s.dateadded, s.owner, s.version
                FROM featured_scroll f
                JOIN scrolls s ON f.scrollid = s.scrollid AND f.version = s.version
                LIMIT 1;
                """;
    
        try (Connection conn = connectToDatabase();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectFeaturedScrollQuery)) {
    
            if (rs.next()) {
                return new Scroll(
                    rs.getString("scrollid"),
                    rs.getString("scrollname"),
                    rs.getTimestamp("dateadded").toLocalDateTime(),
                    rs.getString("owner"),
                    rs.getInt("version")
                );
            } else {
                System.out.println("No featured scroll found.");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching featured scroll: " + e.getMessage());
        }
    
        return null;
    }
    
    
    
    public Scroll getRandomScroll() {
        String randomScrollQuery = """
                SELECT s.*
                FROM scrolls s
                INNER JOIN (
                    SELECT scrollid, MAX(version) as latest_version
                    FROM scrolls
                    GROUP BY scrollid
                ) latest_versions
                ON s.scrollid = latest_versions.scrollid AND s.version = latest_versions.latest_version
                ORDER BY RANDOM() LIMIT 1;
                """;
    
        try (Connection conn = connectToDatabase();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(randomScrollQuery)) {
    
            if (rs.next()) {
                return new Scroll(
                    rs.getString("scrollid"),
                    rs.getString("scrollname"),
                    rs.getTimestamp("dateadded").toLocalDateTime(),
                    rs.getString("owner"),
                    rs.getInt("version")  // Now we include the version
                );
            }
        } catch (SQLException e) {
            System.out.println("Error fetching random scroll: " + e.getMessage());
        }
        return null;
    }

    public static List<Scroll> getScrolls() {
        List<Scroll> scrolls = new ArrayList<>();
        String query = """
                SELECT s.*
                FROM scrolls s
                INNER JOIN (
                    SELECT scrollid, MAX(version) as latest_version
                    FROM scrolls
                    GROUP BY scrollid
                ) latest_versions
                ON s.scrollid = latest_versions.scrollid AND s.version = latest_versions.latest_version;
                """;

        try (Connection connection = DriverManager.getConnection(BASE_URL + DB_NAME, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                Scroll scroll = new Scroll(
                    rs.getString("scrollid"),
                    rs.getString("scrollname"),
                    rs.getTimestamp("dateadded").toLocalDateTime(),
                    rs.getString("owner"),
                    rs.getInt("version")
                );
                scrolls.add(scroll);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return scrolls;
    }

    public static File getScrollFileById(String filename, File destinationFolder) {
        // System.out.println(filename);
        File[] matchingFiles = destinationFolder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(filename + ".");
            }
        });

        if (matchingFiles != null && matchingFiles.length > 0) {
            return matchingFiles[0];  // Return the first matching file
        } else {
            throw new RuntimeException("Scroll file not found for filename: " + filename);
        }
    }
    

    public static void initDatabase() {
        String createDBQuery = "CREATE DATABASE " + DB_NAME;

        try (Connection conn = DriverManager.getConnection(BASE_URL + "postgres", USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            String checkDBQuery = "SELECT datname FROM pg_database WHERE datname = '" + DB_NAME + "';";
            ResultSet rs = stmt.executeQuery(checkDBQuery);

            if (!rs.next()) {
                stmt.executeUpdate(createDBQuery);
            }
        } catch (SQLException e) {
            System.out.println("Error creating database: " + e.getMessage());
        }
    }

    public Connection connectToDatabase() {
        Connection conn = null;
        initDatabase();
        try {
            conn = DriverManager.getConnection(BASE_URL + DB_NAME, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
            return null;
        }
        return conn;
    }

    public Connection databaseConnect() throws SQLException {
        return connectToDatabase();
    }

    // Method to prepare SQL statements
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        Connection conn = connectToDatabase();  // Ensure we have a valid connection
        if (conn != null) {
            return conn.prepareStatement(sql);  // Return prepared statement
        } else {
            throw new SQLException("Failed to establish a database connection.");
        }
    }

    // Setter method for executing update queries (INSERT, UPDATE, DELETE)
    public int executeUpdate(String sql, Object... params) throws SQLException {
        try (Connection conn = connectToDatabase();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setParameters(pstmt, params);
            return pstmt.executeUpdate();
        }
    }

    // Getter method for executing select queries
    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        Connection conn = connectToDatabase();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        setParameters(pstmt, params);
        return pstmt.executeQuery();
    }

    // Helper method to set parameters in PreparedStatement
    private void setParameters(PreparedStatement pstmt, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
    }
 
    public boolean deleteScroll(String scrollId) {
        String deleteSQL = "DELETE FROM scrolls WHERE scrollid = ?";

        try (Connection conn = connectToDatabase();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {

            pstmt.setString(1, scrollId); // Set the scroll ID to be deleted

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; // Return true if a row was deleted

        } catch (SQLException e) {
            System.out.println("Error deleting scroll: " + e.getMessage());
            return false;
        }
    }

    public void createScrollsTable() {
        String createScrollsTableSQL = """
                CREATE TABLE IF NOT EXISTS scrolls (
                    scrollid VARCHAR(32) NOT NULL,
                    scrollname VARCHAR(255) NOT NULL,
                    version INT NOT NULL,
                    description VARCHAR(255),
                    dateadded DATE NOT NULL,
                    owner VARCHAR(255),
                    fileformat VARCHAR(10),
                    CONSTRAINT fk_owner FOREIGN KEY (owner) REFERENCES users(username),
                    PRIMARY KEY (scrollid, version)
                );
                """;

        try (Connection conn = connectToDatabase();
                Statement stmt = conn.createStatement()) {
            stmt.execute(createScrollsTableSQL);
            // System.out.println("Scrolls table created successfully!");
        } catch (SQLException e) {
            System.out.println("Error creating scrolls table: " + e.getMessage());
            return;
        }
    }

    public void createUsersTable() {
        String createuserTableSQL = """
                CREATE TABLE IF NOT EXISTS users (
                    username VARCHAR(255) UNIQUE NOT NULL PRIMARY KEY,
                    password VARCHAR(255) NOT NULL,
                    first_name VARCHAR(255) NOT NULL,
                    last_name VARCHAR(255) NOT NULL,
                    email VARCHAR(255) NOT NULL,
                    phone VARCHAR(20) NOT NULL,
                    customisable_id VARCHAR(255) NOT NULL,
                    isAdmin BOOLEAN NOT NULL
                );
                """;

        String alterTableSQL = """
            ALTER TABLE users
            ADD COLUMN IF NOT EXISTS customisable_id VARCHAR(255),
            ADD COLUMN IF NOT EXISTS isAdmin BOOLEAN;
            """;

        try (Connection conn = connectToDatabase();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createuserTableSQL);
            stmt.execute(alterTableSQL);
        } catch (SQLException e) {
            System.out.println("Error creating users table: " + e.getMessage());
            return;
        }

        boolean adminCreated = AuthenticatorDB.createSuperAdmin();
        if (!adminCreated) {
            System.out.println("Failed to create super admin!");
        }
    }
}
