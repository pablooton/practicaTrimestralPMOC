package edu.pmoc.practicatrim.practicatrimestralpmoc.controller;

import edu.pmoc.practicatrim.practicatrimestralpmoc.dao.UsuarioDao;
import edu.pmoc.practicatrim.practicatrimestralpmoc.dao.UsuarioDaoImpl;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class UsersBdController implements Initializable {


    public Button btnDelete;
    @FXML Button btnUpdateAdmin;
    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, Integer> colID;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colApellido;
    @FXML private TableColumn<Usuario, String> colNickname;
    @FXML private TableColumn<Usuario, Boolean> colIsAdmin;


    @FXML private TextField txtIdUsuario;
    @FXML private TextField txtNickname;
    @FXML private CheckBox chkIsAdmin;


    private final UsuarioDao usuarioDao = new UsuarioDaoImpl();
    private final ObservableList<Usuario> usuarios = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTabla();
        cargarUsuarios();

        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mostrarDetallesUsuario(newSelection);
            } else {
                limpiarCampos();
            }
        });
    }

    private void configurarTabla() {
        colID.setCellValueFactory(new PropertyValueFactory<>("idusuario"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colNickname.setCellValueFactory(new PropertyValueFactory<>("nickname"));
        colIsAdmin.setCellValueFactory(new PropertyValueFactory<>("admin"));

        tablaUsuarios.setItems(usuarios);
    }

    public void cargarUsuarios() {
        try {
            usuarios.clear();

            usuarios.addAll(usuarioDao.getSelectedAllUsers());
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Conexión", "No se pudieron cargar los datos de los usuarios: " + e.getMessage());
        }
    }

    private void mostrarDetallesUsuario(Usuario usuario) {
        if (usuario != null) {
            txtIdUsuario.setText(String.valueOf(usuario.getIdusuario()));
            txtNickname.setText(usuario.getNickname());
            chkIsAdmin.setSelected(usuario.isAdmin());
        }
    }

    private void limpiarCampos() {
        txtIdUsuario.clear();
        txtNickname.clear();
        chkIsAdmin.setSelected(false);
    }


    @FXML
    public void handleUpdateAdminStatus(ActionEvent actionEvent) {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            return;
        }

        try {
            int id = seleccionado.getIdusuario();
            boolean nuevoEstadoAdmin = chkIsAdmin.isSelected();


            usuarioDao.updateUserAdminStatus(id, nuevoEstadoAdmin);

            cargarUsuarios();
            mostrarAlerta(Alert.AlertType.CONFIRMATION, "Éxito", "Permisos de usuario actualizados.");

        } catch (RuntimeException e) {

        }
    }

    @FXML
    public void handleDeleteUser(ActionEvent actionEvent) {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "No seleccionado", "Debes seleccionar un usuario para eliminar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Estás seguro de eliminar al usuario: " + seleccionado.getNickname() + "?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Confirmar Eliminación");
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            try {
                usuarioDao.deleteUser(seleccionado.getIdusuario());
                cargarUsuarios();
                limpiarCampos();
                mostrarAlerta(Alert.AlertType.CONFIRMATION, "Éxito", "Usuario eliminado correctamente.");
            } catch (RuntimeException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Operación", "No se pudo eliminar el usuario. Error: " + e.getMessage());
            }
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}