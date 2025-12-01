package edu.pmoc.practicatrim.practicatrimestralpmoc.dao;

import edu.pmoc.practicatrim.practicatrimestralpmoc.db.DatabaseConnection;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.EquipoFantasy;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.Jugador;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDaoImpl implements UsuarioDao {

    @Override
    public List<Usuario> getSelectedAllUsers() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                usuarios.add(new Usuario(
                        rs.getInt("idusuario"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("nickname"),
                        rs.getString("password"),
                        rs.getBoolean("isAdmin")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    @Override
    public boolean validarCredenciales(String username, String password) {
        String sql = "SELECT * FROM usuarios WHERE nickname = ? AND password = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Usuario getUserByNickname(String nickname) {
        Usuario usuario = null;


        String sql = "SELECT * FROM usuarios WHERE nickname = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, nickname);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario(
                            rs.getInt("idusuario"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("nickname"),
                            rs.getString("password"),
                            false
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR SQL al recuperar usuario: " + e.getMessage());
            e.printStackTrace();
        }


        System.out.println("DAO getUserByNickname resultado: " + (usuario != null ? usuario.getNickname() : "NULL"));

        return usuario;
    }

    @Override
    public EquipoFantasy getEquipoByUserId(int idUsuario) {
        EquipoFantasy equipo = null;
        String sql = "SELECT idEquipo, nombre, idUsuario, presupuesto FROM equiposfantasy WHERE idUsuario = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                   equipo = new EquipoFantasy(
                            rs.getInt("idEquipo"),
                            rs.getString("nombre"),
                            rs.getInt("idUsuario"),
                            rs.getLong("presupuesto")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return equipo;
    }
    @Override
    public ObservableList<Jugador> obtenerJugadoresDelEquipoUsuario(int idUsuario) {
        ObservableList<Jugador> misJugadores = FXCollections.observableArrayList();

        String sql = "SELECT j.* FROM jugadores j " +
                "JOIN jugadores_equipos je ON j.idjugadores = je.id_jugador " +
                "JOIN equiposfantasy e ON je.id_equipoFantasy = e.idEquipo " +
                "WHERE e.idUsuario = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Jugador jugador = new Jugador(
                            rs.getInt("idjugadores"),
                            rs.getString("nombre"),
                            rs.getLong("valorMercado"),
                            rs.getInt("mediaPuntos"),
                            rs.getString("posicion"),
                            rs.getString("equipoLiga"),
                            false
                    );
                    misJugadores.add(jugador);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return misJugadores;
    }

    @Override
    public boolean updateNickname(int idUsuario, String newNickname) {
        String sql = "UPDATE usuarios SET nickname = ? WHERE idusuario = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, newNickname);
            ps.setInt(2, idUsuario);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updatePassword(int idUsuario, String newPassword) {
        String sql = "UPDATE usuarios SET password = ? WHERE idusuario = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setInt(2, idUsuario);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean existeNickname(String nickname) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE nickname = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, nickname);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }


}
