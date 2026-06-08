package io.proyecto2.flowfree.usuario;

import io.proyecto2.flowfree.usuario.exceptions;.UsuarioNoEncontradoException;
import io.proyecto2.flowfree.usuario.exceptions.ArchivoCorruptoException;

import java.io.*;
import java.nio.file.*;

public class ArchivoUsuario {

    private static Path getRutaPerfil(String nombreUsuario) {
        return Paths.get(Constantes.CARPETA_USUARIOS, nombreUsuario, "perfil.dat");
    }
    
    public static void guardar(Usuario usuario) throws IOException {
        Path carpeta = Paths.get(Constantes.CARPETA_USUARIOS, usuario.getNombreUsuario());
        Files.createDirectories(carpeta);

        Path archivo = getRutaPerfil(usuario.getNombreUsuario());
        try (DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(
                    new FileOutputStream(archivo.toFile())))) {
            usuario.guardar(out);
        }
    }
    
    public static Usuario cargar(String nombreUsuario)
            throws UsuarioNoEncontradoException, ArchivoCorruptoException {
        
        Path archivo = getRutaPerfil(nombreUsuario);
        
        if (!Files.exists(archivo)) {
            throw new UsuarioNoEncontradoException(
                "No existe el usuario: " + nombreUsuario);
        }
        
        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(
                    new FileInputStream(archivo.toFile())))) {
            
            Usuario usuario = new Usuario();
            usuario.cargar(in);
            return usuario;

        } catch (IOException e) {
            throw new ArchivoCorruptoException(
                "Archivo corrupto para: " + nombreUsuario, e);
        }
    }
    
    public static boolean existeUsuario(String nombreUsuario) {
        return Files.exists(getRutaPerfil(nombreUsuario));
    }
    
    public static void eliminar(String nombreUsuario) throws IOException {
        Path carpeta = Paths.get(Constantes.CARPETA_USUARIOS, nombreUsuario);
        if (Files.exists(carpeta)) {
            Files.walk(carpeta)
                 .sorted(java.util.Comparator.reverseOrder())
                 .forEach(p -> { try { Files.delete(p); } catch (IOException ignored) {} });
        }
    }
}
