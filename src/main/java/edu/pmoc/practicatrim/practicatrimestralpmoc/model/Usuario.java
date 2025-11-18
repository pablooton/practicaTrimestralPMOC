package edu.pmoc.practicatrim.practicatrimestralpmoc.model;

import java.util.Date;

public class Usuario {
    private int idUsuario;
    private String nombre;
    private  String apellido;
    private String nickname;
    private String password;
    private boolean isAdmin;

    public Usuario(int idUsuario,String nombre, String apellido, String nickname, String password,  boolean isAdmin) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.idUsuario = idUsuario;
        this.nickname = nickname;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public Usuario(String nombre, String apellido,  String nickname, String password) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.nickname = nickname;
        this.password = password;
    }



    public String getApellido() {
        return apellido;
    }

    public Usuario setApellido(String apellido) {
        this.apellido = apellido;
        return this;
    }


    public int getIdUsuario() {
        return idUsuario;
    }

    public Usuario setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
        return this;
    }

    public String getNickname() {
        return nickname;
    }

    public Usuario setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public String getNombre() {
        return nombre;
    }

    public Usuario setNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Usuario setPassword(String password) {
        this.password = password;
        return this;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public Usuario setAdmin(boolean admin) {
        isAdmin = admin;
        return this;
    }
}
