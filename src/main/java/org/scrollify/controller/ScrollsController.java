package org.scrollify.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.scrollify.model.Scroll;
import org.scrollify.scene.SceneManager;
import org.scrollify.service.ScrollService;
import org.scrollify.backend.User;
import org.scrollify.database.DatabaseManager;
import org.scrollify.enums.UserEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Collectors;

public class ScrollsController {

    private SceneManager sceneManager;
    private User user;

    private static final DatabaseManager databaseManager = new DatabaseManager();  // Assuming DatabaseManager is used to fetch scroll files
    private static final ScrollService scrollService = new ScrollService(databaseManager);

    @FXML
    private TextField titleFilter;
    @FXML
    private TextField uploaderIdFilter;
    @FXML
    private DatePicker uploadDateFilter;

    @FXML
    private ListView<HBox> scrollListView;

    @FXML
    private Label userRoleLabel;

    @FXML
    private TextField scrollNameField;

    @FXML
    private Label selectedFileLabel;

    private File selectedFile;

    private Scroll selectedScroll;

    @FXML
    private Button updateProfileButton;

    @FXML
    private Button addNewScrollButton;

    @FXML
    private Button adminDashboardButton;


    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void setUser(User user) {
        this.user = user;

        if (user == null) {
            userRoleLabel.setText("Welcome, Guest");
            scrollListView.setVisible(true);
            adminDashboardButton.setVisible(false);
            addNewScrollButton.setVisible(false);
            updateProfileButton.setVisible(false);
        } else {
            String fullName = user.getFirstName() + " " + user.getLastName();
            String role = (user.getUserType() == UserEnum.USER) ? "User" : "Admin";
            userRoleLabel.setText("Welcome, " + role + " " + fullName);

            adminDashboardButton.setVisible(user.getUserType() == UserEnum.ADMIN);
            addNewScrollButton.setVisible(true);
            scrollListView.setVisible(true);
            updateProfileButton.setVisible(true);
        }
        loadFeaturedScroll();
        loadScrolls();
    }


    @FXML
    private HBox featuredScrollContainer;

    private void loadFeaturedScroll() {
        Scroll featuredScroll = databaseManager.getFeaturedScroll();

        if (featuredScroll != null) {
            HBox featuredScrollBox = createFeaturedScrollContainer(featuredScroll);
            featuredScrollContainer.getChildren().add(featuredScrollBox);
        } else {
            featuredScrollContainer.getChildren().add(new Label("No featured scroll available today!"));
        }
    }


    private void loadScrolls() {
        List<Scroll> scrolls = scrollService.getAllScrolls();
        displayScrolls(scrolls);
    }

    private HBox createFeaturedScrollContainer(Scroll scroll) {
        HBox container = new HBox();
        container.setSpacing(20);
        container.setPadding(new Insets(10));

        container.setStyle("-fx-background-color: gold; -fx-border-color: #dddddd; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Text title = new Text(scroll.getScrollName());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-fill: #2c3e50;");

        Text owner = new Text("Owner: " + scroll.getOwner());
        owner.setStyle("-fx-font-size: 14px; -fx-fill: #7f8c8d;");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        Text dateAdded = new Text("Date Added: " + scroll.getDateAdded().format(formatter));
        dateAdded.setStyle("-fx-font-size: 14px; -fx-fill: #7f8c8d;");

        Button viewButton = new Button("View ->");
        viewButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5px; -fx-padding: 5px 10px;");
        viewButton.setOnAction(event -> handleViewScroll(scroll));

        Button downloadButton = new Button("Download");
        downloadButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5px; -fx-padding: 5px 10px;");
        downloadButton.setVisible(user != null);
        downloadButton.setOnAction(event -> handleDownloadScroll(scroll));

        Button editButton = new Button("Edit");
        editButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5px; -fx-padding: 5px 10px;");
        if (user==null) {
            editButton.setVisible(false);
        } else {
            editButton.setVisible(user.getUsername().equals(scroll.getOwner()));
        }
        editButton.setOnAction(event -> goToEditScroll(scroll));

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #ff0000; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5px; -fx-padding: 5px 10px;");

        if (user==null) {
            deleteButton.setVisible(false);
        } else {
            deleteButton.setVisible(user.getUsername().equals(scroll.getOwner()));
        }

        deleteButton.setOnAction(event -> handleDeleteScroll(scroll));

        container.setOnMouseEntered(event -> container.setStyle("-fx-background-color: #ffeb7f; -fx-border-color: #dddddd; -fx-border-radius: 8px;"));
        container.setOnMouseExited(event -> container.setStyle("-fx-background-color: gold; -fx-border-color: #dddddd; -fx-border-radius: 8px;"));
        container.setOnMouseClicked(event -> container.setStyle("-fx-background-color: #d4af37; -fx-border-color: #dddddd; -fx-border-radius: 8px;"));

        container.getChildren().addAll(title, owner, dateAdded, viewButton, downloadButton, editButton, deleteButton);
        return container;
    }


    private HBox createScrollContainer(Scroll scroll) {
        HBox container = new HBox();
        container.setSpacing(20);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dddddd; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Text title = new Text(scroll.getScrollName());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-fill: #2c3e50;");

        Text owner = new Text("Owner: " + scroll.getOwner());
        owner.setStyle("-fx-font-size: 14px; -fx-fill: #7f8c8d;");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        Text dateAdded = new Text("Date Added: " + scroll.getDateAdded().format(formatter));
        dateAdded.setStyle("-fx-font-size: 14px; -fx-fill: #7f8c8d;");

        Button viewButton = new Button("View ->");
        viewButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5px; -fx-padding: 5px 10px;");
        viewButton.setOnAction(event -> handleViewScroll(scroll));

        Button downloadButton = new Button("Download");
        downloadButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5px; -fx-padding: 5px 10px;");
        downloadButton.setVisible(user != null);
        downloadButton.setOnAction(event -> handleDownloadScroll(scroll));

        Button editButton = new Button("Edit");
        editButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5px; -fx-padding: 5px 10px;");
        if (user==null) {
            editButton.setVisible(false);
        } else {
            editButton.setVisible(user.getUsername().equals(scroll.getOwner()));
        }
        editButton.setOnAction(event -> goToEditScroll(scroll));

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #ff0000; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-radius: 5px; -fx-padding: 5px 10px;");

        if (user==null) {
            deleteButton.setVisible(false);
        } else {
            deleteButton.setVisible(user.getUsername().equals(scroll.getOwner()));
        }
        deleteButton.setOnAction(event -> handleDeleteScroll(scroll));

        container.setOnMouseEntered(event -> container.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #dddddd; -fx-border-radius: 8px;"));
        container.setOnMouseExited(event -> container.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dddddd; -fx-border-radius: 8px;"));

        container.setOnMouseClicked(event -> container.setStyle("-fx-background-color: #d3d3d3; -fx-border-color: #dddddd; -fx-border-radius: 8px;"));

        container.getChildren().addAll(title, owner, dateAdded, viewButton, downloadButton, editButton, deleteButton);
        return container;
    }

    private void displayScrolls(List<Scroll> scrolls) {
        scrollListView.getItems().clear();

        if (scrolls.isEmpty()) {
            HBox noScrollBox = new HBox();
            noScrollBox.getChildren().add(new Label("No scrolls in the library of Edstemus :'("));
            scrollListView.getItems().add(noScrollBox);
        } else {
            ObservableList<HBox> scrollContainers = FXCollections.observableArrayList();
            for (Scroll scroll : scrolls) {
                HBox container = createScrollContainer(scroll);
                scrollContainers.add(container);
            }
            scrollListView.setItems(scrollContainers);
        }
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String title = titleFilter.getText().trim();
        String uploaderId = uploaderIdFilter.getText().trim();
        LocalDate uploadDate = uploadDateFilter.getValue(); // Get the selected date from DatePicker

        List<Scroll> allScrolls = scrollService.getAllScrolls();

        List<Scroll> filteredScrolls = allScrolls.stream()
                .filter(scroll ->
                        (title.isEmpty() || scroll.getScrollName().toLowerCase().contains(title.toLowerCase())) &&
                                (uploaderId.isEmpty() || scroll.getOwner().toLowerCase().contains(uploaderId.toLowerCase())) &&
                                (uploadDate == null || matchesUploadDate(scroll, uploadDate)))
                .collect(Collectors.toList());

        displayScrolls(filteredScrolls);
    }

    private boolean matchesUploadDate(Scroll scroll, LocalDate uploadDate) {
      return scroll.getDateAdded().toLocalDate().isEqual(uploadDate);

    }
    public static void handleDownloadScroll(Scroll scroll) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Destination to Save Scroll");

        File destinationFolder = new File("src/main/resources/scrolls");
        File scrollFile;
        try {
            scrollFile = DatabaseManager.getScrollFileById(scroll.getScrollId() + "#" + scroll.getVersion(), destinationFolder);
            if (scrollFile == null) {
                throw new IOException("Scroll file not found");
            }
        } catch (IOException e) {
            System.out.println("Error retrieving the scroll file: " + e.getMessage());
            return;
        }

        String scrollName = scroll.getScrollName();
        String originalFileName = scrollFile.getName();
        String fileExtension = "";
        int lastDotIndex = originalFileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < originalFileName.length() - 1) {
            fileExtension = originalFileName.substring(lastDotIndex);
        }

        fileChooser.setInitialFileName(scrollName + fileExtension);

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File destinationFile = fileChooser.showSaveDialog(new Stage());

        if (destinationFile != null) {
            try {
                Files.copy(scrollFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Scroll downloaded successfully to: " + destinationFile.getAbsolutePath());
                scrollService.incrementDownloadCount(scroll.getScrollId());
            } catch (IOException e) {
                System.out.println("Error downloading the file: " + e.getMessage());
            }
        }
    }


    public void handleDeleteScroll(Scroll scroll) {
        // Confirm the deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Scroll");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete " + scroll.getScrollName() + "?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response.getButtonData().isDefaultButton()) {
                // Delete the scroll from the database
                boolean success = databaseManager.deleteScroll(scroll.getScrollId());
                if (success) {
                    deleteScrollFiles(scroll.getScrollId());
                    System.out.println("Deleted " + scroll.getScrollName());
                    showAlert("Scroll Deleted", "The scroll was successfully deleted.", Alert.AlertType.INFORMATION);
                    sceneManager.showScrollsScene(user);
                } else {
                    showAlert("Error", "Failed to delete the scroll.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void deleteScrollFiles(String scrollId) {
        File folder = new File("src/main/resources/scrolls");

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.startsWith(scrollId));

            if (files != null) {
                for (File file : files) {
                    if (!file.delete()) {
                        System.out.println("Failed to delete file: " + file.getName());
                    }
                }
            }
        } else {
            System.out.println("Scrolls folder does not exist or is not a directory.");
        }
    }


    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleViewScroll(Scroll scroll) {
        try {
            sceneManager.showPreviewScroll(scroll, user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Label errorMessage = new Label("Error retrieving file!");
            errorMessage.setStyle("-fx-text-fill: red;");
            scrollListView.getItems().add(new HBox(errorMessage));
        }
    }

    @FXML
    private void handleUploadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Scroll File");
        selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            selectedFileLabel.setText("Selected File: " + selectedFile.getName());
        } else {
            selectedFileLabel.setText("No file selected");
        }
    }

    @FXML
    private void handleAddScroll() {
        String scrollName = scrollNameField.getText();
        String owner = user.getUsername();
        String fileFormat = getFileFormat(selectedFile);

        if (scrollName.isEmpty() || selectedFile == null || user.getUserType().equals(UserEnum.GUEST)) {
            return;
        }

        String scrollId = UUID.randomUUID().toString().replace("-", "").substring(0, 32);

        try {
            ScrollService scrollService = new ScrollService(new DatabaseManager());
            File savedFile = saveScrollFile(scrollId, fileFormat);
            scrollService.insertScrollIntoDatabase(scrollId, scrollName, LocalDateTime.now(), owner, scrollService.getLatestScrollVersionByID(scrollId)+1);
            int version = scrollService.getLatestScrollVersionByID(scrollId);
            renameScrollFileWithVersion(scrollId, savedFile, version, fileFormat);

            sceneManager.showScrollsScene(user);
        } catch (IOException e) {
            System.out.println("Error saving the file: " + e.getMessage());
        }
    }

    private String getFileFormat(File file) {
        String fileName = file.getName();
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    private void renameScrollFileWithVersion(String scrollId, File savedFile, int version, String fileFormat) throws IOException {
        File destinationFolder = new File("src/main/resources/scrolls");
        File renamedFile = new File(destinationFolder, scrollId + "#" + version + "." + fileFormat);

        // Rename the file
        if (savedFile.renameTo(renamedFile)) {
            System.out.println("File renamed to: " + renamedFile.getAbsolutePath());
        } else {
            throw new IOException("Failed to rename the file.");
        }
    }


    private File saveScrollFile(String scrollId, String fileFormat) throws IOException {
        File destinationFolder = new File("src/main/resources/scrolls");

        if (!destinationFolder.exists()) {
            destinationFolder.mkdirs();
        }

        File destinationFile = new File(destinationFolder, scrollId + "." + fileFormat);
        Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        return destinationFile; // Return the saved file
    }

    @FXML
    private void Logout() {
        sceneManager.showWelcomeScene();
    }

    @FXML
    private void goToAddScroll() {
        sceneManager.showAddScroll(user);
    }

    private void goToEditScroll(Scroll scroll) {
        sceneManager.showEditScroll(user, scroll);
    }

    @FXML
    private void goToHomepage() {
        sceneManager.showScrollsScene(user);
    }

    @FXML
    private void goToUpdateProfile() {
        sceneManager.showUpdateProfile(user);
    }

    @FXML
    private void goToAdmin() { sceneManager.showAdminScene(user);
    }
}

