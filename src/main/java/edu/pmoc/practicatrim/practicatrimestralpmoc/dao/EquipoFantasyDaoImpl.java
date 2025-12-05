package edu.pmoc.practicatrim.practicatrimestralpmoc.dao;

import edu.pmoc.practicatrim.practicatrimestralpmoc.db.DatabaseConnection;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.EquipoFantasy;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EquipoFantasyDaoImpl implements EquipoFantasyDao {

    @Override
    public EquipoFantasy getEquipoByUserId(int idUsuario) {
        EquipoFantasy equipo = null;
        String sql = "SELECT idEquipo, nombre, idUsuario, presupuesto FROM equiposfantasy WHERE idUsuario = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long presupuesto = rs.getLong("presupuesto");

                    equipo = new EquipoFantasy(
                            rs.getInt("idEquipo"),
                            rs.getString("nombre"),
                            rs.getInt("idUsuario"),
                            presupuesto
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipo;
    }

    @Override
    public boolean ficharJugador(int idEquipoFantasy, int idJugador, long precioJugador) {

        String sqlPresupuesto = "SELECT presupuesto FROM equiposfantasy WHERE idEquipo = ?";
        String sqlUpdatePresupuesto = "UPDATE equiposfantasy SET presupuesto = presupuesto - ? WHERE idEquipo = ?";
        String sqlInsertFichaje = "INSERT INTO jugadores_equipos(id_equipofantasy, id_jugador, fecha_fichaje) VALUES(?, ?, ?)";
        String sqlUpdateJugadorLibre = "UPDATE jugadores SET isLibre = FALSE WHERE idjugadores = ?";

        Connection connection = null;
        boolean exito = false;

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);


            try (PreparedStatement ps = connection.prepareStatement(sqlPresupuesto)) {
                ps.setInt(1, idEquipoFantasy);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getLong("presupuesto") >= precioJugador) {
                    } else {
                        connection.rollback();
                        return false;
                    }
                }
            }


            try (PreparedStatement ps = connection.prepareStatement(sqlUpdatePresupuesto)) {
                ps.setLong(1, precioJugador);
                ps.setInt(2, idEquipoFantasy);
                ps.executeUpdate();
            }


            try (PreparedStatement ps = connection.prepareStatement(sqlInsertFichaje)) {
                Timestamp now = Timestamp.valueOf(LocalDateTime.now());
                ps.setInt(1, idEquipoFantasy);
                ps.setInt(2, idJugador);
                ps.setTimestamp(3, now);
                ps.executeUpdate();
            }


            try (PreparedStatement ps = connection.prepareStatement(sqlUpdateJugadorLibre)) {
                ps.setInt(1, idJugador);
                int filasAfectadas = ps.executeUpdate();


                if (filasAfectadas == 0) {
                    System.err.println("Advertencia: No se pudo marcar al jugador " + idJugador + " como no libre.");
                    connection.rollback();
                    return false;
                }
            }

            connection.commit();
            exito = true;

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error realizando rollback en ficharJugador: " + ex.getMessage());
                }
            }
            System.err.println("Database error signing player: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error cerrando conexión en ficharJugador: " + e.getMessage());
                }
            }
        }
        return exito;
    }
    @Override
    public List<EquipoFantasy> getAllEquipos() {
        List<EquipoFantasy> equipos = new ArrayList<>();
        String sql = "SELECT idEquipo, nombre, idUsuario, presupuesto FROM equiposfantasy";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                EquipoFantasy equipo = new EquipoFantasy(
                        rs.getInt("idEquipo"),
                        rs.getString("nombre"),
                        rs.getInt("idUsuario"),
                        rs.getLong("presupuesto")
                );
                equipos.add(equipo);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al cargar todos los equipos de la base de datos.", e);
        }
        return equipos;
    }

    @Override
    public boolean venderJugador(int idJugador, int idEquipoFantasy, long precioVenta) {
        String sqlDeleteFichaje = "DELETE FROM jugadores_equipos WHERE id_jugador = ? AND id_equipofantasy = ?";
        String sqlUpdatePresupuesto = "UPDATE equiposfantasy SET presupuesto = presupuesto + ? WHERE idEquipo = ?";
        String sqlUpdateJugadorLibre = "UPDATE jugadores SET isLibre = TRUE WHERE idjugadores = ?";

        Connection connection = null;
        boolean exito = false;

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement(sqlDeleteFichaje)) {
                ps.setInt(1, idJugador);
                ps.setInt(2, idEquipoFantasy);
                int filasAfectadas = ps.executeUpdate();

                if (filasAfectadas != 1) {
                    connection.rollback();
                    return false;
                }
            }

            try (PreparedStatement ps = connection.prepareStatement(sqlUpdatePresupuesto)) {
                ps.setLong(1, precioVenta);
                ps.setInt(2, idEquipoFantasy);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = connection.prepareStatement(sqlUpdateJugadorLibre)) {
                ps.setInt(1, idJugador);
                ps.executeUpdate();
            }

            connection.commit();
            exito = true;

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error realizando rollback en venderJugador: " + ex.getMessage());
                }
            }
            System.err.println("Database error selling player: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error cerrando conexión en venderJugador: " + e.getMessage());
                }
            }
        }
        return exito;
    }
    @Override
    public void addEquipo(EquipoFantasy equipo) {
        String sql = "INSERT INTO equiposfantasy (nombre, idUsuario, presupuesto) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, equipo.getNombre());
            ps.setInt(2, equipo.getIdUsuario());
            ps.setLong(3, equipo.getPresupuesto());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    equipo.setIdEquipo(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al añadir un nuevo equipo.", e);
        }
    }
    @Override
    public void updateEquipo(EquipoFantasy equipo) {
        String sql = "UPDATE equiposfantasy SET nombre = ?, idUsuario = ?, presupuesto = ? WHERE idEquipo = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, equipo.getNombre());
            ps.setInt(2, equipo.getIdUsuario());
            ps.setLong(3, equipo.getPresupuesto());
            ps.setInt(4, equipo.getIdEquipo());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el equipo con ID: " + equipo.getIdEquipo(), e);
        }
    }
    @Override
    public void eliminarEquipo(int idEquipo) {
        String sqlLiberarJugadores = "UPDATE jugadores j JOIN jugadores_equipos je ON j.idjugadores = je.id_jugador SET j.isLibre = TRUE WHERE je.id_equipofantasy = ?";
        String sqlDeleteFichajes = "DELETE FROM jugadores_equipos WHERE id_equipofantasy = ?";
        String sqlDeleteEquipo = "DELETE FROM equiposfantasy WHERE idEquipo = ?";

        Connection connection = null;

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement psLiberar = connection.prepareStatement(sqlLiberarJugadores)) {
                psLiberar.setInt(1, idEquipo);
                psLiberar.executeUpdate();
            }

            try (PreparedStatement psFichajes = connection.prepareStatement(sqlDeleteFichajes)) {
                psFichajes.setInt(1, idEquipo);
                psFichajes.executeUpdate();
            }

            try (PreparedStatement psEquipo = connection.prepareStatement(sqlDeleteEquipo)) {
                psEquipo.setInt(1, idEquipo);
                psEquipo.executeUpdate();
            }

            connection.commit();

        } catch (SQLException e) {
            System.err.println("Error SQL durante la eliminación del equipo. Intentando Rollback...");
            try {
                if (connection != null) {
                    connection.rollback();
                    System.err.println("Rollback exitoso.");
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error crítico durante el rollback: " + rollbackEx.getMessage());
            }

            throw new RuntimeException("Error al eliminar el equipo con ID: " + idEquipo + ". Revisar dependencias.", e);

        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException closeEx) {
                System.err.println("Error al cerrar la conexión: " + closeEx.getMessage());
            }
        }
    }
}