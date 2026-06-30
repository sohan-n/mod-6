package com.example.csc325_firebase_webview_auth.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

public class SplashController {

    @FXML
    private Label splashTitle;

    @FXML
    private Button continueButton;

    @FXML
    private void initialize() {
        splashTitle.setText("FSC CSC325 Full Stack Project");
    }

    @FXML
    private void goToLogin() throws IOException {
        App.setRoot("/files/LoginView.fxml");
    }
}
