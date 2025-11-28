package edu.pmoc.practicatrim.practicatrimestralpmoc;

import edu.pmoc.practicatrim.practicatrimestralpmoc.model.Usuario;

public class SessionManager {
    private static SessionManager instance;
    private Usuario user;

    private SessionManager(){}

    public static SessionManager getInstance(){
        if (instance==null){
            instance = new SessionManager();
        }
        return instance;
    }
    public Usuario getCurrentUser(){
        return user;
    }
    public void setCurrentUser(Usuario usuario){
        this.user = usuario;
    }
    public void clearSession(){
        user = null;
    }
}
