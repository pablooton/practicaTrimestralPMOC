package edu.pmoc.practicatrim.practicatrimestralpmoc.controller;

import edu.pmoc.practicatrim.practicatrimestralpmoc.AppView;
import edu.pmoc.practicatrim.practicatrimestralpmoc.SessionManager;
import edu.pmoc.practicatrim.practicatrimestralpmoc.dao.UsuarioDao;
import edu.pmoc.practicatrim.practicatrimestralpmoc.dao.UsuarioDaoImpl;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.Usuario;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML public BorderPane mainPane;
    @FXML public HBox tituloContainer;
    @FXML public VBox contenedorLogin;
    @FXML public Label nombreU;
    @FXML public TextField nombreT;
    @FXML public Label contraseñaU;
    @FXML public PasswordField contraseñaT;
    @FXML public Button _loginButton;

    private final UsuarioDao usuarioDao = new UsuarioDaoImpl();
    @FXML
    public void manageLogin(ActionEvent actionEvent) {
        String username = nombreT.getText();
        String password = contraseñaT.getText();


        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Campos vacíos", "Por favor, introduce usuario y contraseña.");
            return;
        }


        if (usuarioDao.validarCredenciales(username, password)) {
            Usuario usuario = usuarioDao.getUserByNickname(username);
            SessionManager.getInstance().setCurrentUser(usuario);
            loadMainView();
        } else {
            showAlert("Login Fallido", "Usuario o contraseña incorrectos.");
        }
    }

    private void loadMainView() {
        try {
            Stage stage = (Stage) _loginButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppView.MAIN.getFxmlFile()));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error Crítico", "No se pudo cargar la vista principal.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> nombreT.requestFocus());
    }
}