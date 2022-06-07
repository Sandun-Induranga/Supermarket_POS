package com.supermarket.pos.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class MainFormController {

    public FontAwesomeIconView btnCustomer;
    public FontAwesomeIconView btnItem;
    public FontAwesomeIconView btnPlaceOrder;
    public FontAwesomeIconView btnInfo;
    public FontAwesomeIconView btnLogOut;
    public Label lblMenu;
    public AnchorPane context;

    public void initialize() {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(2000), context);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    @FXML
    private void playMouseExitAnimation(MouseEvent event) {
        if (event.getSource() instanceof FontAwesomeIconView) {
            FontAwesomeIconView icon = (FontAwesomeIconView) event.getSource();
            ScaleTransition scaleT = new ScaleTransition(Duration.millis(200), icon);
            scaleT.setToX(1);
            scaleT.setToY(1);
            scaleT.play();

            icon.setEffect(null);
            lblMenu.setText("Welcome");
        }
    }

    @FXML
    private void playMouseEnterAnimation(MouseEvent event) {
        if (event.getSource() instanceof FontAwesomeIconView) {
            FontAwesomeIconView icon = (FontAwesomeIconView) event.getSource();

            ScaleTransition scaleT = new ScaleTransition(Duration.millis(200), icon);
            scaleT.setToX(1.2);
            scaleT.setToY(1.2);
            scaleT.play();

            DropShadow glow = new DropShadow();
            glow.setColor(Color.valueOf("#EF233C"));
            glow.setWidth(20);
            glow.setHeight(20);
            glow.setRadius(20);
            icon.setEffect(glow);
        }
    }

    @FXML
    private void navigate(MouseEvent event) throws IOException {
        if (event.getSource() instanceof FontAwesomeIconView) {
            FontAwesomeIconView icon = (FontAwesomeIconView) event.getSource();

            Parent root = null;

            switch (icon.getId()) {
                case "btnCustomer":
                    root = FXMLLoader.load(this.getClass().getResource("/com/supermarket/pos/view/manage-customers-form.fxml"));
                    break;
                case "btnItem":
                    root = FXMLLoader.load(this.getClass().getResource("/com/supermarket/pos/view/manage-items-form.fxml"));
                    break;
                case "btnPlaceOrder":
                    root = FXMLLoader.load(this.getClass().getResource("/com/supermarket/pos/view/place-order-form.fxml"));
                    break;
                case "btnInfo":
                    root = FXMLLoader.load(this.getClass().getResource("/com/supermarket/pos/view/manage-order-details-form.fxml"));
                    break;
                case "btnLogOut":
                    root = FXMLLoader.load(this.getClass().getResource("/com/supermarket/pos/view/login-form.fxml"));
                    break;
            }

            if (root != null) {
                Scene subScene = new Scene(root);
                Stage primaryStage = (Stage) this.context.getScene().getWindow();
                primaryStage.setScene(subScene);
                primaryStage.centerOnScreen();

                TranslateTransition tt = new TranslateTransition(Duration.millis(350), subScene.getRoot());
                tt.setFromX(-subScene.getWidth());
                tt.setToX(0);
                tt.play();

            }
        }
    }
}
