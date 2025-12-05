package edu.pmoc.practicatrim.practicatrimestralpmoc.controller;

import edu.pmoc.practicatrim.practicatrimestralpmoc.dao.EquipoFantasyDao;
import edu.pmoc.practicatrim.practicatrimestralpmoc.dao.EquipoFantasyDaoImpl;
import edu.pmoc.practicatrim.practicatrimestralpmoc.dao.UsuarioDao;
import edu.pmoc.practicatrim.practicatrimestralpmoc.dao.UsuarioDaoImpl;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.EquipoFantasy;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class EquiposController implements Initializable {

    @FXML private TableView<EquipoFantasy> tablaEquipos;
    @FXML private TableColumn<EquipoFantasy, Integer> colIDEquipo;
    @FXML private TableColumn<EquipoFantasy, String> colNombre;
    @FXML private TableColumn<EquipoFantasy, Integer> colIDUsuario;
    @FXML private TableColumn<EquipoFantasy, Long> colPresupuesto;

    @FXML private TextField txtIdEquipo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtIdUsuario;
    @FXML private TextField txtPresupuesto;

    private final EquipoFantasyDao equipoDao = new EquipoFantasyDaoImpl();
    private final UsuarioDao usuarioDao = new UsuarioDaoImpl();
    private final ObservableList<EquipoFantasy> equipos = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTabla();
        cargarEquipos();

        tablaEquipos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mostrarDetallesEquipo(newSelection);
            } else {
                limpiarCampos();
            }
        });
    }

    private void configurarTabla() {
        colIDEquipo.setCellValueFactory(new PropertyValueFactory<>("idEquipo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colIDUsuario.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colPresupuesto.setCellValueFactory(new PropertyValueFactory<>("presupuesto"));

        tablaEquipos.setItems(equipos);
    }

    public void cargarEquipos() {
        try {
            equipos.clear();
            equipos.addAll(equipoDao.getAllEquipos());
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Conexión", "No se pudieron cargar los datos de los equipos: " + e.getMessage());
        }
    }

    private void mostrarDetallesEquipo(EquipoFantasy equipo) {
        if (equipo != null) {
            txtIdEquipo.setText(String.valueOf(equipo.getIdEquipo()));
            txtNombre.setText(equipo.getNombre());
            txtIdUsuario.setText(String.valueOf(equipo.getIdUsuario()));
            txtPresupuesto.setText(String.valueOf(equipo.getPresupuesto()));
        }
    }

    private void limpiarCampos() {
        txtIdEquipo.setText("0");
        txtNombre.clear();
        txtIdUsuario.clear();
        txtPresupuesto.clear();
    }

    @FXML
    public void handleAddTeam(ActionEvent actionEvent) {
        if (!validarCampos()) {
            return;
        }

        try {
            int idUsuario = Integer.parseInt(txtIdUsuario.getText().trim());

            if (equipoDao.getEquipoByUserId(idUsuario) != null) {
                mostrarAlerta(Alert.AlertType.ERROR,
                        "Límite de Equipos",
                        "El usuario con ID " + idUsuario + " ya tiene un equipo asignado. Un usuario solo puede tener un equipo.");
                        limpiarCampos();
                return;
            }
            EquipoFantasy nuevoEquipo = crearEquipoDesdeFormulario(0);
            equipoDao.addEquipo(nuevoEquipo);

            cargarEquipos();
            limpiarCampos();
            mostrarAlerta(Alert.AlertType.CONFIRMATION, "Éxito", "Equipo agregado correctamente.");

        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Operación", "No se pudo agregar el equipo: " + e.getMessage());
        }
    }

    @FXML
    public void handleUpdateTeam(ActionEvent actionEvent) {
        if (tablaEquipos.getSelectionModel().getSelectedItem() == null || Integer.parseInt(txtIdEquipo.getText()) == 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "No seleccionado", "Debes seleccionar un equipo para editar.");
            return;
        }

        if (!validarCampos()) {
            return;
        }

        try {
            int id = Integer.parseInt(txtIdEquipo.getText());
            EquipoFantasy equipoActualizado = crearEquipoDesdeFormulario(id);

            equipoDao.updateEquipo(equipoActualizado);

            cargarEquipos();
            limpiarCampos();

            mostrarAlerta(Alert.AlertType.CONFIRMATION, "Éxito", "Equipo actualizado correctamente.");

        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Operación", "No se pudo actualizar el equipo: " + e.getMessage());
        }
    }

    @FXML
    public void handleDeleteTeam(ActionEvent actionEvent) {
        EquipoFantasy seleccionado = tablaEquipos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "No seleccionado", "Debes seleccionar un equipo para eliminar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Estás seguro de eliminar el equipo: " + seleccionado.getNombre() + "?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Confirmar Eliminación");
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            try {
                equipoDao.eliminarEquipo(seleccionado.getIdEquipo());
                cargarEquipos();
                limpiarCampos();
                mostrarAlerta(Alert.AlertType.CONFIRMATION, "Éxito", "Equipo eliminado correctamente.");
            } catch (RuntimeException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Operación", "No se pudo eliminar el equipo. Error: " + e.getMessage());
            }
        }
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty() || txtIdUsuario.getText().trim().isEmpty() ||
                txtPresupuesto.getText().trim().isEmpty()) {

            mostrarAlerta(Alert.AlertType.WARNING, "Campos Incompletos", "Por favor, completa todos los campos del formulario.");
            return false;
        }

        int idUsuario;
        try {
            idUsuario = Integer.parseInt(txtIdUsuario.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Formato", "El ID de Usuario debe ser un número entero.");
            return false;
        }


        try {
            if (!usuarioDao.existeIdUsuario(idUsuario)) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Referencia", "El ID de Usuario (" + idUsuario + ") no existe en la base de datos.");
                return false;
            }
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Conexión", "Error al verificar la existencia del usuario: " + e.getMessage());
            return false;
        }

        return true;
    }

    private EquipoFantasy crearEquipoDesdeFormulario(int id) {
        return new EquipoFantasy(
                id,
                txtNombre.getText().trim(),
                Integer.parseInt(txtIdUsuario.getText().trim()),
                Long.parseLong(txtPresupuesto.getText().trim())
        );
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public void handleClearFields(ActionEvent actionEvent) {
        limpiarCampos();
    }
}