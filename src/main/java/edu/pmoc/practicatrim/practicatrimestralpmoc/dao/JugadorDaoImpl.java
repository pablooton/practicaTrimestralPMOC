package edu.pmoc.practicatrim.practicatrimestralpmoc.dao;

import edu.pmoc.practicatrim.practicatrimestralpmoc.db.DatabaseConnection;
import edu.pmoc.practicatrim.practicatrimestralpmoc.model.Jugador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JugadorDaoImpl implements JugadorDao{
    @Override
    public List<Jugador> getAllJugadores() {
        List<Jugador> jugadores= new ArrayList<>();
        String sql = "Select * from jugadores";
        Connection connection = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){

                Jugador jugador = new Jugador(
                        rs.getInt("idjugadores"),
                        rs.getString("nombre"),
                        rs.getLong("valorMercado"),
                        rs.getInt("mediaPuntos"),
                        rs.getString("posicion"),
                        rs.getString("equipoLiga"),
                        rs.getBoolean("isLibre")
                );
                jugadores.add(jugador);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return jugadores;
    }

    @Override
    public List<Jugador> sacarJugadoresMercado() {
        List<Jugador> jugadoresLibres = new ArrayList<>();


        String sql = "SELECT idjugadores, nombre, valorMercado, mediaPuntos, posicion, equipoLiga, isLibre FROM jugadores WHERE isLibre = TRUE";


        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()){
                Jugador jugadorLibre = new Jugador(
                        rs.getInt("idjugadores"),
                        rs.getString("nombre"),
                        rs.getLong("valorMercado"),
                        rs.getInt("mediaPuntos"),
                        rs.getString("posicion"),
                        rs.getString("equipoLiga"),
                        rs.getBoolean("isLibre")
                );
                jugadoresLibres.add(jugadorLibre);
            }

        } catch (SQLException e) {

            throw new RuntimeException("Error al cargar jugadores del mercado desde la base de datos.", e);
        }

        return jugadoresLibres;
    }

    @Override
    public List<Jugador> buscarJugadores(String nombre) {
        List<Jugador> jugadoresEncontrados = new ArrayList<>();

        String sql = "SELECT idjugadores, nombre, valorMercado, mediaPuntos, posicion, equipoLiga FROM jugadores WHERE LOWER(nombre) LIKE ? and isLibre = True";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {


            String nombreBusqueda = "%" + nombre.toLowerCase() + "%";
            preparedStatement.setString(1, nombreBusqueda);

            try(ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()){

                    Jugador jugador = new Jugador(
                            rs.getInt("idjugadores"),
                            rs.getString("nombre"),
                            rs.getLong("valorMercado"),
                            rs.getInt("mediaPuntos"),
                            rs.getString("posicion"),
                            rs.getString("equipoLiga"),
                            rs.getBoolean("isLibre")
                    );
                    jugadoresEncontrados.add(jugador);
                }
            }
        } catch (SQLException e) {

            throw new RuntimeException("Error al buscar jugadores por nombre en la base de datos.", e);
        }

        return jugadoresEncontrados;
    }
}

