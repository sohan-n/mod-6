package com.example.csc325_firebase_webview_auth.view;

import com.example.csc325_firebase_webview_auth.model.AuthHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void register() throws IOException {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Missing Info", "Email and password are required.");
            return;
        }

        if (AuthHelper.register(email, password)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setContentText("Account created. You can sign in now.");
            alert.showAndWait();
            App.setRoot("/files/LoginView.fxml");
        } else {
            showAlert("Register Failed", "Could not create account.");
        }
    }

    @FXML
    private void goToLogin() throws IOException {
        App.setRoot("/files/LoginView.fxml");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
