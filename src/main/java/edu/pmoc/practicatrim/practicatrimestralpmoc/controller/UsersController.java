package edu.pmoc.practicatrim.practicatrimestralpmoc.controller;

import edu.pmoc.practicatrim.practicatrimestralpmoc.SessionManager;
import edu.pmoc.practicatrim.practicatrimestralpmoc.dao.EquipoFantasyDao;
import edu.pmoc.practicatrim.practicatrimestralpmoc.dao.EquipoFantasyDaoImpl;
import edu.pmoc.practicatrim.practicatrimestralpmoc.dao.UsuarioDao;
import edu.pmoc.practicatrim.practicatrimestralpmoc.dao.UsuarioDaoImpl;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.EquipoFantasy;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.Jugador;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.Usuario;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class UsersController {


    @FXML private Label lblNombreEquipo;
    @FXML private Label lblPropietario;
    @FXML public Label lblPresupuesto;


    @FXML public VBox detailPane;
    @FXML public TextField txtDetalleNombre;
    @FXML public TextField txtDetallePosicion;
    @FXML public TextField txtDetalleMediaPuntos;
    @FXML public TextField txtDetalleValorVenta;
    @FXML public Button btnVender;
    @FXML public Button btnCalcularTiempo;
    @FXML public Label lblTiempoEnClub;


    @FXML private TableView<Jugador> tablaJugadores;
    @FXML private TableColumn<Jugador, String> colNombre;
    @FXML private TableColumn<Jugador, String> colPosicion;
    @FXML private TableColumn<Jugador, String> colEquipoReal;
    @FXML private TableColumn<Jugador, Integer> colPuntos;
    @FXML private TableColumn<Jugador, Long> colValor;

    private final UsuarioDao usuarioDao = new UsuarioDaoImpl();
    private final EquipoFantasyDao equipoFantasyDao = new EquipoFantasyDaoImpl();

    @FXML
    public void initialize() {
        System.out.println("Entrando en initialize");
        configurarColumnas();


        tablaJugadores.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> mostrarDetallesJugador(newValue)
        );

        Usuario usuarioActual = SessionManager.getInstance().getCurrentUser();

        if (usuarioActual != null) {
            EquipoFantasy miEquipo = equipoFantasyDao.getEquipoByUserId(usuarioActual.getIdUsuario());

            if (miEquipo != null) {

                lblNombreEquipo.setText(miEquipo.getNombreEquipo());
                lblPropietario.setText("Entrenador: " + usuarioActual.getNickname());
                lblPresupuesto.setText("Presupuesto: " + miEquipo.getPresupuesto() + " €");


                cargarJugadores(usuarioActual.getIdUsuario());
            } else {
                lblNombreEquipo.setText("Sin Equipo");
                lblPropietario.setText("Necesitas crear un equipo.");
                lblPresupuesto.setText("Presupuesto: N/A");
            }
        } else {
            System.out.println("ES NULO: No hay usuario en la sesión.");
            lblNombreEquipo.setText("Error de Sesión");
            lblPropietario.setText("Debes iniciar sesión.");
        }


        mostrarDetallesJugador(null);
    }

    private void configurarColumnas() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPosicion.setCellValueFactory(new PropertyValueFactory<>("posicion"));
        colPuntos.setCellValueFactory(new PropertyValueFactory<>("mediaPuntos"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valorMercado"));
        colEquipoReal.setCellValueFactory(new PropertyValueFactory<>("equipoLiga"));
    }

    private void cargarJugadores(int idUsuario) {
        ObservableList<Jugador> plantilla = usuarioDao.obtenerJugadoresDelEquipoUsuario(idUsuario);
        tablaJugadores.setItems(plantilla);
    }


    private void mostrarDetallesJugador(Jugador jugador) {
        boolean selected = jugador != null;

        if (selected) {
            txtDetalleNombre.setText(jugador.getNombre());
            txtDetallePosicion.setText(jugador.getPosicion());
            txtDetalleMediaPuntos.setText(String.valueOf(jugador.getMediaPuntos()));
            txtDetalleValorVenta.setText(String.valueOf(jugador.getValorMercado()));
            lblTiempoEnClub.setText("Seleccionado: " + jugador.getNombre());
        } else {
            txtDetalleNombre.setText("");
            txtDetallePosicion.setText("");
            txtDetalleMediaPuntos.setText("");
            txtDetalleValorVenta.setText("");
        }
        btnVender.setDisable(!selected);
        btnCalcularTiempo.setDisable(!selected);
    }

    @FXML
    public void handleVenderJugador(ActionEvent actionEvent) {
        Jugador jugador = tablaJugadores.getSelectionModel().getSelectedItem();
        if (jugador != null) {
            System.out.println("Vendiendo a: " + jugador.getNombre());
        }
    }

    @FXML
    public void handleCalcularTiempo(ActionEvent actionEvent) {
        Jugador jugador = tablaJugadores.getSelectionModel().getSelectedItem();
        if (jugador != null) {
            lblTiempoEnClub.setText("Tiempo calculado (FAKE): 1 año, 2 meses.");
        }
    }
}