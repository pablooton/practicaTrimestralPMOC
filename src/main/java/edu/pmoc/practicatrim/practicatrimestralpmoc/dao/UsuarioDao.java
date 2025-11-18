package edu.pmoc.practicatrim.practicatrimestralpmoc.dao;


import edu.pmoc.practicatrim.practicatrimestralpmoc.model.Usuario;

import java.util.List;

public interface UsuarioDao {
    public List<Usuario> getSelectedAllUsers();
    boolean validarCredenciales(String username,String password);
    public Usuario getUserByNickname(String nickname);

}
