package edu.pmoc.practicatrim.practicatrimestralpmoc.controller;

import edu.pmoc.practicatrim.practicatrimestralpmoc.SessionManager;
import edu.pmoc.practicatrim.practicatrimestralpmoc.dao.UsuarioDao;
import edu.pmoc.practicatrim.practicatrimestralpmoc.dao.UsuarioDaoImpl;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.EquipoFantasy;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.Jugador;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.Usuario;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class UsersController {

    @FXML private Label lblNombreEquipo;
    @FXML private Label lblPropietario;

    @FXML private TableView<Jugador> tablaJugadores;
    @FXML private TableColumn<Jugador, String> colNombre;
    @FXML private TableColumn<Jugador, String> colPosicion;
    @FXML private TableColumn<Jugador, String> colEquipoReal;
    @FXML private TableColumn<Jugador, Integer> colPuntos;
    @FXML private TableColumn<Jugador, Long> colValor;

    private final UsuarioDao usuarioDao = new UsuarioDaoImpl();

    @FXML
    public void initialize() {
        Usuario usuarioActual = SessionManager.getInstance().getCurrentUser();

        if (usuarioActual != null) {
            EquipoFantasy miEquipo = usuarioDao.getEquipoByUserId(usuarioActual.getIdUsuario());

            if (miEquipo != null) {
                lblNombreEquipo.setText(miEquipo.getNombreEquipo());
                lblPropietario.setText("Entrenador: " + usuarioActual.getNickname());
                cargarJugadores(usuarioActual.getIdUsuario());
            } else {
                lblNombreEquipo.setText("Sin Equipo");
                lblPropietario.setText("");
            }

            configurarColumnas();
        }
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
}