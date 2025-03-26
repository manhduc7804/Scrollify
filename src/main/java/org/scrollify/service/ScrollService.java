package org.scrollify.service;

import org.scrollify.database.DatabaseManager;
import org.scrollify.model.Scroll;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ScrollService {

    private final DatabaseManager dbManager;

    public ScrollService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public boolean insertScrollIntoDatabase(String scrollId, String scrollName, LocalDateTime dateAdded, String owner, int version) {
        String insertSQL = """
                INSERT INTO scrolls (scrollid, scrollname, dateadded, owner, version) 
                VALUES (?, ?, ?, ?, ?)
                """;
        try {
            dbManager.executeUpdate(insertSQL, scrollId, scrollName, Timestamp.valueOf(dateAdded), owner, version);
            return true;
        } catch (SQLException e) {
            System.out.println("Error inserting scroll: " + e.getMessage());
            return false;
        }
    }

    public int getLatestScrollVersionByID(String scrollid) {
        String query = "SELECT version FROM scrolls WHERE scrollid = ? ORDER BY version DESC LIMIT 1";
        try (ResultSet rs = dbManager.executeQuery(query, scrollid)) {
            if (rs.next()) {
                return rs.getInt("version");
            } else {
                return 0;
            }
        } catch (SQLException e) {
            System.out.println("Error fetching latest scroll version: " + e.getMessage());
            return -1;
        }
    }

    public void incrementDownloadCount(String scrollId) {
        String updateSQL = "UPDATE scrolls SET downloads = downloads + 1 WHERE scrollid = ?";
        try {
            dbManager.executeUpdate(updateSQL, scrollId);
        } catch (SQLException e) {
            System.out.println("Error incrementing download count: " + e.getMessage());
        }
    }

    public int getTotalDownloads() {
        String query = "SELECT SUM(downloads) AS totalDownloads FROM scrolls";
        try (ResultSet rs = dbManager.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("totalDownloads");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching total downloads: " + e.getMessage());
        }
        return 0;
    }

    public List<Scroll> getScrollVersionsHistoryByID(String scrollid) {
        List<Scroll> scrolls = new ArrayList<>();
        String query = "SELECT * FROM scrolls WHERE scrollid = ? ORDER BY version DESC";
        try (ResultSet rs = dbManager.executeQuery(query, scrollid)) {
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
        } catch (SQLException e) {
            System.out.println("Error fetching scroll version history: " + e.getMessage());
        }
        return scrolls;
    }

    public List<Scroll> getAllScrolls() {
        List<Scroll> scrolls = new ArrayList<>();
        String query = """
                SELECT s.* 
                FROM scrolls s
                INNER JOIN (
                    SELECT scrollid, MAX(version) AS latest_version 
                    FROM scrolls 
                    GROUP BY scrollid
                ) latest_versions
                ON s.scrollid = latest_versions.scrollid AND s.version = latest_versions.latest_version;
                """;
        try (ResultSet rs = dbManager.executeQuery(query)) {
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
        } catch (SQLException e) {
            System.out.println("Error fetching all scrolls: " + e.getMessage());
        }
        return scrolls;
    }

    public int getTotalScrollsUploaded() {
        String query = "SELECT COUNT(*) AS total FROM scrolls";
        try (ResultSet rs = dbManager.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching total scrolls uploaded: " + e.getMessage());
        }
        return 0;
    }
}
