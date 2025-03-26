package org.scrollify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.scrollify.backend.User;
import org.scrollify.database.DatabaseManager;
import org.scrollify.service.UserService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private static final String USERNAME = "test_user";
    private static final String PASSWORD = "password123";
    private static final String USER_EMAIL = "test@example.com";
    private static final String USER_PHONE = "1234567890";
    private static final String CUSTOM_ID = "CUST123";

    @Mock
    private DatabaseManager mockDatabaseManager;

    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(mockDatabaseManager);
    }

    @Test
    void testGetAllUsers() throws SQLException {
        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockDatabaseManager.executeQuery(anyString())).thenReturn(mockResultSet);

        // Mocking ResultSet behavior
        when(mockResultSet.next()).thenReturn(true).thenReturn(false); // One result
        when(mockResultSet.getString("username")).thenReturn(USERNAME);
        when(mockResultSet.getString("first_name")).thenReturn("First");
        when(mockResultSet.getString("last_name")).thenReturn("Last");
        when(mockResultSet.getString("email")).thenReturn(USER_EMAIL);
        when(mockResultSet.getString("phone")).thenReturn(USER_PHONE);
        when(mockResultSet.getString("customisable_id")).thenReturn(CUSTOM_ID);
        when(mockResultSet.getBoolean("isAdmin")).thenReturn(false);

        List<User> users = userService.getAllUsers();

        assertNotNull(users, "User list should not be null.");
        assertEquals(1, users.size(), "There should be exactly one user in the list.");

        User retrievedUser = users.get(0);
        assertEquals(USERNAME, retrievedUser.getUsername(), "Username should match.");
        assertEquals("First", retrievedUser.getFirstName(), "First name should match.");
        assertEquals("Last", retrievedUser.getLastName(), "Last name should match.");
        assertEquals(USER_EMAIL, retrievedUser.getEmail(), "Email should match.");
        assertEquals(USER_PHONE, retrievedUser.getPhone(), "Phone should match.");
        assertEquals(CUSTOM_ID, retrievedUser.getCustomisableID(), "Customisable ID should match.");
        assertFalse(retrievedUser.isAdmin(), "User should not be an admin.");
    }

    @Test
    void testDeleteUser() throws SQLException {
        User userToDelete = new User(USERNAME, "First", "Last", USER_EMAIL, USER_PHONE, CUSTOM_ID, false);

        // Update to match the actual parameters in executeUpdate
        when(mockDatabaseManager.executeUpdate(anyString(), any())).thenReturn(1); // Adjusted

        // Call the method under test
        boolean deleted = userService.deleteUser(userToDelete);

        // Verify that executeUpdate was called with the correct parameters
        verify(mockDatabaseManager, times(1)).executeUpdate(anyString(), eq(userToDelete.getUsername()));

        assertTrue(deleted, "User should be deleted successfully.");
    }

    @Test
    void testDeleteNonExistentUser() throws SQLException {
        User nonExistentUser = new User("nonexistent_user", "First", "Last", "nonexistent@example.com", "1234567890", "CUST123", false);

        when(mockDatabaseManager.executeUpdate(anyString(), any(), any())).thenReturn(0);

        boolean deleted = userService.deleteUser(nonExistentUser);

        assertFalse(deleted, "Deleting a non-existent user should return false.");
    }
}
