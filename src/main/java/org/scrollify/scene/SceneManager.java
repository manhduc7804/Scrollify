package org.scrollify.scene;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.scrollify.backend.User;
import org.scrollify.controller.*;
import org.scrollify.database.DatabaseManager;
import org.scrollify.main.App;
import org.scrollify.model.Scroll;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

import java.io.IOException;

public class SceneManager {
    private Stage stage;
    public SceneManager(Stage stageDefault) throws IOException {
        this.stage = stageDefault;
        showWelcomeScene();
    }

    public void showStage() {
        stage.show();
    }

    public void showWelcomeScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/xml/WelcomePage.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
            stage.setScene(scene);
            stage.setTitle("Scrollify VSAS");
            AuthenticationController controller = fxmlLoader.getController();
            controller.setSceneManager(this);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void showLoginScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/xml/LoginPage.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
            stage.setScene(scene);
            stage.setTitle("Scrollify Login");
            AuthenticationController controller = fxmlLoader.getController();
            controller.setSceneManager(this);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void showSignupScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/xml/SignupPage.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
            stage.setScene(scene);
            stage.setTitle("Scrollify Sign up");
            AuthenticationController controller = fxmlLoader.getController();
            controller.setSceneManager(this);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void showPreviewScroll(Scroll scroll, User user) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/xml/ScrollPreviewPage.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
            PreviewScrollController controller = fxmlLoader.getController();
            controller.setSceneManager(this);
            controller.setUser(user);
            controller.setSelectedScroll(scroll);
            stage.setScene(scene);
            stage.setTitle("Scrollify scrolls preview");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void showScrollsScene(User user) {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/xml/ScrollsPage.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
            stage.setTitle("Scrollify VSAS");
            
            ScrollsController controller = fxmlLoader.getController();
            controller.setSceneManager(this);
            controller.setUser(user);
            stage.setScene(scene);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void showAddScroll(User user) {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/xml/AddScrollPage.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
            stage.setScene(scene);
            stage.setTitle("Add Scroll");

            ScrollsController controller = fxmlLoader.getController();
            controller.setSceneManager(this);
            controller.setUser(user);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    public void showEditScroll(User user, Scroll scroll) {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/xml/EditScrollPage.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
            stage.setScene(scene);
            stage.setTitle("Edit Scroll");

            EditScrollController controller = fxmlLoader.getController();
            controller.setSceneManager(this);
            controller.setUser(user);
            controller.initData(scroll);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void showUpdateProfile(User user) {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/xml/UpdateProfile.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
            stage.setScene(scene);
            stage.setTitle("Update Profile");

            ProfileController controller = fxmlLoader.getController();
            controller.setSceneManager(this);
            controller.setUser(user);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void showAdminScene(User user) {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/xml/AdminPage.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
            stage.setScene(scene);
            stage.setTitle("Admin Page");

            AdminController controller = fxmlLoader.getController();
            controller.setSceneManager(this);
            controller.setUser(user);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
