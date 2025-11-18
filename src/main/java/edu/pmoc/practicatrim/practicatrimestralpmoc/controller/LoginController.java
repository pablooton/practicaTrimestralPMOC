package edu.pmoc.practicatrim.practicatrimestralpmoc.controller;

import edu.pmoc.practicatrim.practicatrimestralpmoc.SessionManager;
import edu.pmoc.practicatrim.practicatrimestralpmoc.dao.UsuarioDao;
import edu.pmoc.practicatrim.practicatrimestralpmoc.dao.UsuarioDaoImpl;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.Usuario;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public BorderPane mainPane;
    public HBox tituloContainer;
    public VBox contenedorLogin;
    public Label nombreU;
    public TextField nombreT;
    public Label contraseñaU;
    public PasswordField contraseñaT;
    public Button _loginButton;

    private final UsuarioDao usuarioDao = new UsuarioDaoImpl();


    public void manageLogin(ActionEvent actionEvent) {
        String username = nombreT.getText();
        String password = contraseñaT.getText();

        if (usuarioDao.validarCredenciales(username,password)){
            Usuario usuario = usuarioDao.getUserByNickname(username);
            SessionManager.getInstance().setCurrentUser(usuario);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> nombreT.requestFocus());
    }
}