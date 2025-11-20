package edu.pmoc.practicatrim.practicatrimestralpmoc.dao;


import edu.pmoc.practicatrim.practicatrimestralpmoc.model.EquipoFantasy;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.Usuario;
import javafx.collections.ObservableList;

import java.util.List;

public interface UsuarioDao {
    public List<Usuario> getSelectedAllUsers();
    boolean validarCredenciales(String username,String password);
    public Usuario getUserByNickname(String nickname);
    public ObservableList<EquipoFantasy> obtenerEquipoUser(int idUser);

}
