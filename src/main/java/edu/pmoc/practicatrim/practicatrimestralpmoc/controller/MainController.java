package edu.pmoc.practicatrim.practicatrimestralpmoc.controller;

import edu.pmoc.practicatrim.practicatrimestralpmoc.AppView;
import edu.pmoc.practicatrim.practicatrimestralpmoc.SessionManager;
import edu.pmoc.practicatrim.practicatrimestralpmoc.ViewSwitcher;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private BorderPane mainContentPane;
    @FXML private Menu menuGestionDatos;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ViewSwitcher.setMainContentPane(mainContentPane);
        ViewSwitcher.switchView(AppView.USERS);
        configurarPermisosMenu();
    }

    public void configurarPermisosMenu() {
        Usuario usuarioActual = SessionManager.getInstance().getCurrentUser();

        if (menuGestionDatos != null&& usuarioActual.isAdmin()) {
            menuGestionDatos.setVisible(true);
        }else {
            menuGestionDatos.setVisible(false);
        }
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
        SessionManager.getInstance().setCurrentUser(null);

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

    public void handleMercado(ActionEvent actionEvent) {
        ViewSwitcher.switchView(AppView.MARKET);
    }

    public void handleJugadores(ActionEvent actionEvent) {
        ViewSwitcher.switchView(AppView.PLAYERS);
    }

    public void handleUsuarios(ActionEvent actionEvent) {
        ViewSwitcher.switchView(AppView.USERSBD);
    }

    public void handleEquipos(ActionEvent actionEvent) {
        ViewSwitcher.switchView(AppView.TEAMS);
    }
}