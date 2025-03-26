package org.scrollify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.scrollify.database.DatabaseManager;
import org.scrollify.model.Scroll;
import org.scrollify.service.ScrollService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ScrollServiceTest {

    @Mock
    private DatabaseManager mockDatabaseManager;

    private ScrollService scrollService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        scrollService = new ScrollService(mockDatabaseManager);
    }

    @Test
    void testInsertScrollIntoDatabase() throws SQLException {
        // Prepare test data
        String scrollId = "scroll1";
        String scrollName = "Test Scroll";
        LocalDateTime dateAdded = LocalDateTime.now();
        String owner = "test_owner";
        int version = 1;

        // Stub the executeUpdate method to simulate successful execution
        when(mockDatabaseManager.executeUpdate(anyString(), any(), any(), any(), any())).thenReturn(1);

        // Call the method under test
        boolean result = scrollService.insertScrollIntoDatabase(scrollId, scrollName, dateAdded, owner, version);

        // Assertions
        assertTrue(result, "Scroll should be inserted successfully.");
        verify(mockDatabaseManager, times(1)).executeUpdate(anyString(), eq(scrollId), eq(scrollName), any(), eq(owner), eq(version));
    }

    @Test
    void testGetLatestScrollVersionByID() throws Exception {
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockDatabaseManager.executeQuery(anyString(), any())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("version")).thenReturn(2);

        int version = scrollService.getLatestScrollVersionByID("scroll1");
        assertEquals(2, version);
        verify(mockDatabaseManager, times(1)).executeQuery(anyString(), any());
    }

    @Test
    void testGetLatestScrollVersionByID_NoResults() throws Exception {
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockDatabaseManager.executeQuery(anyString(), any())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // No results

        int version = scrollService.getLatestScrollVersionByID("scroll1");
        assertEquals(0, version);
        verify(mockDatabaseManager, times(1)).executeQuery(anyString(), any());
    }

    @Test
    void testGetLatestScrollVersionByID_SQLException() throws Exception {
        when(mockDatabaseManager.executeQuery(anyString(), any())).thenThrow(new SQLException("SQL Error"));

        int version = scrollService.getLatestScrollVersionByID("scroll1");
        assertEquals(-1, version);
        verify(mockDatabaseManager, times(1)).executeQuery(anyString(), any());
    }

    @Test
    void testIncrementDownloadCount() throws SQLException {
        // Prepare test data
        String scrollId = "scroll1";

        // Stub the executeUpdate method to simulate successful execution
        when(mockDatabaseManager.executeUpdate(anyString(), any())).thenReturn(1);

        // Call the method under test
        scrollService.incrementDownloadCount(scrollId);

        // Assertions
        verify(mockDatabaseManager, times(1)).executeUpdate(anyString(), eq(scrollId));
    }

    @Test
    void testIncrementDownloadCount_SQLException() throws SQLException {
        // Prepare test data
        String scrollId = "scroll1";

        // Stub the executeUpdate method to simulate a failure
        when(mockDatabaseManager.executeUpdate(anyString(), any())).thenThrow(new SQLException("SQL Error"));

        // Call the method under test
        scrollService.incrementDownloadCount(scrollId);

        // Assertions
        verify(mockDatabaseManager, times(1)).executeUpdate(anyString(), eq(scrollId));
    }

    @Test
    void testGetTotalDownloads() throws Exception {
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockDatabaseManager.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("totalDownloads")).thenReturn(100);

        int totalDownloads = scrollService.getTotalDownloads();
        assertEquals(100, totalDownloads);
        verify(mockDatabaseManager, times(1)).executeQuery(anyString());
    }

    @Test
    void testGetTotalDownloads_NoResults() throws Exception {
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockDatabaseManager.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // No results

        int totalDownloads = scrollService.getTotalDownloads();
        assertEquals(0, totalDownloads);
        verify(mockDatabaseManager, times(1)).executeQuery(anyString());
    }

    @Test
    void testGetTotalDownloads_SQLException() throws Exception {
        when(mockDatabaseManager.executeQuery(anyString())).thenThrow(new SQLException("SQL Error"));

        int totalDownloads = scrollService.getTotalDownloads();
        assertEquals(0, totalDownloads);
        verify(mockDatabaseManager, times(1)).executeQuery(anyString());
    }

    @Test
    void testGetScrollVersionsHistoryByID() throws Exception {
        String scrollId = "scroll1";
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockDatabaseManager.executeQuery(anyString(), any())).thenReturn(mockResultSet);

        // Mocking ResultSet behavior
        when(mockResultSet.next()).thenReturn(true).thenReturn(false); // One result
        when(mockResultSet.getString("scrollid")).thenReturn(scrollId);
        when(mockResultSet.getString("scrollname")).thenReturn("Test Scroll");
        when(mockResultSet.getString("owner")).thenReturn("test_owner");
        when(mockResultSet.getTimestamp("dateadded")).thenReturn(java.sql.Timestamp.valueOf(LocalDateTime.now()));
        when(mockResultSet.getInt("version")).thenReturn(1);

        List<Scroll> scrolls = scrollService.getScrollVersionsHistoryByID(scrollId);

        // Assertions
        assertEquals(1, scrolls.size(), "There should be one scroll in history.");
        assertEquals("Test Scroll", scrolls.get(0).getScrollName(), "Scroll name should match.");
    }

    @Test
    void testGetScrollVersionsHistoryByID_NoResults() throws Exception {
        String scrollId = "scroll2";
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockDatabaseManager.executeQuery(anyString(), any())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // No results

        List<Scroll> scrolls = scrollService.getScrollVersionsHistoryByID(scrollId);
        assertTrue(scrolls.isEmpty(), "Scrolls list should be empty for no results.");
    }

    @Test
    void testGetScrollVersionsHistoryByID_SQLException() throws Exception {
        String scrollId = "scroll1";
        when(mockDatabaseManager.executeQuery(anyString(), any())).thenThrow(new SQLException("SQL Error"));

        List<Scroll> scrolls = scrollService.getScrollVersionsHistoryByID(scrollId);
        assertTrue(scrolls.isEmpty(), "Scrolls list should be empty on SQL error.");
    }

    @Test
    void testGetAllScrolls() throws Exception {
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockDatabaseManager.executeQuery(anyString())).thenReturn(mockResultSet);

        // Mocking ResultSet behavior
        when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false); // Two results
        when(mockResultSet.getString("scrollid")).thenReturn("scroll1").thenReturn("scroll2");
        when(mockResultSet.getString("scrollname")).thenReturn("Scroll One").thenReturn("Scroll Two");
        when(mockResultSet.getString("owner")).thenReturn("test_owner").thenReturn("test_owner");
        when(mockResultSet.getTimestamp("dateadded")).thenReturn(java.sql.Timestamp.valueOf(LocalDateTime.now())).thenReturn(java.sql.Timestamp.valueOf(LocalDateTime.now().plusDays(1)));
        when(mockResultSet.getInt("version")).thenReturn(1).thenReturn(1);

        List<Scroll> allScrolls = scrollService.getAllScrolls();

        // Assertions
        assertEquals(2, allScrolls.size(), "There should be 2 scrolls in total.");
        assertEquals("Scroll One", allScrolls.get(0).getScrollName(), "First scroll name should match.");
        assertEquals("Scroll Two", allScrolls.get(1).getScrollName(), "Second scroll name should match.");
    }

    @Test
    void testGetAllScrolls_NoResults() throws Exception {
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockDatabaseManager.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // No results

        List<Scroll> allScrolls = scrollService.getAllScrolls();
        assertTrue(allScrolls.isEmpty(), "Scrolls list should be empty for no results.");
    }

    @Test
    void testGetAllScrolls_SQLException() throws Exception {
        when(mockDatabaseManager.executeQuery(anyString())).thenThrow(new SQLException("SQL Error"));

        List<Scroll> allScrolls = scrollService.getAllScrolls();
        assertTrue(allScrolls.isEmpty(), "Scrolls list should be empty on SQL error.");
    }

    @Test
    void testGetTotalScrollsUploaded() throws Exception {
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockDatabaseManager.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("total")).thenReturn(5);

        int totalScrolls = scrollService.getTotalScrollsUploaded();
        assertEquals(5, totalScrolls);
        verify(mockDatabaseManager, times(1)).executeQuery(anyString());
    }

    @Test
    void testGetTotalScrollsUploaded_NoResults() throws Exception {
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockDatabaseManager.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // No results

        int totalScrolls = scrollService.getTotalScrollsUploaded();
        assertEquals(0, totalScrolls);
        verify(mockDatabaseManager, times(1)).executeQuery(anyString());
    }

    @Test
    void testGetTotalScrollsUploaded_SQLException() throws Exception {
        when(mockDatabaseManager.executeQuery(anyString())).thenThrow(new SQLException("SQL Error"));

        int totalScrolls = scrollService.getTotalScrollsUploaded();
        assertEquals(0, totalScrolls);
        verify(mockDatabaseManager, times(1)).executeQuery(anyString());
    }
}
