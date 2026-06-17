package io.proyecto2.flowfree.datos;

import io.proyecto2.flowfree.constantes.Constantes;
import io.proyecto2.flowfree.util.GestorAvatares;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * GestorArchivoUsuario — maneja el árbol de carpetas por usuario.
 
 * Estructura en disco:
 *   data/
 *     usuarios/
 *       juan123/
 *         perfil.dat
 *         estadisticas.dat
 *         historial.dat
 *         avatar.png
 *       maria456/

 *     ranking_global.dat
 */

public final class GestorArchivoUsuario {

    private GestorArchivoUsuario() {}
    
    public static Path rutaCarpeta(String usuario) {
        return Paths.get(Constantes.CARPETA_USUARIOS, usuario);
    }
    public static Path rutaPerfil(String usuario) {
        return rutaCarpeta(usuario).resolve("perfil.dat");
    }
    public static Path rutaEstadisticas(String usuario) {
        return rutaCarpeta(usuario).resolve("estadisticas.dat");
    }
    public static Path rutaHistorial(String usuario) {
        return rutaCarpeta(usuario).resolve("historial.dat");
    }
    public static Path rutaAvatar(String usuario) {
        return rutaCarpeta(usuario).resolve("avatar.png");
    }
    public static Path rutaSolicitudesEntrada(String usuario) {
        return rutaCarpeta(usuario).resolve(Constantes.ARCHIVO_SOLICITUDES_ENTRADA);
    }
    public static Path rutaSolicitudesEnviadas(String usuario) {
        return rutaCarpeta(usuario).resolve(Constantes.ARCHIVO_SOLICITUDES_ENVIADAS);
    }
    public static Path rutaDesafiosEntrada(String usuario) {
        return rutaCarpeta(usuario).resolve(Constantes.ARCHIVO_DESAFIOS_ENTRADA);
    }
    
    public static void crearEstructura(String nombreUsuario) throws IOException {
        Path carpeta = rutaCarpeta(nombreUsuario);
        Files.createDirectories(carpeta);
        
        crearSiNoExiste(rutaPerfil(nombreUsuario));
        crearSiNoExiste(rutaEstadisticas(nombreUsuario));
        crearSiNoExiste(rutaHistorial(nombreUsuario));
        copiarAvatarDefault(rutaAvatar(nombreUsuario));
    }
    
    private static void crearSiNoExiste(Path ruta) throws IOException {
        if (!Files.exists(ruta)) Files.createFile(ruta);
    }
    
    private static void copiarAvatarDefault(Path destino) {
        try {
            if (Files.exists(destino) && Files.size(destino) > 0) return;
            GestorAvatares.exportar(destino, "default");
        } catch (IOException e) {
            System.err.println("No se pudo copiar avatar: " + e.getMessage());
        }
    }
    
    public static boolean existeUsuario(String nombreUsuario) {
        return Files.exists(rutaPerfil(nombreUsuario));
    }
    
    public static List<String> listarUsuarios() throws IOException {
        Path raiz = Paths.get(Constantes.CARPETA_USUARIOS);
        if (!Files.exists(raiz)) return Collections.emptyList();

        try (var stream = Files.list(raiz)) {
            return stream
                .filter(Files::isDirectory)
                .map(p -> p.getFileName().toString())
                .sorted()
                .toList();
        }
    }
    
    public static void eliminarEstructura(String nombreUsuario) throws IOException {
        Path carpeta = rutaCarpeta(nombreUsuario);
        if (!Files.exists(carpeta)) return;
        Files.walk(carpeta).sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                 try { Files.delete(p); }
                 catch (IOException e) {
                     System.err.println("No se pudo borrar: " + p);
                 }
        });
    }
}
