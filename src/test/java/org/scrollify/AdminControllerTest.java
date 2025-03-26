//package org.scrollify;
//
//import javafx.collections.FXCollections;
//import javafx.scene.control.ListView;
//import javafx.scene.control.Label;
//import javafx.scene.control.Button;
//import javafx.scene.layout.HBox;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.scrollify.controller.AdminController;
//import org.scrollify.scene.SceneManager;
//import org.scrollify.service.UserService;
//import org.scrollify.backend.User;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class AdminControllerTest {
//
//    private AdminController adminController;
//    private SceneManager mockSceneManager;
//    private UserService mockUserService;
//    private ListView<HBox> mockUserListView;
//    private Label mockUserRoleLabel;
//
//    @BeforeEach
//    public void setUp() {
//        mockSceneManager = mock(SceneManager.class);
//        mockUserService = mock(UserService.class);
//        mockUserListView = new ListView<>();
//        mockUserRoleLabel = new Label();
//
//        adminController = new AdminController();
//        adminController.setSceneManager(mockSceneManager);
//        adminController.userService = mockUserService; // Injecting the mocked UserService
//        adminController.userListView = mockUserListView;
//        adminController.userRoleLabel = mockUserRoleLabel;
//    }
//
//    @Test
//    public void testSetUser() {
//        User testUser = new User("adminUser", "Admin", "User", "admin@example.com", "1234567890", "ID1", true);
//        adminController.setUser(testUser);
//        assertEquals("Welcome, Admin", mockUserRoleLabel.getText());
//    }
//
//    @Test
//    public void testLoadUsers_Empty() {
//        when(mockUserService.getAllUsers()).thenReturn(new ArrayList<>());
//
//        adminController.initialize(); // Trigger loading of users
//
//        assertTrue(mockUserListView.getItems().isEmpty());
//    }
//
//    @Test
//    public void testLoadUsers_WithUsers() {
//        List<User> testUsers = new ArrayList<>();
//        User user1 = new User("user1", "First", "User", "user1@example.com", "1234567890", "ID1", false);
//        testUsers.add(user1);
//
//        when(mockUserService.getAllUsers()).thenReturn(testUsers);
//
//        adminController.initialize();
//
//        assertEquals(1, mockUserListView.getItems().size());
//        HBox userContainer = mockUserListView.getItems().get(0);
//        assertTrue(userContainer.getChildren().stream().anyMatch(node -> ((Label) node).getText().equals("user1")));
//    }
//
//    @Test
//    public void testRemoveUser_Success() {
//        User testUser = new User("userToRemove", "ToRemove", "User", "remove@example.com", "0987654321", "ID2", false);
//
//        // Simulating the behavior of the remove button
//        when(mockUserService.deleteUser(testUser)).thenReturn(true);
//
//        HBox userContainer = adminController.createUserContainer(testUser);
//        userContainer.getChildren().stream()
//                .filter(node -> node instanceof Button && ((Button) node).getText().equals("Remove"))
//                .forEach(button -> {
//                    ((Button) button).fire(); // Simulate button click
//                });
//
//        verify(mockUserService).deleteUser(testUser);
//        assertTrue(mockUserListView.getItems().isEmpty()); // Expect the user list to be empty after removal
//    }
//
//    @Test
//    public void testShowUserDetails() {
//        User testUser = new User("userDetails", "John", "Doe", "john.doe@example.com", "1234567890", "customID", false);
//
//        // This should execute without throwing exceptions
//        assertDoesNotThrow(() -> adminController.showUserDetails(testUser));
//    }
//
//    @Test
//    public void testLogout() {
//        adminController.Logout();
//        verify(mockSceneManager).showWelcomeScene();
//    }
//
//    @Test
//    public void testGoToAddUser() {
//        adminController.goToAddUser();
//        verify(mockSceneManager).showSignupScene();
//    }
//
//    @Test
//    public void testGoToHomepage() {
//        User testUser = new User("homepageUser", "Homepage", "User", "homepage@example.com", "1234567890", "ID3", false);
//        adminController.goToHomepage();
//        verify(mockSceneManager).showScrollsScene(testUser);
//    }
//
//    @Test
//    public void testGoToSummary() {
//        User testUser = new User("summaryUser", "Summary", "User", "summary@example.com", "1234567890", "ID4", false);
//        adminController.goToSummary();
//        verify(mockSceneManager).showSummary(testUser);
//    }
//}
