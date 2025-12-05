package edu.pmoc.practicatrim.practicatrimestralpmoc.dao;

import edu.pmoc.practicatrim.practicatrimestralpmoc.model.EquipoFantasy;

import java.util.List;

public interface EquipoFantasyDao {
    public boolean ficharJugador(int idEquipoFantasy, int idJugador, long precioJugador);
    public boolean venderJugador(int idJugador, int idEquipoFantasy, long precioVenta);
    public EquipoFantasy getEquipoByUserId(int idUsuario);
    public void updateEquipo(EquipoFantasy equipo);
    public void addEquipo(EquipoFantasy equipo);
    public void eliminarEquipo(int idEquipo);
}
