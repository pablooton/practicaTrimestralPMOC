package edu.pmoc.practicatrim.practicatrimestralpmoc.dao;

import edu.pmoc.practicatrim.practicatrimestralpmoc.model.Jugador;

import java.util.ArrayList;
import java.util.List;

public interface JugadorDao {
    public List<Jugador> getAllJugadores();
    public List<Jugador> sacarJugadoresMercado();
    public List<Jugador> buscarJugadores(String nombre);
    public void addJugador(Jugador jugador);
    public void updateJugador(Jugador jugador);
    public void eliminarJugador(int idJugador);
}
