package io.proyecto2.flowfree.datos;

import io.proyecto2.flowfree.datos.exceptions.ArchivoCorruptoException;
import io.proyecto2.flowfree.datos.exceptions.UsuarioNoEncontradoException;
import io.proyecto2.flowfree.usuario.Usuario;

import java.io.*;
import java.nio.file.*;


public final class ArchivoUsuario {

    private ArchivoUsuario() {}
    
    public static void guardar(Usuario usuario) throws IOException {
        Path ruta = GestorArchivoUsuario.rutaPerfil(usuario.getNombreUsuario());
        
        try (DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(ruta.toFile())))) {
            usuario.guardar(out);
        }
    }
    
    public static Usuario cargar(String nombreUsuario) throws UsuarioNoEncontradoException, ArchivoCorruptoException {
        
        Path ruta = GestorArchivoUsuario.rutaPerfil(nombreUsuario);
        
        if (!Files.exists(ruta)) {
            throw new UsuarioNoEncontradoException("No existe el usuario: " + nombreUsuario);
        }
        
        try {
            if (Files.size(ruta) == 0) {
                throw new ArchivoCorruptoException(
                    "Archivo de perfil vacío para: " + nombreUsuario, null);
            }
            
        } catch (IOException e) {
            throw new ArchivoCorruptoException("Error verificando tamaño", e);
        }
        
        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(
                    new FileInputStream(ruta.toFile())))) {Usuario usuario = new Usuario();
                    
            usuario.cargar(in);
            return usuario;
            
        } catch (EOFException e) {
            throw new ArchivoCorruptoException(
                "Perfil DAÑADO para: " + nombreUsuario, e);
        } catch (IOException e) {
            throw new ArchivoCorruptoException(
                "Error leyendo perfil de: " + nombreUsuario, e);
        }
    }
    
    public static boolean existeUsuario(String nombreUsuario) {
        return GestorArchivoUsuario.existeUsuario(nombreUsuario);
    }
}
