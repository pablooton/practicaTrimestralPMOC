package edu.pmoc.practicatrim.practicatrimestralpmoc.controller;

import edu.pmoc.practicatrim.practicatrimestralpmoc.dao.JugadorDao;
import edu.pmoc.practicatrim.practicatrimestralpmoc.dao.JugadorDaoImpl;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.Jugador;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class JugadoresController implements Initializable {

    @FXML private TableView<Jugador> tablaJugadores;
    @FXML private TableColumn<Jugador, Integer> colID;
    @FXML private TableColumn<Jugador, String> colNombre;
    @FXML private TableColumn<Jugador, String> colPosicion;
    @FXML private TableColumn<Jugador, String> colEquipoReal;
    @FXML private TableColumn<Jugador, Integer> colMediaPuntos;
    @FXML private TableColumn<Jugador, Long> colValor;

    @FXML private TextField txtIdJugador;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cmbPosicion;
    @FXML private TextField txtEquipoReal;
    @FXML private TextField txtMediaPuntos;
    @FXML private TextField txtValor;


    private final JugadorDao jugadorDao = new JugadorDaoImpl();
    private final ObservableList<Jugador> jugadores = FXCollections.observableArrayList();
    private static final String[] POSICIONES = {"POR", "DEF", "MED", "DEL"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTabla();
        cmbPosicion.setItems(FXCollections.observableArrayList(POSICIONES));
        cargarJugadores();

        tablaJugadores.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mostrarDetallesJugador(newSelection);
            } else {
                limpiarCampos();
            }
        });
    }


    private void configurarTabla() {
        colID.setCellValueFactory(new PropertyValueFactory<>("idJugador"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPosicion.setCellValueFactory(new PropertyValueFactory<>("posicion"));
        colEquipoReal.setCellValueFactory(new PropertyValueFactory<>("equipoLiga"));
        colMediaPuntos.setCellValueFactory(new PropertyValueFactory<>("mediaPuntos"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valorMercado"));

        tablaJugadores.setItems(jugadores);
    }

    public void cargarJugadores() {
        try {
            jugadores.clear();
            jugadores.addAll(jugadorDao.getAllJugadores());
        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Conexión", "No se pudieron cargar los datos de los jugadores: " + e.getMessage());
        }
    }


    private void mostrarDetallesJugador(Jugador jugador) {
        if (jugador != null) {
            txtIdJugador.setText(String.valueOf(jugador.getIdJugador()));
            txtNombre.setText(jugador.getNombre());
            cmbPosicion.setValue(jugador.getPosicion());
            txtEquipoReal.setText(jugador.getEquipoLiga());
            txtMediaPuntos.setText(String.valueOf(jugador.getMediaPuntos()));
            txtValor.setText(String.valueOf(jugador.getValorMercado()));
        }
    }

    private void limpiarCampos() {
        txtIdJugador.setText("0");
        txtNombre.clear();
        cmbPosicion.setValue(null);
        txtEquipoReal.clear();
        txtMediaPuntos.clear();
        txtValor.clear();
    }




    @FXML
    public void manageAdd(ActionEvent actionEvent) {
        if (!validarCampos()) {
            return;
        }

        try {
            Jugador nuevoJugador = crearJugadorDesdeFormulario(0);
            jugadorDao.addJugador(nuevoJugador);
            cargarJugadores();
            limpiarCampos();
            mostrarAlerta(Alert.AlertType.CONFIRMATION, "Éxito", "Jugador agregado correctamente.");

        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Operación", "No se pudo agregar el jugador: " + e.getMessage());
        }
    }

    @FXML
    public void manageUpdate(ActionEvent actionEvent) {
        Jugador seleccionado = tablaJugadores.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "No seleccionado", "Debes seleccionar un jugador para editar.");
            return;
        }

        if (!validarCampos()) {
            return;
        }

        try {
            int id = Integer.parseInt(txtIdJugador.getText());
            Jugador jugadorActualizado = crearJugadorDesdeFormulario(id);

            jugadorDao.updateJugador(jugadorActualizado);
            cargarJugadores();
            limpiarCampos();
            mostrarAlerta(Alert.AlertType.CONFIRMATION, "Éxito", "Jugador actualizado correctamente.");

        } catch (RuntimeException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Operación", "No se pudo actualizar el jugador: " + e.getMessage());
        }
    }

    @FXML
    public void manageDelete(ActionEvent actionEvent) {
        Jugador seleccionado = tablaJugadores.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "No seleccionado", "Debes seleccionar un jugador para eliminar.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Estás seguro de eliminar el jugador: " + seleccionado.getNombre() + "?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Confirmar Eliminación");
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            try {
                jugadorDao.eliminarJugador(seleccionado.getIdJugador());
                cargarJugadores();
                limpiarCampos();
                mostrarAlerta(Alert.AlertType.CONFIRMATION, "Éxito", "Jugador eliminado correctamente.");
            } catch (RuntimeException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Operación", "No se pudo eliminar el jugador. Asegúrate de que no pertenezca a un equipo. Error: " + e.getMessage());
            }
        }
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty() || cmbPosicion.getValue() == null ||
                txtEquipoReal.getText().trim().isEmpty() || txtMediaPuntos.getText().trim().isEmpty() ||
                txtValor.getText().trim().isEmpty()) {

            mostrarAlerta(Alert.AlertType.WARNING, "Campos Incompletos", "Por favor, completa todos los campos del formulario.");
            return false;
        }

        try {
            Integer.parseInt(txtMediaPuntos.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Formato", "La Media de Puntos debe ser un número entero.");
            return false;
        }

        try {
            Long.parseLong(txtValor.getText().trim());
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Formato", "El Valor de Mercado debe ser un número entero (Long).");
            return false;
        }

        return true;
    }

    private Jugador crearJugadorDesdeFormulario(int id) {
        return new Jugador(
                id,
                txtNombre.getText().trim(),
                Long.parseLong(txtValor.getText().trim()),
                Integer.parseInt(txtMediaPuntos.getText().trim()),
                cmbPosicion.getValue(),
                txtEquipoReal.getText().trim(),
                true
        );
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}