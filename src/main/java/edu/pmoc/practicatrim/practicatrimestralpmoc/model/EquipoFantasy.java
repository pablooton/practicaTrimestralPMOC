package edu.pmoc.practicatrim.practicatrimestralpmoc.model;

public class EquipoFantasy {
    private int idEquipo;
    private String nombreEquipo;


    public EquipoFantasy(int idEquipo, String nombreEquipo, int idUsuario) {
        this.idEquipo = idEquipo;
        this.nombreEquipo = nombreEquipo;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public EquipoFantasy setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
        return this;
    }

    public int getIdEquipo() {
        return idEquipo;
    }

    public EquipoFantasy setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
        return this;
    }

    
}
