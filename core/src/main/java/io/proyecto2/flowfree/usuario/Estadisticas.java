package io.proyecto2.flowfree.usuario;

import java.io.*;

public class Estadisticas implements Guardable, Serializable {

    private static final long serialVersionUID = 1L;

    private int  partidasJugadas;
    private int  nivelesCompletados;
    private long tiempoTotalMs;
    private int  mejorPuntuacion;
    private int  rachaActual;
    private int  rachaMejor;
    private int  totalFallos;
    private long tiempoPromedioNivelMs;
    
    public Estadisticas() {}
    
    public Estadisticas(int partidas, int niveles, long tiempo, int mejorPts, int racha) {
        this.partidasJugadas = partidas;
        this.nivelesCompletados = niveles;
        this.tiempoTotalMs  = tiempo;
        this.mejorPuntuacion = mejorPts;
        this.rachaActual = racha;
    }
    
    
    public void registrarPartidaIniciada() {
        partidasJugadas++;
    }
    
    public void registrarNivelCompletado(int nivel, long tiempoMs, int puntos) {
        nivelesCompletados++;
        tiempoTotalMs += tiempoMs;
        rachaActual++;
        if (rachaActual > rachaMejor) rachaMejor = rachaActual;
        if (puntos > mejorPuntuacion) mejorPuntuacion = puntos;
        if (nivelesCompletados > 0)
            tiempoPromedioNivelMs = tiempoTotalMs / nivelesCompletados;
    }
    
    public void registrarFallo() {
        totalFallos++;
        rachaActual = 0;
    }
    
    @Override
    public void guardar(DataOutputStream out) throws IOException {
        out.writeInt(partidasJugadas);
        out.writeInt(nivelesCompletados);
        out.writeLong(tiempoTotalMs);
        out.writeInt(mejorPuntuacion);
        out.writeInt(rachaActual);
        out.writeInt(rachaMejor);
        out.writeInt(totalFallos);
    }
    
    @Override
    public void cargar(DataInputStream in) throws IOException {
        partidasJugadas = in.readInt();
        nivelesCompletados = in.readInt();
        tiempoTotalMs = in.readLong();
        mejorPuntuacion = in.readInt();
        rachaActual = in.readInt();
        rachaMejor = in.readInt();
        totalFallos = in.readInt();
        if (nivelesCompletados > 0)
            tiempoPromedioNivelMs = tiempoTotalMs / nivelesCompletados;
    }
    
    public int  getPartidasJugadas() { return partidasJugadas; }
    public int  getNivelesCompletados() { return nivelesCompletados; }
    public long getTiempoTotalMs() { return tiempoTotalMs; }
    public int  getMejorPuntuacion() { return mejorPuntuacion; }
    public int  getRachaActual() { return rachaActual; }
    public int  getRachaMejor() { return rachaMejor; }
    public int  getTotalFallos() { return totalFallos; }
    public long getTiempoPromedioMs() { return tiempoPromedioNivelMs; }
    
    public String getTiempoTotalFormateado() {
        long seg = tiempoTotalMs / 1000;
        return String.format("%02d:%02d", seg / 60, seg % 60);
    }
}
