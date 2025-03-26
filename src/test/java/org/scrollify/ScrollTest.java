package org.scrollify;

import org.junit.jupiter.api.Test;
import org.scrollify.model.Scroll;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ScrollTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        String scrollId = "scroll1";
        String scrollName = "Test Scroll";
        LocalDateTime dateAdded = LocalDateTime.now();
        String owner = "test_owner";
        int version = 1;

        // Act
        Scroll scroll = new Scroll(scrollId, scrollName, dateAdded, owner, version);

        // Assert
        assertEquals(scrollId, scroll.getScrollId(), "Scroll ID should match");
        assertEquals(scrollName, scroll.getScrollName(), "Scroll Name should match");
        assertEquals(dateAdded, scroll.getDateAdded(), "Date Added should match");
        assertEquals(owner, scroll.getOwner(), "Owner should match");
        assertEquals(version, scroll.getVersion(), "Version should match");
    }

    @Test
    void testSetters() {
        // Arrange
        Scroll scroll = new Scroll("scroll1", "Test Scroll", LocalDateTime.now(), "test_owner", 1);

        // Act
        scroll.setScrollId("scroll2");
        scroll.setScrollName("Updated Scroll");
        scroll.setDateAdded(LocalDateTime.now().plusDays(1));
        scroll.setOwner("new_owner");

        // Assert
        assertEquals("scroll2", scroll.getScrollId(), "Scroll ID should be updated");
        assertEquals("Updated Scroll", scroll.getScrollName(), "Scroll Name should be updated");
        assertNotNull(scroll.getDateAdded(), "Date Added should not be null");
        assertEquals("new_owner", scroll.getOwner(), "Owner should be updated");
    }

    @Test
    void testToString() {
        // Arrange
        Scroll scroll = new Scroll("scroll1", "Test Scroll", LocalDateTime.now(), "test_owner", 1);

        // Act
        String result = scroll.toString();

        // Assert
        assertEquals("Test Scroll", result, "toString should return the scroll name");
    }
}
