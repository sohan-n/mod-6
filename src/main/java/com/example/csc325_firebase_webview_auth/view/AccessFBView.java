package com.example.csc325_firebase_webview_auth.view;

import com.example.csc325_firebase_webview_auth.model.Person;
import com.example.csc325_firebase_webview_auth.model.StorageHelper;
import com.example.csc325_firebase_webview_auth.viewmodel.AccessDataViewModel;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class AccessFBView {

    @FXML
    private TextField nameField;
    @FXML
    private TextField majorField;
    @FXML
    private TextField ageField;
    @FXML
    private Button writeButton;
    @FXML
    private Button readButton;
    @FXML
    private TableView<Person> personTable;
    @FXML
    private TableColumn<Person, String> nameColumn;
    @FXML
    private TableColumn<Person, String> majorColumn;
    @FXML
    private TableColumn<Person, Integer> ageColumn;
    @FXML
    private ImageView profileImageView;

    private String currentImageUrl = "";
    private ObservableList<Person> listOfUsers = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        AccessDataViewModel accessDataViewModel = new AccessDataViewModel();
        nameField.textProperty().bindBidirectional(accessDataViewModel.userNameProperty());
        majorField.textProperty().bindBidirectional(accessDataViewModel.userMajorProperty());
        writeButton.disableProperty().bind(accessDataViewModel.isWritePossibleProperty().not());

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        majorColumn.setCellValueFactory(new PropertyValueFactory<>("major"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        personTable.setItems(listOfUsers);

        loadDefaultImage();

        personTable.setOnMouseClicked(e -> {
            Person selected = personTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                nameField.setText(selected.getName());
                majorField.setText(selected.getMajor());
                ageField.setText(String.valueOf(selected.getAge()));
                currentImageUrl = selected.getImageUrl();
                loadImage(currentImageUrl);
            }
        });
    }

    @FXML
    private void addRecord(ActionEvent event) {
        addData();
    }

    @FXML
    private void readRecord(ActionEvent event) {
        readFirebase();
    }

    @FXML
    private void uploadPhoto(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Profile Picture");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File file = chooser.showOpenDialog(profileImageView.getScene().getWindow());
        if (file != null) {
            String url = StorageHelper.uploadFile(file);
            if (url != null && !url.isEmpty()) {
                currentImageUrl = url;
                loadImage(url);
            } else {
                showAlert("Upload Failed", "Could not upload photo.");
            }
        }
    }

    @FXML
    private void goSignIn(ActionEvent event) throws IOException {
        App.setRoot("/files/LoginView.fxml");
    }

    @FXML
    private void goRegister(ActionEvent event) throws IOException {
        App.setRoot("/files/RegisterView.fxml");
    }

    @FXML
    private void exitApp(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void deleteRecord(ActionEvent event) {
        Person selected = personTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a row to delete.");
            return;
        }
        if (selected.getId() != null && !selected.getId().isEmpty()) {
            App.fstore.collection("References").document(selected.getId()).delete();
        }
        listOfUsers.remove(selected);
        nameField.clear();
        majorField.clear();
        ageField.clear();
        currentImageUrl = "";
        loadDefaultImage();
    }

    @FXML
    private void showAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("FSC CSC325 Full Stack Project");
        alert.setContentText("Student Management System");
        alert.showAndWait();
    }

    public void addData() {
        String docId = UUID.randomUUID().toString();
        DocumentReference docRef = App.fstore.collection("References").document(docId);

        Map<String, Object> data = new HashMap<>();
        data.put("Name", nameField.getText());
        data.put("Major", majorField.getText());
        data.put("Age", Integer.parseInt(ageField.getText()));
        if (currentImageUrl != null && !currentImageUrl.isEmpty()) {
            data.put("imageUrl", currentImageUrl);
        }

        ApiFuture<WriteResult> result = docRef.set(data);
    }

    public void readFirebase() {
        listOfUsers.clear();
        ApiFuture<QuerySnapshot> future = App.fstore.collection("References").get();
        List<QueryDocumentSnapshot> documents;
        try {
            documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                String imageUrl = "";
                if (document.getData().get("imageUrl") != null) {
                    imageUrl = document.getData().get("imageUrl").toString();
                }
                Person person = new Person(
                        document.getId(),
                        String.valueOf(document.getData().get("Name")),
                        document.getData().get("Major").toString(),
                        Integer.parseInt(document.getData().get("Age").toString()),
                        imageUrl
                );
                listOfUsers.add(person);
            }
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }
    }

    private void loadDefaultImage() {
        loadImage("https://cdn-icons-png.flaticon.com/512/149/149071.png");
    }

    private void loadImage(String url) {
        if (url == null || url.trim().isEmpty()) {
            profileImageView.setImage(null);
            return;
        }
        try {
            Image img = new Image(url, true);
            profileImageView.setImage(img);
        } catch (Exception ex) {
            profileImageView.setImage(null);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
