package io.proyecto2.flowfree.datos;

import io.proyecto2.flowfree.constantes.Constantes;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/*
 * GestorRanking — RandomAccessFile.
 
 * Layout de ranking_global.dat:
 *   offset 0:
 *   offset 32:
 *   offset 36:
 *   offset 40:
 *   offset 48:
 *   TOTAL: 56 BYTES por registro
 */

public final class GestorRanking {

    private static final int LONG_NOMBRE = Constantes.RAF_LONG_NOMBRE;
    private static final int TAMANO_REGISTRO = Constantes.RAF_TAMANO_REGISTRO;
    
    private static final Path RUTA = Paths.get(Constantes.RANKING_GLOBAL);
    
    private GestorRanking() {}
    
    public static void inicializar() throws IOException {
        Files.createDirectories(RUTA.getParent());
        if (!Files.exists(RUTA)) Files.createFile(RUTA);
    }
    
    public static void actualizarPuntuacion(String nombreUsuario, int puntuacion, int nivelAlcanzado, long tiempoTotalMs) throws IOException {
        inicializar();
        try (RandomAccessFile raf = new RandomAccessFile(RUTA.toFile(), "rw")) {
            long pos = buscarPosicion(raf, nombreUsuario);
            if (pos == -1L) pos = raf.length();
            raf.seek(pos);
            escribirRegistro(raf, nombreUsuario, puntuacion, nivelAlcanzado, tiempoTotalMs);
        }
    }
    
    public static List<EntradaRanking> leerTodos() throws IOException {
        List<EntradaRanking> lista = new ArrayList<>();
        if (!Files.exists(RUTA) || Files.size(RUTA) == 0) return lista;
        
        try (RandomAccessFile raf = new RandomAccessFile(RUTA.toFile(), "r")) {
            long total = raf.length() / TAMANO_REGISTRO;
            for (long i = 0; i < total; i++) {
                raf.seek(i * TAMANO_REGISTRO);
                lista.add(leerRegistro(raf));
            }
        }
        lista.sort((a, b) -> Integer.compare(b.puntuacion(), a.puntuacion()));
        return lista;
    }
    
    public static EntradaRanking leerUsuario(String nombreUsuario) throws IOException {
        if (!Files.exists(RUTA)) return null;
        try (RandomAccessFile raf = new RandomAccessFile(RUTA.toFile(), "r")) {
            long pos = buscarPosicion(raf, nombreUsuario);
            if (pos == -1L) return null;
            raf.seek(pos);
            return leerRegistro(raf);
        }
    }
    
    private static long buscarPosicion(RandomAccessFile raf, String nombre)
            throws IOException {
        long total = raf.length() / TAMANO_REGISTRO;
        for (long i = 0; i < total; i++) {
            raf.seek(i * TAMANO_REGISTRO);
            if (leerNombreFijo(raf).equals(nombre)) return i * TAMANO_REGISTRO;
        }
        return -1L;
    }
    
    private static void escribirRegistro(RandomAccessFile raf, String nombre,
                                          int puntuacion, int nivel, long tiempo)
            throws IOException {
        escribirNombreFijo(raf, nombre);
        raf.writeInt(puntuacion);
        raf.writeInt(nivel);
        raf.writeLong(tiempo);
        raf.writeLong(System.currentTimeMillis());
    }
    
    private static EntradaRanking leerRegistro(RandomAccessFile raf) throws IOException {
        String nombre = leerNombreFijo(raf);
        int puntuacion = raf.readInt();
        int nivel = raf.readInt();
        long tiempo = raf.readLong();
        long timestamp = raf.readLong();
        return new EntradaRanking(nombre, puntuacion, nivel, tiempo, timestamp);
    }
    
    private static void escribirNombreFijo(RandomAccessFile raf, String nombre)
            throws IOException {
        byte[] buf = new byte[LONG_NOMBRE];
        byte[] src = nombre.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(src, 0, buf, 0, Math.min(src.length, LONG_NOMBRE));
        raf.write(buf);
    }
    
    private static String leerNombreFijo(RandomAccessFile raf) throws IOException {
        byte[] buf = new byte[LONG_NOMBRE];
        raf.readFully(buf);
        int fin = 0;
        while (fin < buf.length && buf[fin] != 0) fin++;
        return new String(buf, 0, fin, StandardCharsets.UTF_8);
    }
    
    public record EntradaRanking(
        String nombre,
        int    puntuacion,
        int    nivelAlcanzado,
        long   tiempoTotalMs,
        long   timestampActualizacion
    ) {}
}
