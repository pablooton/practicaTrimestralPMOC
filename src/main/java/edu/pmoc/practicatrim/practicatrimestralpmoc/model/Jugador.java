package edu.pmoc.practicatrim.practicatrimestralpmoc.model;

public class Jugador {
    private int idJugador;
    private String nombre;
    private long valorMercado;
    private int mediaPuntos;
    private String posicion;
    private String equipoLiga;
    private boolean libre;

    public boolean isLibre() {
        return libre;
    }

    public Jugador setLibre(boolean libre) {
        this.libre = libre;
        return this;
    }

    public Jugador(int idJugador, String nombre, long valorMercado, int mediaPuntos, String posicion, String equipoLiga, boolean libre) {
        this.idJugador = idJugador;
        this.nombre = nombre;
        this.valorMercado = valorMercado;
        this.mediaPuntos = mediaPuntos;
        this.posicion = posicion;
        this.equipoLiga = equipoLiga;
        this.libre = libre;
    }

    public String getEquipoLiga() {
        return equipoLiga;
    }

    public Jugador setEquipoLiga(String equipoLiga) {
        this.equipoLiga = equipoLiga;
        return this;
    }

    public int getIdJugador() {
        return idJugador;
    }

    public Jugador setIdJugador(int idJugador) {
        this.idJugador = idJugador;
        return this;
    }

    public int getMediaPuntos() {
        return mediaPuntos;
    }

    public Jugador setMediaPuntos(int mediaPuntos) {
        this.mediaPuntos = mediaPuntos;
        return this;
    }

    public String getPosicion() {
        return posicion;
    }

    public Jugador setPosicion(String posicion) {
        this.posicion = posicion;
        return this;
    }

    public String getNombre() {
        return nombre;
    }

    public Jugador setNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public long getValorMercado() {
        return valorMercado;
    }

    public Jugador setValorMercado(long valorMercado) {
        this.valorMercado = valorMercado;
        return this;
    }
}
