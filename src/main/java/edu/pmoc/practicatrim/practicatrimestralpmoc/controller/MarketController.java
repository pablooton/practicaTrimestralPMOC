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


    @FXML public TextField buscarField;
    @FXML public Slider valorSlider;
    @FXML public Label lblSliderValue;


    @FXML public CheckBox chkTodos;
    @FXML public CheckBox chkPOR;
    @FXML public CheckBox chkDEF;
    @FXML public CheckBox chkMED;
    @FXML public CheckBox chkDEL;

    private final JugadorDao jugadorDAO = new JugadorDaoImpl();
    private final EquipoFantasyDao equipoFantasyDAO = new EquipoFantasyDaoImpl();
    private Jugador jugadorSeleccionado;
    private EquipoFantasy equipoUsuario;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("es", "ES"));

    private FilteredList<Jugador> jugadoresFilter;
    private ObservableList<Jugador> jugadoresMercado;


    private static final long MAX_VALOR_MILLONES = 35;
    private static final long VALOR_UNITARIO = 1_000_000L;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        jugadoresMercado = FXCollections.observableArrayList();
        jugadoresFilter = new FilteredList<>(jugadoresMercado,p->true);

        marketTable.setItems(jugadoresFilter);

        configurarColumnas();
        cargarPresupuestoUsuario();
        cargarDatosMercado();
        configurarSlider();
        configurarFiltrosPosicion();

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

        buscarField.textProperty().addListener((observable, oldValue, newValue) -> aplicarFiltros());
        valorSlider.valueProperty().addListener((observable, oldValue, newValue) -> aplicarFiltros());

        valorSlider.setValue(MAX_VALOR_MILLONES);
        lblSliderValue.setText("Precio Máximo: " + currencyFormatter.format(MAX_VALOR_MILLONES * VALOR_UNITARIO));
    }

    private void configurarFiltrosPosicion() {

        chkTodos.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                chkPOR.setSelected(false);
                chkDEF.setSelected(false);
                chkMED.setSelected(false);
                chkDEL.setSelected(false);
            }
            aplicarFiltros();
        });

        chkPOR.selectedProperty().addListener(obs -> manejarDeseleccionTotal());
        chkDEF.selectedProperty().addListener(obs -> manejarDeseleccionTotal());
        chkMED.selectedProperty().addListener(obs -> manejarDeseleccionTotal());
        chkDEL.selectedProperty().addListener(obs -> manejarDeseleccionTotal());
    }

    private void manejarDeseleccionTotal() {
        if (!chkPOR.isSelected() && !chkDEF.isSelected() && !chkMED.isSelected() && !chkDEL.isSelected()) {
            chkTodos.setSelected(true);
        } else if (chkTodos.isSelected()) {
            chkTodos.setSelected(false);
        }
        aplicarFiltros();
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

    private void configurarSlider() {
        valorSlider.setMin(0);
        valorSlider.setMax(MAX_VALOR_MILLONES);
        valorSlider.setBlockIncrement(5);

        valorSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            long valorActual = newVal.longValue() * VALOR_UNITARIO;
            lblSliderValue.setText("Precio Máximo: " + currencyFormatter.format(valorActual));
        });
    }

    private void cargarPresupuestoUsuario() {
        Usuario usuarioActual = SessionManager.getInstance().getCurrentUser();
        if (usuarioActual != null) {
            equipoUsuario = equipoFantasyDAO.getEquipoByUserId(usuarioActual.getIdusuario());
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
            aplicarFiltros();
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
    private void aplicarFiltros() {
        final String textoFiltro = buscarField.getText().toLowerCase();
        final long maxValorPermitido = valorSlider.getValue() == 0 ? Long.MAX_VALUE : (long) (valorSlider.getValue() * VALOR_UNITARIO);
        final boolean filtrarPorPosicion = !chkTodos.isSelected();
        final boolean chkPOR_sel = chkPOR.isSelected();
        final boolean chkDEF_sel = chkDEF.isSelected();
        final boolean chkMED_sel = chkMED.isSelected();
        final boolean chkDEL_sel = chkDEL.isSelected();


        jugadoresFilter.setPredicate(jugador -> {
            if (jugador.getValorMercado() > maxValorPermitido) {
                return false;
            }
            if (filtrarPorPosicion) {
                boolean coincidePosicion = false;
                String pos = jugador.getPosicion();

                if (chkPOR_sel && pos.equals("POR")) {
                    coincidePosicion = true;
                } else if (chkDEF_sel && pos.equals("DEF")) {
                    coincidePosicion = true;
                } else if (chkMED_sel && pos.equals("CEN")) {
                    coincidePosicion = true;
                } else if (chkDEL_sel && pos.equals("DEL")) {
                    coincidePosicion = true;
                }

                if (!coincidePosicion) {
                    return false;
                }
            }
            if (textoFiltro.isEmpty()) {
                return true;
            }

            String nombre = jugador.getNombre().toLowerCase();
            String posicion = jugador.getPosicion().toLowerCase();

            if (nombre.contains(textoFiltro) || posicion.contains(textoFiltro)) {
                return true;
            }

            return false;
        });
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
            cargarDatosMercado();
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