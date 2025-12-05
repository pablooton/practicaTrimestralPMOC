package edu.pmoc.practicatrim.practicatrimestralpmoc.dao;

import edu.pmoc.practicatrim.practicatrimestralpmoc.db.DatabaseConnection;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.Jugador;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
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
                            rs.getBoolean("isAdmin")
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
    public void deleteUser(int idUser) {

        String sqlLiberarJugadores = "UPDATE jugadores j " +
                "INNER JOIN jugadores_equipos je ON j.idjugadores = je.id_jugador " +
                "INNER JOIN equiposfantasy ef ON je.id_equipofantasy = ef.idEquipo " +
                "SET j.isLibre = TRUE " +
                "WHERE ef.idUsuario = ?";

        String sqlDeleteFichajes = "DELETE je FROM jugadores_equipos je " +
                "INNER JOIN equiposfantasy ef ON je.id_equipofantasy = ef.idEquipo " +
                "WHERE ef.idUsuario = ?";


        String sqlDeleteEquipoFantasy = "DELETE FROM equiposfantasy WHERE idUsuario = ?";

        String sqlDeleteUsuario = "DELETE FROM usuarios WHERE idusuario = ?";

        Connection connection = null;

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            // 1. LIBERAR JUGADORES
            try (PreparedStatement psLiberar = connection.prepareStatement(sqlLiberarJugadores)) {
                psLiberar.setInt(1, idUser);
                psLiberar.executeUpdate();
            }

            // 2. ELIMINAR FICHAJES
            try (PreparedStatement psFichajes = connection.prepareStatement(sqlDeleteFichajes)) {
                psFichajes.setInt(1, idUser);
                psFichajes.executeUpdate();
            }

            // 3. ELIMINAR EQUIPO FANTASY
            try (PreparedStatement psEquipo = connection.prepareStatement(sqlDeleteEquipoFantasy)) {
                psEquipo.setInt(1, idUser);
                psEquipo.executeUpdate();
            }

            // 4. ELIMINAR USUARIO
            try (PreparedStatement psUsuario = connection.prepareStatement(sqlDeleteUsuario)) {
                psUsuario.setInt(1, idUser);
                psUsuario.executeUpdate();
            }

            connection.commit();

        } catch (SQLException e) {
            System.err.println("Error SQL durante la eliminación del usuario. Intentando Rollback...");
            try {
                if (connection != null) {
                    connection.rollback();
                    System.err.println("Rollback exitoso.");
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error crítico durante el rollback: " + rollbackEx.getMessage());
            }

            throw new RuntimeException("Error al eliminar el usuario con ID: " + idUser + ". Revisar dependencias. Detalle: " + e.getMessage(), e);

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
    @Override
    public void updateUserAdminStatus(int idUsuario, boolean isAdmin) {
        String sql = "UPDATE usuarios SET isAdmin=? WHERE idusuario=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, isAdmin);
            stmt.setInt(2, idUsuario);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el estado de administrador del usuario con ID: " + idUsuario, e);
        }
    }

    @Override
    public boolean existeIdUsuario(int idUsuario) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE idusuario = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {

            throw new RuntimeException("Error en la base de datos al verificar existencia de usuario.", e);
        }
        return false;
    }
}


