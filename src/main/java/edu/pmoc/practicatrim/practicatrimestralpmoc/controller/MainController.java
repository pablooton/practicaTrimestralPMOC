package edu.pmoc.practicatrim.practicatrimestralpmoc.controller;

import edu.pmoc.practicatrim.practicatrimestralpmoc.AppView;
import edu.pmoc.practicatrim.practicatrimestralpmoc.ViewSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    private BorderPane mainContentPane;

    @FXML
    public void initialize() {
        ViewSwitcher.setMainContentPane(mainContentPane);
        ViewSwitcher.switchView(AppView.USERS);
    }

    @FXML
    public void handleShowUsers(ActionEvent actionEvent) {
        ViewSwitcher.switchView(AppView.USERS);
    }

    @FXML
    public void handleShowUserProfile(ActionEvent actionEvent) {
        ViewSwitcher.switchView(AppView.PROFILE);
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            Stage stage = (Stage) mainContentPane.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/pmoc/practicatrim/practicatrimestralpmoc/login-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root,320, 240);
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "No se pudo cargar la pantalla de login.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}