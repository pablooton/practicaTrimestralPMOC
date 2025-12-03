package edu.pmoc.practicatrim.practicatrimestralpmoc.dao;

import edu.pmoc.practicatrim.practicatrimestralpmoc.db.DatabaseConnection;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.JugadoresEquipos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class JugadorEquipoDaoImpl implements JugadorEquipoDao {
    @Override
    public int calcularTiempo(int idJugador, int idEquipo, Date fechaFichaje, Date fechaSalida) {

        String sql = "SELECT DATEDIFF(?, ?) AS diferencia_dias";
        int  dias = 0;

        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        Date fechaFin = (fechaSalida != null) ? fechaSalida : new Date();


        try {
            stmt.setDate(1, new java.sql.Date(fechaFin.getTime()));
            stmt.setDate(2, new java.sql.Date(fechaFichaje.getTime()));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    dias = rs.getInt("diferencia_dias");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return dias;
    }

    public JugadoresEquipos getJugadoresEquipos(int idJugador, int idEquipo) {
        String sql = "SELECT fecha_fichaje, fecha_salida FROM jugadores_equipos " +
                "WHERE id_jugador = ? AND id_equipoFantasy = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idJugador);
            stmt.setInt(2, idEquipo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    JugadoresEquipos je = new JugadoresEquipos();
                     je.setFechaFichaje(rs.getDate("fecha_fichaje"));
                    je.setFechaSalida(rs.getDate("fecha_salida"));
                    return je;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener el registro de JugadoresEquipos: " + e.getMessage());
            throw new RuntimeException("Error DAO en getJugadoresEquipos.", e);
        }
        return null;
    }
}

