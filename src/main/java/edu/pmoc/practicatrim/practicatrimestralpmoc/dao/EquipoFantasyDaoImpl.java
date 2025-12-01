package edu.pmoc.practicatrim.practicatrimestralpmoc.dao;

import edu.pmoc.practicatrim.practicatrimestralpmoc.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class EquipoFantasyDaoImpl implements EquipoFantasyDao {

    @Override
    public boolean ficharJugador(int idEquipoFantasy, int idJugador, long precioJugador) {
        String sqlPresupuesto = "SELECT presupuesto FROM equiposfantasy WHERE idEquipo = ?";
        String sqlUpdatePresupuesto = "UPDATE equiposfantasy SET presupuesto = presupuesto - ? WHERE idEquipo = ?";
        String sqlInsertFichaje = "INSERT INTO jugadores_equipos(id_equipofantasy, id_jugador, fecha_fichaje) VALUES(?, ?, ?)";

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
    public boolean venderJugador(int idJugador, int idEquipoFantasy, long precioVenta) {
        String sqlDeleteFichaje = "DELETE FROM jugadores_equipos WHERE id_jugador = ?";
        String sqlUpdatePresupuesto = "UPDATE equiposfantasy SET presupuesto = presupuesto + ? WHERE idEquipo = ?";

        Connection connection = null;
        boolean exito = false;

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement(sqlDeleteFichaje)) {
                ps.setInt(1, idJugador);
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
}