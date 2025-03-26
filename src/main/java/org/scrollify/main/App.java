package org.scrollify.main;

import javafx.application.Application;
import javafx.stage.Stage;
import org.scrollify.scene.SceneManager;
import org.scrollify.database.DatabaseManager;
import org.scrollify.service.ScrollService;

import java.io.IOException;

public class App extends Application {
    private SceneManager sceneManager;
    private DatabaseManager dbManager;

    @Override
    public void start(Stage stage) throws IOException {
        this.dbManager = new DatabaseManager();
        this.dbManager.initialiseTables();
        stage.setResizable(false);
        sceneManager = new SceneManager(stage);
        sceneManager.showStage();
        stage.centerOnScreen();   
    }

    public static void main(String[] args) {
        launch();
    }
}
