package io.proyecto2.flowfree;

import java.io.Serializable;


public class NivelFlowFree implements Nivelable, Serializable {
    private final int numero;
    private final int tamaño;
    private final int[][]puntos;
    private final int tiempoLimite;
    private final int puntajeBase;
    private final String nombre;

    public NivelFlowFree(int numero, int tamaño, int[][] puntos, int tiempoLimite, int puntajeBase, String nombre) {
        this.numero = numero;
        this.tamaño = tamaño;
        this.puntos = puntos;
        this.tiempoLimite = tiempoLimite;
        this.puntajeBase = puntajeBase;
        this.nombre = nombre;
    }
    
    @Override public void cargarNivel(NivelFlowFree n) { }
    @Override public boolean esCompleto() { return false; }
    @Override public int getNumeroPasos() { return 0; }

    public int getNumero() { return numero; }
    public int getTamano() { return tamaño; }
    public int[][] getPuntosIniciales() { return puntos; }
    public int getTiempoLimite() { return tiempoLimite; }
    public int getPuntajeBase() { return puntajeBase; }
    public String getNombre() { return nombre; }
}
