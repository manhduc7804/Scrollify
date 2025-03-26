package org.scrollify.controller;
import org.scrollify.model.Scroll;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.scrollify.backend.User;
import org.scrollify.scene.SceneManager;
import org.scrollify.database.DatabaseManager;
import java.time.LocalDateTime;

import java.io.IOException;
import java.io.File;
import javafx.scene.control.Label;


import org.scrollify.service.ScrollService;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class EditScrollController {

    private SceneManager sceneManager;
    private DatabaseManager databaseManager;
    private Scroll selectedScroll;
    private User user;
    private File selectedScrollFile;
    private String fileFormat;

    public EditScrollController() {
        this.databaseManager = new DatabaseManager();
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void initData(Scroll scroll) {

        if (scroll != null) {
            this.selectedScroll = scroll;
            scrollNameField.setText(scroll.getScrollName());
            fileNameLabel.setText("Current file: " + scroll.getScrollName());
        } else {

            scrollNameField.setText("");
            fileNameLabel.setText("No scroll selected.");
        }
    }

    @FXML
    private TextField scrollNameField;

    @FXML
    private Label fileNameLabel;

    @FXML
    private void handleNewFileSelection() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select New Scroll File");
        selectedScrollFile = fileChooser.showOpenDialog(new Stage());

        if (selectedScrollFile != null) {
            fileNameLabel.setText("Selected File: " + selectedScrollFile.getName());
        } else {
            fileNameLabel.setText("No file selected");
        }
    }

    @FXML
    private void handleSaveChanges() {
        String newScrollName = scrollNameField.getText();

        if (newScrollName.isEmpty()) {
            System.out.println("No new name provided, retaining the old name.");
        } else {
            selectedScroll.setScrollName(newScrollName);
        }

        if (selectedScrollFile != null) {
            fileFormat = getFileFormat(selectedScrollFile);
        } else {
            File destinationFolder = new File("src/main/resources/scrolls");
            File previousFile = databaseManager.getScrollFileById(selectedScroll.getScrollId()+"#"+selectedScroll.getVersion(), destinationFolder);

            if (previousFile != null) {
                fileFormat = getFileFormat(previousFile);
            } else {
                System.out.println("No previous file found, cannot determine file format.");
                return;
            }
        }
        ScrollService scrollService = new ScrollService(databaseManager);
        int latestVersion = scrollService.getLatestScrollVersionByID(selectedScroll.getScrollId());
        int newVersion = latestVersion + 1;

        try {
            File destinationFolder = new File("src/main/resources/scrolls");
            File fileToUse = (selectedScrollFile == null) ? databaseManager.getScrollFileById(selectedScroll.getScrollId()+"#"+selectedScroll.getVersion(), destinationFolder) : selectedScrollFile;

            saveNewScrollFileWithVersion(selectedScroll.getScrollId(), newVersion, fileToUse);
            System.out.println("Scroll file saved successfully.");

            boolean success = scrollService.insertScrollIntoDatabase(selectedScroll.getScrollId(), newScrollName, LocalDateTime.now(), selectedScroll.getOwner(), newVersion);

            if (success) {
                System.out.println("Scroll inserted successfully.");
                sceneManager.showScrollsScene(user);
            } else {
                System.out.println("Failed to insert new scroll version into the database.");
            }
        } catch (IOException e) {
            System.out.println("Error saving the new scroll file: " + e.getMessage());
        }
    }

    private void saveNewScrollFileWithVersion(String scrollId, int version, File newFile) throws IOException {
        File destinationFolder = new File("src/main/resources/scrolls");

        if (!destinationFolder.exists()) {
            destinationFolder.mkdirs();
        }

        String fileFormat = getFileFormat(newFile);
        File destinationFile = new File(destinationFolder, scrollId + "#" + version + "." + fileFormat);

        Files.copy(newFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Scroll file saved as: " + destinationFile.getAbsolutePath());
    }

    private String getFileFormat(File file) {
        String fileName = file.getName();
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    @FXML
    private void goToHomepage() {
        sceneManager.showScrollsScene(user);
    }

}