package edu.pmoc.practicatrim.practicatrimestralpmoc.dao;



import edu.pmoc.practicatrim.practicatrimestralpmoc.db.DatabaseConnection;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.EquipoFantasy;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDaoImpl implements UsuarioDao{
    @Override
    public List<Usuario> getSelectedAllUsers() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "Select * from usuarios";
        Connection connection = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                Usuario usuario = new Usuario(
                        rs.getInt("idUsuario"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("nickname"),
                        rs.getString("password"),
                        rs.getBoolean("isAdmin")

                );
                usuarios.add(usuario);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return usuarios;
    }

    @Override
    public boolean validarCredenciales(String username, String password) {
        String sql = "Select * from usuarios where nickname= ?  and password = ?";
        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1,username);
            ps.setString(2,password);

            try(ResultSet rs = ps.executeQuery()){
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error al validar las credenciales");
            e.printStackTrace();
            return false;
        }


    }

    public Usuario getUserByNickname(String nickname) {
        Usuario usuario = null;
        String sql = "SELECT * FROM usuarios WHERE nickname = ?";
        Connection connection = DatabaseConnection.getConnection();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nickname);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario(
                            rs.getInt("idUsuario"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getString("nickname"),
                            rs.getString("password"),
                            rs.getBoolean("isAdmin")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuario;
    }

    @Override
    public ObservableList<EquipoFantasy> obtenerEquipoUser(int idUser) {
        ObservableList<EquipoFantasy> equiposUser = FXCollections.observableArrayList();

    }


}
