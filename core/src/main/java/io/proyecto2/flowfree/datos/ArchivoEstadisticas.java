package io.proyecto2.flowfree.datos;

import io.proyecto2.flowfree.usuario.Estadisticas;

import java.io.*;
import java.nio.file.*;

/**
 * ArchivoEstadisticas
 
 estadisticas.dat
 
 * offset  0: int  partidasJugadas
 * offset  4: int  nivelesCompletados
 * offset  8: long tiempoTotalMs
 * offset 16: int  mejorPuntuacion
 * offset 20: int  rachaActual
 * ffset 24: int  rachaMejor
 * offset 28: int  totalFallos
 
 
 TOTAL: 32 bytes
 
 */

public final class ArchivoEstadisticas {

    private static final int OFF_PARTIDAS = 0;
    private static final int OFF_NIVELES = 4;
    private static final int OFF_TIEMPO = 8;
    private static final int OFF_MEJOR_PTS = 16;
    private static final int OFF_RACHA_ACT = 20;
    private static final int OFF_RACHA_MEJ = 24;
    private static final int OFF_FALLOS = 28;
    private static final int TAMAÑO = 32;
    
    private ArchivoEstadisticas() {}
    
    public static void inicializar(String nombreUsuario) throws IOException {
        Path ruta = GestorArchivoUsuario.rutaEstadisticas(nombreUsuario);
        try {
            if (Files.size(ruta) == 0) {
                try (RandomAccessFile raf = new RandomAccessFile(ruta.toFile(), "rw")) {
                    raf.write(new byte[TAMAÑO]);
                }
            }
        } catch (IOException e) {
            throw new IOException("No se pudo inicializar estadisticas.dat", e);
        }
    }
    
    public static void guardar(String nombreUsuario, Estadisticas est) throws IOException {
        
        Path ruta = GestorArchivoUsuario.rutaEstadisticas(nombreUsuario);
        try (RandomAccessFile raf = new RandomAccessFile(ruta.toFile(), "rw")) {
            raf.seek(0);
            raf.writeInt(est.getPartidasJugadas());
            raf.writeInt(est.getNivelesCompletados());
            raf.writeLong(est.getTiempoTotalMs());
            raf.writeInt(est.getMejorPuntuacion());
            raf.writeInt(est.getRachaActual());
            raf.writeInt(est.getRachaMejor());
            raf.writeInt(est.getTotalFallos());
        }
    }
    
    public static Estadisticas leer(String nombreUsuario) throws IOException {
        Path ruta = GestorArchivoUsuario.rutaEstadisticas(nombreUsuario);
        if (!Files.exists(ruta) || Files.size(ruta) < TAMAÑO) {
            return new Estadisticas();
        }
        
        try (RandomAccessFile raf = new RandomAccessFile(ruta.toFile(), "r")) {
            raf.seek(0);
            Estadisticas est = new Estadisticas();
            DataInputStream in = new DataInputStream(new FileInputStream(ruta.toFile()));
            est.cargar(in);
            in.close();
            return est;
        }
    }
    
    public static void incrementarPartidas(String nombreUsuario) throws IOException {
        actualizarInt(nombreUsuario, OFF_PARTIDAS, 1);
    }
    
    public static void actualizarMejorPuntuacion(String nombreUsuario, int pts) throws IOException {
        Path ruta = GestorArchivoUsuario.rutaEstadisticas(nombreUsuario);
        try (RandomAccessFile raf = new RandomAccessFile(ruta.toFile(), "rw")) {
            raf.seek(OFF_MEJOR_PTS);
            int actual = raf.readInt();
            if (pts > actual) {
                raf.seek(OFF_MEJOR_PTS);
                raf.writeInt(pts);
            }
        }
    }
    
    private static void actualizarInt(String usuario, int offset, int delta)
            throws IOException {
        Path ruta = GestorArchivoUsuario.rutaEstadisticas(usuario);
        try (RandomAccessFile raf = new RandomAccessFile(ruta.toFile(), "rw")) {
            raf.seek(offset);
            int actual = raf.readInt();
            raf.seek(offset);
            raf.writeInt(actual + delta);
        }
    }
}
