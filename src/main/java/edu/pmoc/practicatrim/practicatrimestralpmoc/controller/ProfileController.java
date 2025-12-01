package edu.pmoc.practicatrim.practicatrimestralpmoc.controller;

import edu.pmoc.practicatrim.practicatrimestralpmoc.SessionManager;
import edu.pmoc.practicatrim.practicatrimestralpmoc.dao.UsuarioDao;
import edu.pmoc.practicatrim.practicatrimestralpmoc.dao.UsuarioDaoImpl;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {


    public TextField txtPass;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtNickname;


    private final UsuarioDao usuarioDAO = new UsuarioDaoImpl();


    private Usuario usuarioActual;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarDatosUser();
    }


    private void cargarDatosUser(){

        usuarioActual = SessionManager.getInstance().getCurrentUser();

        if (usuarioActual != null) {

            txtNombre.setText(usuarioActual.getNombre());
            txtApellido.setText(usuarioActual.getApellido());
            txtNickname.setText(usuarioActual.getNickname());

            txtNombre.setEditable(false);
            txtApellido.setEditable(false);
        } else {
            System.err.println("ERROR: No hay usuario en sesión.");
        }
    }


    @FXML
    private void handleUpdateNickname(ActionEvent event) {
        if (usuarioActual == null) {
            showAlert("Error", "No hay usuario para actualizar.", Alert.AlertType.ERROR);
            return;
        }

        String nuevoNickname = txtNickname.getText().trim();

        if (nuevoNickname.isEmpty()) {
            showAlert("Error", "El nickname no puede estar vacío.", Alert.AlertType.WARNING);
            return;
        }
        if (nuevoNickname.equals(usuarioActual.getNickname())) {
            showAlert("Información", "El nickname es el mismo que el actual. No se requiere actualización.", Alert.AlertType.INFORMATION);
            return;
        }
        if (usuarioDAO.existeNickname(nuevoNickname)){
            showAlert("Error", "El nickname '" + nuevoNickname + "' ya está en uso por otro usuario.", Alert.AlertType.ERROR);
            return;
        }

        boolean exito = usuarioDAO.updateNickname(usuarioActual.getIdUsuario(), nuevoNickname);

        if (exito) {

            usuarioActual.setNickname(nuevoNickname);

            showAlert("Éxito", "Nickname actualizado correctamente.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "No se pudo actualizar el nickname en la base de datos.", Alert.AlertType.ERROR);
        }
    }



    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void handleUpdatePassword(ActionEvent actionEvent) {
        if (usuarioActual == null) {
            showAlert("Error", "No hay usuario para actualizar.", Alert.AlertType.ERROR);
            return;
        }
        String nuevaContra = txtPass.getText().trim();
        if (nuevaContra.isEmpty()) {
            showAlert("Error", "La contraseña no puede estar vacío.", Alert.AlertType.WARNING);
            return;
        }
        if (nuevaContra.equals(usuarioActual.getPassword())) {
            showAlert("Información", "La contraseña es la misma que la actual. No se requiere actualización.", Alert.AlertType.INFORMATION);
            return;
        }

        boolean exito = usuarioDAO.updatePassword(usuarioActual.getIdUsuario(), nuevaContra);
        if (exito){
            usuarioActual.setPassword(nuevaContra);

            showAlert("Éxito", "Contraseña actualizada con éxito", Alert.AlertType.INFORMATION);
        }else {
            showAlert("Error", "No se pudo actualizar la contraseña en la base de datos.", Alert.AlertType.ERROR);
        }
    }
}