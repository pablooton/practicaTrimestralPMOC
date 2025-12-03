package edu.pmoc.practicatrim.practicatrimestralpmoc.controller;

import edu.pmoc.practicatrim.practicatrimestralpmoc.SessionManager;
import edu.pmoc.practicatrim.practicatrimestralpmoc.dao.*;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.EquipoFantasy;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.Jugador;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class MarketController implements Initializable {

    @FXML public TableView<Jugador> marketTable;
    @FXML public TableColumn<Jugador, String> colNombre;
    @FXML public TableColumn<Jugador, String> colPosicion;
    @FXML public TableColumn<Jugador, Integer> colMediaPuntos;
    @FXML public TableColumn<Jugador, Long> colValorMercado;
    @FXML public Label lblPresupuesto;

    @FXML public VBox detailPane;
    @FXML public TextField txtDetalleNombre;
    @FXML public TextField txtDetallePosicion;
    @FXML public TextField txtDetalleEquipoLiga;
    @FXML public TextField txtDetalleMediaPuntos;
    @FXML public TextField txtDetalleValorMercado;
    @FXML public Button btnComprar;

    private final JugadorDao jugadorDAO = new JugadorDaoImpl();
    private final EquipoFantasyDao equipoFantasyDAO = new EquipoFantasyDaoImpl();
    public TextField buscarField;
    private Jugador jugadorSeleccionado;
    private EquipoFantasy equipoUsuario;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("es", "ES"));

    private  FilteredList<Jugador> jugadoresFilter;
    private  ObservableList<Jugador> jugadoresMercado;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        jugadoresMercado = FXCollections.observableArrayList();
        jugadoresFilter = new FilteredList<>(jugadoresMercado,p->true);

        marketTable.setItems(jugadoresFilter);

        configurarColumnas();
        cargarPresupuestoUsuario();
        cargarDatosMercado();

        detailPane.setDisable(true);
        btnComprar.setDisable(true);

        marketTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            jugadorSeleccionado = newSelection;
            if (jugadorSeleccionado != null) {
                mostrarDetallesJugador(jugadorSeleccionado);
                btnComprar.setDisable(equipoUsuario == null);
            } else {
                limpiarDetallesJugador();
            }
        });
        buscarField.textProperty().addListener((observable, oldValue, newValue) -> {
            jugadoresFilter.setPredicate(jugador ->{
                if (newValue == null || newValue.isEmpty()){
                    return true;
                }

                String lowercaseFilter = newValue.toLowerCase();


                if (jugador.getNombre().toLowerCase().contains(lowercaseFilter)){
                    return true;
                }

                if (jugador.getPosicion().toLowerCase().contains(lowercaseFilter)){
                    return true;
                }

                return false;
            });
        });
    }

    private void configurarColumnas() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPosicion.setCellValueFactory(new PropertyValueFactory<>("posicion"));
        colMediaPuntos.setCellValueFactory(new PropertyValueFactory<>("mediaPuntos"));
        colValorMercado.setCellValueFactory(new PropertyValueFactory<>("valorMercado"));

        colValorMercado.setCellFactory(tc -> new TableCell<Jugador, Long>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : currencyFormatter.format(item));
            }
        });
    }

    private void cargarPresupuestoUsuario() {
        Usuario usuarioActual = SessionManager.getInstance().getCurrentUser();
        if (usuarioActual != null) {
            equipoUsuario = equipoFantasyDAO.getEquipoByUserId(usuarioActual.getIdUsuario());
            if (equipoUsuario != null) {
                actualizarLabelPresupuesto(equipoUsuario.getPresupuesto());
            } else {
                lblPresupuesto.setText("¡Crea un equipo primero!");
            }
        } else {
            lblPresupuesto.setText("Usuario no logueado.");
        }
    }

    private void cargarDatosMercado() {
        try {
            jugadoresMercado.clear();
            List<Jugador> jugadores = jugadorDAO.sacarJugadoresMercado();
            jugadoresMercado.addAll(jugadores);
        } catch (RuntimeException e) {
            showAlert("Error de Carga", "No se pudieron cargar los jugadores del mercado.", Alert.AlertType.ERROR);
        }
    }

    private void mostrarDetallesJugador(Jugador jugador) {
        if (jugador != null) {
            detailPane.setDisable(false);
            txtDetalleNombre.setText(jugador.getNombre());
            txtDetallePosicion.setText(jugador.getPosicion());
            txtDetalleEquipoLiga.setText(jugador.getEquipoLiga());
            txtDetalleMediaPuntos.setText(String.valueOf(jugador.getMediaPuntos()));
            txtDetalleValorMercado.setText(currencyFormatter.format(jugador.getValorMercado()));
        }
    }

    private void limpiarDetallesJugador() {
        detailPane.setDisable(true);
        txtDetalleNombre.setText("");
        txtDetallePosicion.setText("");
        txtDetalleEquipoLiga.setText("");
        txtDetalleMediaPuntos.setText("");
        txtDetalleValorMercado.setText("");
        btnComprar.setDisable(true);
    }

    private void actualizarLabelPresupuesto(long presupuesto) {
        lblPresupuesto.setText(currencyFormatter.format(presupuesto));
    }

    @FXML
    public void handleFicharJugador(ActionEvent actionEvent) {
        if (jugadorSeleccionado == null || equipoUsuario == null) {
            showAlert("Error", "Asegúrate de seleccionar un jugador y tener un equipo.", Alert.AlertType.WARNING);
            return;
        }

        long precioFichaje = jugadorSeleccionado.getValorMercado();

        if (equipoUsuario.getPresupuesto() < precioFichaje) {
            showAlert("Error de Fichaje", "Presupuesto insuficiente. Necesitas " + currencyFormatter.format(precioFichaje) + ".", Alert.AlertType.ERROR);
            return;
        }

        boolean exito = equipoFantasyDAO.ficharJugador(
                equipoUsuario.getIdEquipo(),
                jugadorSeleccionado.getIdJugador(),
                precioFichaje
        );

        if (exito) {
            showAlert("Fichaje Exitoso", jugadorSeleccionado.getNombre() + " ha sido fichado.", Alert.AlertType.INFORMATION);

            equipoUsuario.setPresupuesto(equipoUsuario.getPresupuesto() - precioFichaje);
            actualizarLabelPresupuesto(equipoUsuario.getPresupuesto());

            jugadoresMercado.remove(jugadorSeleccionado);
            marketTable.getSelectionModel().clearSelection();
            limpiarDetallesJugador();

        } else {
            showAlert("Error de Fichaje", "No se pudo completar el fichaje.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}