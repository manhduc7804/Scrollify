package org.scrollify.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import org.scrollify.backend.User;
import org.scrollify.database.AuthenticatorDB;
import org.scrollify.scene.SceneManager;

public class ProfileController {
    private SceneManager sceneManager;
    private User user;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void setUser(User user) {
        this.user = user;
        loadUserData();
    }

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField customisableIDField;

    @FXML
    private PasswordField oldPasswordField; // Changed to PasswordField for security

    @FXML
    private PasswordField newPasswordField; // Changed to PasswordField for security

    @FXML
    private Label notifText;

    private void loadUserData() {
        if (user != null) {
            firstNameField.setText(user.getFirstName());
            lastNameField.setText(user.getLastName());
            emailField.setText(user.getEmail());
            phoneField.setText(user.getPhone());
            customisableIDField.setText(user.getCustomisableID());
        }
    }

    @FXML
    private void handleProfileUpdate() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String customisableID = customisableIDField.getText();

        try {
            // Validate input fields
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || customisableID.isEmpty()) {
                notifText.setText("Please fill in all fields.");
                return;
            }

            // Check if the new customisable ID is taken by another user (excluding the current user)
            if (AuthenticatorDB.isCustomisableIDTakenByOther(customisableID, user.getUsername())) {
                notifText.setText("Customisable ID key is taken by another user! Please try a new one...");
                return;
            }

            // Verify if the old password matches the stored password (if new password is provided)
            if (!newPassword.isEmpty()) {
                String currentStoredPassword = AuthenticatorDB.getPasswordForUser(user.getUsername());
                if (!AuthenticatorDB.verifyPassword(oldPassword, currentStoredPassword)) {
                    notifText.setText("Old password is incorrect.");
                    return;
                }
            }

            // Update the user profile in the database
            boolean updateSuccess = AuthenticatorDB.updateUserProfile(
                    firstName, lastName, email, phone, user.getUsername(),
                    newPassword.isEmpty() ? null : newPassword,  // If no new password, skip password update
                    customisableID
            );

            if (updateSuccess) {
                notifText.setText("Profile updated successfully.");
                sceneManager.showWelcomeScene();
            } else {
                notifText.setText("Profile update failed. Please check your details.");
            }
        } catch (Exception e) {
            notifText.setText("Error: " + e.getMessage());
        }
    }



    @FXML
    private void goToHome() {
        sceneManager.showScrollsScene(user);
    }
}
