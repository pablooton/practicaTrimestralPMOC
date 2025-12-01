package edu.pmoc.practicatrim.practicatrimestralpmoc.model;

public class EquipoFantasy {
    private int idEquipo;
    private String nombre;
    private int idUsuario;
    private long presupuesto;

    public EquipoFantasy(int idEquipo, String nombre, int idUsuario, long presupuesto) {
        this.idEquipo = idEquipo;
        this.nombre = nombre;
        this.idUsuario = idUsuario;
        this.presupuesto = presupuesto;
    }

    public EquipoFantasy(String nombre, int idUsuario, long presupuesto) {
        this.nombre = nombre;
        this.idUsuario = idUsuario;
        this.presupuesto = presupuesto;
    }

    public EquipoFantasy(int idEquipo, String nombre, int idUsuario) {
        this(idEquipo, nombre, idUsuario, 0);
    }

    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getNombreEquipo() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public long getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(long presupuesto) {
        this.presupuesto = presupuesto;
    }
}