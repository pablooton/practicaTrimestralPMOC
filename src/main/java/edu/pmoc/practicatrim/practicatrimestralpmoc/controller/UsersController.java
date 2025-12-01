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
        System.out.println("Entrando en intialize");
        configurarColumnas();

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
        }else{
            System.out.println("ES NULO");
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


        if (plantilla == null) {
            System.out.println("ERROR GRAVE: La lista es NULL. Revisa el DAO.");
        } else if (plantilla.isEmpty()) {
            System.out.println("AVISO: La lista es válida pero está VACÍA (0 jugadores encontrados).");
            System.out.println("Revisa tu base de datos: ¿El usuario " + idUsuario + " tiene equipo? ¿Ese equipo tiene jugadores vinculados?");
        } else {
            System.out.println("ÉXITO: Se han encontrado " + plantilla.size() + " jugadores.");

            for (Jugador j : plantilla) {
                System.out.println(" - Jugador: " + j.getNombre() + " | Puntos: " + j.getMediaPuntos());
            }
        }

        tablaJugadores.setItems(plantilla);
    }
}