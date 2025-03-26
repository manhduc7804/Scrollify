package org.scrollify.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.StringConverter;
import org.scrollify.backend.User;
import org.scrollify.database.DatabaseManager;
import org.scrollify.model.Scroll;
import org.scrollify.scene.SceneManager;
import org.scrollify.service.ScrollService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class PreviewScrollController {
    private User user;
    private Scroll selectedScroll;
    private DatabaseManager databaseManager = new DatabaseManager();
    private ScrollService scrollService = new ScrollService(databaseManager);
    private SceneManager sceneManager;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void setUser(User user) {
        this.user = user;
        if (user==null) {
            this.downloadButton.setVisible(false);
        }
    }

    @FXML
    private Button downloadButton;

    @FXML
    private Label scrollNameLabel;

    @FXML
    private TextArea scrollContentArea;

    @FXML
    private ImageView scrollImageView;

    @FXML
    private Label errorLabel;

    @FXML
    private ComboBox<Scroll> versionHistoryDropdown;

    public void setSelectedScroll(Scroll scroll) {
        this.selectedScroll = scroll;
        scrollNameLabel.setText(scroll.getScrollName());
        loadScrollContent();
        loadScrollVersionsHistory();
    }

    private void loadScrollContent() {
        File destinationFolder = new File("src/main/resources/scrolls");

        try {
            File scrollFile = databaseManager.getScrollFileById(selectedScroll.getScrollId()+"#"+selectedScroll.getVersion(), destinationFolder);
            if (scrollFile == null) {
                throw new IOException("File not found");
            }

            String fileFormat = scrollFile.getName().substring(scrollFile.getName().lastIndexOf('.') + 1);

            if (fileFormat.equals("txt")) {
                String content = new String(Files.readAllBytes(Paths.get(scrollFile.toURI())));
                scrollContentArea.setText(content);
                scrollContentArea.setVisible(true);
                scrollImageView.setVisible(false);
            } else if (fileFormat.equals("jpg") || fileFormat.equals("png")) {
                Image image = new Image(scrollFile.toURI().toString());
                scrollImageView.setImage(image);
                scrollImageView.setVisible(true);
                scrollContentArea.setVisible(false);
            }
        } catch (IOException e) {
            errorLabel.setText("Error retrieving file!");
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void loadScrollVersionsHistory() {
        List<Scroll> scrollVersions = scrollService.getScrollVersionsHistoryByID(selectedScroll.getScrollId());
        versionHistoryDropdown.setItems(FXCollections.observableArrayList(scrollVersions));
        versionHistoryDropdown.setConverter(new StringConverter<>() {
            @Override
            public String toString(Scroll scroll) {
                if (scroll == null) {   
                    return "Select Version";
                }
                boolean isLatest = scroll.equals(scrollVersions.get(0));

                if (isLatest) {
                    return String.format("Version %d (LATEST): %s (Added: %s)", scroll.getVersion(), scroll.getScrollName(), scroll.getDateAdded());
                } else {
                    return String.format("Version %d: %s (Added: %s)", scroll.getVersion(), scroll.getScrollName(), scroll.getDateAdded());
                }
            }
        
            @Override
            public Scroll fromString(String string) {
                return null;
            }
        });
        versionHistoryDropdown.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Scroll>() {
        @Override
        public void changed(ObservableValue<? extends Scroll> observable, Scroll oldScroll, Scroll newScroll) {
            if (newScroll != null) {
                // Redirect to show the selected version of the scroll
                try {
                    sceneManager.showPreviewScroll(newScroll, user);
                } catch (IOException e) {
                    System.out.println("Error showing scrolls preview.");
                }
                
            }
        }
    });

        
    }

    @FXML
    private void viewVersionHistory() {
        loadScrollVersionsHistory();
        versionHistoryDropdown.setVisible(true);
    }

    @FXML
    private void closePreview() {
        sceneManager.showScrollsScene(user);
    }

    @FXML
    private void gotohandleDownloadScroll() {
        ScrollsController.handleDownloadScroll(selectedScroll);
    }
}
