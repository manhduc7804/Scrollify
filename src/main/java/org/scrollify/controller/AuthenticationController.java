package org.scrollify.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import org.scrollify.backend.User;
import org.scrollify.database.AuthenticatorDB;
import org.scrollify.scene.SceneManager;

public class AuthenticationController {
    private SceneManager sceneManager;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    // FXML fields for login
    @FXML
    public TextField usernameField;

    @FXML
    public PasswordField passwordField;

    @FXML
    public Label notifText;

    // FXML fields for signup
    @FXML
    public TextField firstNameField;

    @FXML
    public TextField lastNameField;

    @FXML
    public TextField emailField;

    @FXML
    public TextField phoneField;

    @FXML
    public TextField customisableIDField;

    @FXML
    public void handleLogin() {
        // Get input values from the form fields
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Validation: Check if the fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            notifText.setText("Please fill in all fields.");
            return;
        }

        // Call AuthenticatorDB to authenticate the user
        User user = AuthenticatorDB.loginUser(username, password);
        if (user == null) {
            notifText.setText("Invalid username or password. Please try again.");
        } else {
            sceneManager.showScrollsScene(user); // Navigate to the scrolls scene on successful login
        }
    }

    @FXML
    public void handleSignup() {
        // Get input values from the form fields
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String customisable_id = customisableIDField.getText();

        // Validation: Check if any field is empty
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || customisable_id.isEmpty()) {
            notifText.setText("Please fill in all required fields.");
            return;
        }

        // Call the AuthenticatorDB to signup the new user
        try {
            if (AuthenticatorDB.isCustomisableIDTaken(customisable_id)) {
                notifText.setText("Customisable ID key is taken! Please try a new one...");
                return;
            } else if (AuthenticatorDB.isUsernameTaken(username)) {
                notifText.setText("Username is taken! Please try a new one...");
                return;
            }
            AuthenticatorDB.signupNewUser(firstName, lastName, email, phone, username, password, customisable_id);
            notifText.setText("Signup successful! Redirecting to login page...");
            Thread.sleep(2000);
            sceneManager.showLoginScene();
        } catch (Exception e) {
            notifText.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void goToLogin() {
        sceneManager.showLoginScene();
    }

    @FXML
    private void goToSignup() {
        sceneManager.showSignupScene();
    }

    @FXML
    private void goToWelcome() {
        sceneManager.showWelcomeScene();
    }

    @FXML
    public void goToHomepageAsGuest() {
        sceneManager.showScrollsScene(null);
    }
}
