package io.proyecto2.flowfree.datos;


import java.io.Serializable;
import java.time.LocalDateTime;

public class RegistroPartida implements Serializable {

    private static final long serialVersionUID = 1L;
    public final int numeroNivel;
    public final int puntuacion;
    public final long tiempoMs;
    public final int fallos;
    public final boolean completado;
    public final LocalDateTime fecha;

    public RegistroPartida(int nivel, int puntos, long tiempo, int fallos, boolean completado) {
        this.numeroNivel = nivel;
        this.puntuacion = puntos;
        this.tiempoMs = tiempo;
        this.fallos = fallos;
        this.completado = completado;
        this.fecha = LocalDateTime.now();
    }
    
    public String getTiempoFormateado() {
        long seg = tiempoMs / 1000;
        return String.format("%02d:%02d", seg / 60, seg % 60);
    }
    
    @Override
    public String toString() {
        return "Nivel " + numeroNivel
             + " | " + (completado ? "Completado" : "Fallado")
             + " | " + puntuacion + " pts"
             + " | " + getTiempoFormateado()
             + " | " + fecha.toLocalDate();
    }
}
