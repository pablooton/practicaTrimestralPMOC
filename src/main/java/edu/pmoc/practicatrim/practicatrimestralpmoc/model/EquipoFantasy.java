package edu.pmoc.practicatrim.practicatrimestralpmoc.model;

public class EquipoFantasy {
    private int idEquipo;
    private String nombreEquipo;
    private long dineroDisponible;
    private long valorPlantilla;

    public EquipoFantasy(int idEquipo, String nombreEquipo, long dineroDisponible, long valorPlantilla) {
        this.idEquipo = idEquipo;
        this.nombreEquipo = nombreEquipo;
        this.dineroDisponible = dineroDisponible;
        this.valorPlantilla = valorPlantilla;
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

    public long getDineroDisponible() {
        return dineroDisponible;
    }

    public EquipoFantasy setDineroDisponible(long dineroDisponible) {
        this.dineroDisponible = dineroDisponible;
        return this;
    }

    public long getValorPlantilla() {
        return valorPlantilla;
    }

    public EquipoFantasy setValorPlantilla(long valorPlantilla) {
        this.valorPlantilla = valorPlantilla;
        return this;
    }
}
