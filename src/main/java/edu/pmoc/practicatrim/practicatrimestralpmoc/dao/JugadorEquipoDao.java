package edu.pmoc.practicatrim.practicatrimestralpmoc.dao;

import edu.pmoc.practicatrim.practicatrimestralpmoc.model.JugadoresEquipos;

import java.util.Date;

public interface JugadorEquipoDao {
    public int calcularTiempo(int idJugador, int idEquipo, Date fechaFichaje, Date fechaSalida);
    public JugadoresEquipos getJugadoresEquipos(int idJugador, int idEquipo);
}
