package edu.pmoc.practicatrim.practicatrimestralpmoc.model;

import java.util.Date;

public class JugadoresEquipos {
    private int id;
    private Jugador idJugador;
    private EquipoFantasy idEquipo;
    private Date fechaSalida;
    private Date fechaFichaje;

    public JugadoresEquipos(Date fechaFichaje, Date fechaSalida, int id, EquipoFantasy idEquipo, Jugador idJugador) {
        this.fechaFichaje = fechaFichaje;
        this.fechaSalida = fechaSalida;
        this.id = id;
        this.idEquipo = idEquipo;
        this.idJugador = idJugador;
    }

    public Date getFechaFichaje() {
        return fechaFichaje;
    }

    public JugadoresEquipos setFechaFichaje(Date fechaFichaje) {
        this.fechaFichaje = fechaFichaje;
        return this;
    }

    public Date getFechaSalida() {
        return fechaSalida;
    }

    public JugadoresEquipos setFechaSalida(Date fechaSalida) {
        this.fechaSalida = fechaSalida;
        return this;
    }

    public int getId() {
        return id;
    }

    public JugadoresEquipos setId(int id) {
        this.id = id;
        return this;
    }

    public EquipoFantasy getIdEquipo() {
        return idEquipo;
    }

    public JugadoresEquipos setIdEquipo(EquipoFantasy idEquipo) {
        this.idEquipo = idEquipo;
        return this;
    }

    public Jugador getIdJugador() {
        return idJugador;
    }

    public JugadoresEquipos setIdJugador(Jugador idJugador) {
        this.idJugador = idJugador;
        return this;
    }
}
