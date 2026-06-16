package io.proyecto2.flowfree.datos;


import java.io.*;
import java.nio.file.*;

public final class ArchivoHistorial {

    private ArchivoHistorial() {}
    
    public static void guardar(String nombreUsuario, HistorialPartidas historial)
            throws IOException {
        Path ruta = GestorArchivoUsuario.rutaHistorial(nombreUsuario);
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(
                    new FileOutputStream(ruta.toFile())))) {
            oos.writeObject(historial);
        }
    }
    
    public static HistorialPartidas cargar(String nombreUsuario) throws IOException {
        Path ruta = GestorArchivoUsuario.rutaHistorial(nombreUsuario);

        if (!Files.exists(ruta) || Files.size(ruta) == 0) {
            return new HistorialPartidas();
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(ruta.toFile())))) {
            
            return (HistorialPartidas) ois.readObject();

        } catch (ClassNotFoundException e) {
            
            System.err.println("Historial incompatible para: " + nombreUsuario);
            return new HistorialPartidas();
        }
    }
}
