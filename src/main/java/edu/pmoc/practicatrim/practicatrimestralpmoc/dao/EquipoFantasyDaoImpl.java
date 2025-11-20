package edu.pmoc.practicatrim.practicatrimestralpmoc.dao;

import edu.pmoc.practicatrim.practicatrimestralpmoc.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class EquipoFantasyDaoImpl implements  EquipoFantasyDao{
    @Override
    public boolean ficharJugador(int idEquipoFantasy, int idJugador) {
        Connection connection = DatabaseConnection.getConnection();
        String sql = "Insert into jugadores_equipos(id_equipofantasy,id_jugador,fecha_fichaje) " +
                "values(?,?,?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            ps.setInt(1,idEquipoFantasy);
            ps.setInt(2,idJugador);
            ps.setTimestamp(3,now);

            int filasAfectadas = ps.executeUpdate();

            return filasAfectadas == 1;
        } catch (SQLException e) {
            System.err.println("Database error signing player: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean venderJugador(int idJugador) {
        Connection connection = DatabaseConnection.getConnection();
        String sql = "Delete from jugadores_equipos where id_jugador = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1,idJugador);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas == 1;
        } catch (SQLException e) {
            System.err.println("Database error selling player: " + e.getMessage());
            return false;
        }
    }

}
