package com.supermarket.pos.controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class LoginFormController {
    public AnchorPane loginContext;
    public JFXTextField txtUserName;
    public JFXPasswordField txtPassword;
    public static boolean isAdmin = false;

    public void btnCancelOnAction(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void btnLoginOnAction(ActionEvent actionEvent) throws IOException {
        URL resource;
        if (txtUserName.getText().equals("admin") && txtPassword.getText().equals("admin")) {
            isAdmin = true;
            resource = this.getClass().getResource("/com/supermarket/pos/view/main-form.fxml");
        } else if (txtUserName.getText().equals("user") && txtPassword.getText().equals("user")) {
            isAdmin = false;
            resource = this.getClass().getResource("/com/supermarket/pos/view/main-form.fxml");
        } else {
            new Alert(Alert.AlertType.INFORMATION, "Incorrect Login Details").show();
            return;
        }
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) (this.loginContext.getScene().getWindow());
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        Platform.runLater(primaryStage::sizeToScene);
    }
}
