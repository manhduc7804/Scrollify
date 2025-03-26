package org.scrollify;

import org.junit.jupiter.api.Test;
import org.scrollify.backend.User;
import org.scrollify.enums.UserEnum;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testUserConstructorAndGetters() {
        // Define test data
        String username = "testuser";
        String firstName = "John";
        String lastName = "Doe";
        String email = "john.doe@example.com";
        String phone = "1234567890";
        String customisableID = "CUST123";
        boolean isAdmin = true; // Use boolean for isAdmin

        // Create User object using the test data
        User user = new User(username, firstName, lastName, email, phone, customisableID, isAdmin);

        // Validate each field using assertions
        assertEquals(username, user.getUsername(), "Username should match");
        assertEquals(firstName, user.getFirstName(), "First name should match");
        assertEquals(lastName, user.getLastName(), "Last name should match");
        assertEquals(email, user.getEmail(), "Email should match");
        assertEquals(phone, user.getPhone(), "Phone number should match");
        assertEquals(customisableID, user.getCustomisableID(), "Customisable ID should match");
        assertEquals(isAdmin, user.isAdmin(), "Admin status should match"); // Check isAdmin
    }
}