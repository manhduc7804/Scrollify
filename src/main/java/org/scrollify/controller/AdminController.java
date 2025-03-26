package org.scrollify.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.scrollify.backend.User;
import org.scrollify.database.DatabaseManager;
import org.scrollify.service.ScrollService;
import org.scrollify.service.UserService;
import org.scrollify.scene.SceneManager;

import java.util.List;

public class AdminController {
    private SceneManager sceneManager;
    private User user;
    public UserService userService = new UserService(new DatabaseManager()); // Initialize UserService

    // Summary related fields
    private ScrollService scrollService = new ScrollService(new DatabaseManager());

    @FXML
    public ListView<HBox> userListView; // Change ListView for users

    @FXML
    public Label userRoleLabel;

    @FXML
    private Label totalUploadedLabel;

    @FXML
    private Label totalDownloadedLabel;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void setUser(User user) {
        this.user = user;
        userRoleLabel.setText("Welcome, Admin");
        refreshSummary(); // Refresh summary when user is set
    }

    @FXML
    public void initialize() {
        loadUsers(); // Load users on initialization
        refreshSummary(); // Refresh summary on initialization
    }

    private void loadUsers() {
        List<User> users = userService.getAllUsers(); // Fetch all users
        if (users.isEmpty()) {
            HBox noUserBox = new HBox();
            noUserBox.getChildren().add(new Label("No users found."));
            userListView.getItems().add(noUserBox);
        } else {
            ObservableList<HBox> userContainers = FXCollections.observableArrayList();
            for (User user : users) {
                HBox container = createUserContainer(user); // Create a container for each user
                userContainers.add(container);
            }
            userListView.setItems(userContainers);
        }
    }

    public HBox createUserContainer(User user) {
        HBox container = new HBox();
        container.setSpacing(20);

        Label usernameLabel = new Label(user.getUsername());
        Label customIDLabel = new Label(user.getCustomisableID());

        Button viewMoreButton = new Button("View More");
        viewMoreButton.setOnAction(event -> {
            // Logic to show user details
            showUserDetails(user);
        });

        Button removeButton = new Button("Remove");
        removeButton.setOnAction(event -> {
            boolean success = userService.deleteUser(user); // Delete user
            if (success) {
                // Notify admin of successful deletion
                loadUsers(); // Reload user list after deletion
                // Optionally display a notification
            }
        });

        container.getChildren().addAll(usernameLabel, customIDLabel, viewMoreButton, removeButton);
        container.setStyle("-fx-border-color: #000; -fx-padding: 10; -fx-background-color: #f0f0f0;");

        return container;
    }

    public void showUserDetails(User user) {
        // Create a dialog to display user details
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("User Details");

        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);

        // Set user details in the TextArea
        String userDetails = "First Name: " + user.getFirstName() + "\n" +
                "Last Name: " + user.getLastName() + "\n" +
                "Email: " + user.getEmail() + "\n" +
                "Phone: " + user.getPhone();
        textArea.setText(userDetails);

        dialog.getDialogPane().setContent(textArea);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        dialog.showAndWait();
    }

    public void refreshSummary() {
        totalUploadedLabel.setText("Total Scrolls Uploaded: " + scrollService.getTotalScrollsUploaded());
        totalDownloadedLabel.setText("Total Scrolls Downloaded: " + scrollService.getTotalDownloads());
    }

    @FXML
    public void Logout() {
        sceneManager.showWelcomeScene();
    }

    @FXML
    public void goToAddUser() {
        sceneManager.showSignupScene();
    }

    @FXML
    public void goToHomepage() {
        sceneManager.showScrollsScene(user);
    }
}
