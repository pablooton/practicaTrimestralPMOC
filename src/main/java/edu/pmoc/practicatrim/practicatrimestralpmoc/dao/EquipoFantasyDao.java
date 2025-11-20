package edu.pmoc.practicatrim.practicatrimestralpmoc.dao;

import edu.pmoc.practicatrim.practicatrimestralpmoc.model.EquipoFantasy;

import java.util.List;

public interface EquipoFantasyDao {
    boolean ficharJugador(int idEquipoFantasy, int idJugador);
    boolean venderJugador(int idJugador);
}
