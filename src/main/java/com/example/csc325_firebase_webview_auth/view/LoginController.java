package com.example.csc325_firebase_webview_auth.view;

import com.example.csc325_firebase_webview_auth.model.AuthHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void signIn() throws IOException {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Missing Info", "Email and password are required.");
            return;
        }

        if (AuthHelper.signIn(email, password)) {
            App.setRoot("/files/AccessFBView.fxml");
        } else {
            showAlert("Login Failed", "Wrong email or password.");
        }
    }

    @FXML
    private void goToRegister() throws IOException {
        App.setRoot("/files/RegisterView.fxml");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
