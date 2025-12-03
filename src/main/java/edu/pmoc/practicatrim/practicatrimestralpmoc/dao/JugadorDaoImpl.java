package edu.pmoc.practicatrim.practicatrimestralpmoc.dao;

import edu.pmoc.practicatrim.practicatrimestralpmoc.db.DatabaseConnection;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.Jugador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JugadorDaoImpl implements JugadorDao{
    @Override
    public List<Jugador> getAllJugadores() {
        List<Jugador> jugadores= new ArrayList<>();
        String sql = "Select * from jugadores";
        Connection connection = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){

                Jugador jugador = new Jugador(
                        rs.getInt("idjugadores"),
                        rs.getString("nombre"),
                        rs.getLong("valorMercado"),
                        rs.getInt("mediaPuntos"),
                        rs.getString("posicion"),
                        rs.getString("equipoLiga"),
                        rs.getBoolean("isLibre")
                );
                jugadores.add(jugador);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return jugadores;
    }

    @Override
    public List<Jugador> sacarJugadoresMercado() {
        List<Jugador> jugadoresLibres = new ArrayList<>();


        String sql = "SELECT idjugadores, nombre, valorMercado, mediaPuntos, posicion, equipoLiga, isLibre FROM jugadores WHERE isLibre = TRUE";


        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()){
                Jugador jugadorLibre = new Jugador(
                        rs.getInt("idjugadores"),
                        rs.getString("nombre"),
                        rs.getLong("valorMercado"),
                        rs.getInt("mediaPuntos"),
                        rs.getString("posicion"),
                        rs.getString("equipoLiga"),
                        rs.getBoolean("isLibre")
                );
                jugadoresLibres.add(jugadorLibre);
            }

        } catch (SQLException e) {

            throw new RuntimeException("Error al cargar jugadores del mercado desde la base de datos.", e);
        }

        return jugadoresLibres;
    }

    @Override
    public List<Jugador> buscarJugadores(String nombre) {
        List<Jugador> jugadoresEncontrados = new ArrayList<>();

        String sql = "SELECT idjugadores, nombre, valorMercado, mediaPuntos, posicion, equipoLiga FROM jugadores WHERE LOWER(nombre) LIKE ? and isLibre = True";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {


            String nombreBusqueda = "%" + nombre.toLowerCase() + "%";
            preparedStatement.setString(1, nombreBusqueda);

            try(ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()){

                    Jugador jugador = new Jugador(
                            rs.getInt("idjugadores"),
                            rs.getString("nombre"),
                            rs.getLong("valorMercado"),
                            rs.getInt("mediaPuntos"),
                            rs.getString("posicion"),
                            rs.getString("equipoLiga"),
                            rs.getBoolean("isLibre")
                    );
                    jugadoresEncontrados.add(jugador);
                }
            }
        } catch (SQLException e) {

            throw new RuntimeException("Error al buscar jugadores por nombre en la base de datos.", e);
        }

        return jugadoresEncontrados;
    }
    @Override
    public void addJugador(Jugador jugador) {

        String sql = "INSERT INTO jugadores (nombre, valorMercado, mediaPuntos, posicion, equipoLiga, isLibre) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {


            ps.setString(1, jugador.getNombre());
            ps.setLong(2, jugador.getValorMercado());
            ps.setInt(3, jugador.getMediaPuntos());
            ps.setString(4, jugador.getPosicion());
            ps.setString(5, jugador.getEquipoLiga());
            ps.setBoolean(6, jugador.isLibre());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    jugador.setIdJugador(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al añadir un nuevo jugador.", e);
        }
    }


    @Override
    public void updateJugador(Jugador jugador) {
        String sql = "UPDATE jugadores SET nombre = ?, valorMercado = ?, mediaPuntos = ?, posicion = ?, equipoLiga = ?, isLibre = ? WHERE idjugadores = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, jugador.getNombre());
            ps.setLong(2, jugador.getValorMercado());
            ps.setInt(3, jugador.getMediaPuntos());
            ps.setString(4, jugador.getPosicion());
            ps.setString(5, jugador.getEquipoLiga());
            ps.setBoolean(6, jugador.isLibre());
            ps.setInt(7, jugador.getIdJugador());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el jugador con ID: " + jugador.getIdJugador(), e);
        }
    }


    @Override
    public void eliminarJugador(int idJugador) {
        String sqlDeleteEquipo = "DELETE FROM jugadores_equipos WHERE id_jugador = ?";
        String sqlDeleteJugador = "DELETE FROM jugadores WHERE idjugadores = ?";

        Connection connection = null;

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement psEquipo = connection.prepareStatement(sqlDeleteEquipo)) {
                psEquipo.setInt(1, idJugador);
                psEquipo.executeUpdate();
            }

            try (PreparedStatement psJugador = connection.prepareStatement(sqlDeleteJugador)) {
                psJugador.setInt(1, idJugador);
                psJugador.executeUpdate();
            }

            connection.commit();

        } catch (SQLException e) {
            System.err.println("Error SQL durante la eliminación. Intentando Rollback...");
            try {
                if (connection != null) {
                    connection.rollback();
                    System.err.println("Rollback exitoso.");
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error crítico durante el rollback: " + rollbackEx.getMessage());
            }

            throw new RuntimeException("Error al eliminar el jugador con ID: " + idJugador + ". Revisar dependencias.", e);

        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException closeEx) {
                System.err.println("Error al cerrar la conexión después de la transacción: " + closeEx.getMessage());
            }
        }
    }
}

