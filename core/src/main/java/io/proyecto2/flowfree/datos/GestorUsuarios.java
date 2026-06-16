package io.proyecto2.flowfree.datos;

import io.proyecto2.flowfree.datos.exceptions.*;
import io.proyecto2.flowfree.usuario.Usuario;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GestorUsuarios {

    private static GestorUsuarios instancia;
    private Usuario usuarioActivo;
    
    private GestorUsuarios() {}
    
    public static GestorUsuarios getInstance() {
        if (instancia == null) instancia = new GestorUsuarios();
        return instancia;
    }
    
    public Usuario registrar(String nombreUsuario, String contraseña, String nombreCompleto) throws UsuarioYaExisteException, ContraseñaInvalidaException, IOException {

        if (ArchivoUsuario.existeUsuario(nombreUsuario)) {
            throw new UsuarioYaExisteException(
                "El usuario '" + nombreUsuario + "' ya está registrado.");
        }
        
        validarContrasena(contraseña);
        
        GestorArchivoUsuario.crearEstructura(nombreUsuario);
        
        Usuario nuevo = new Usuario(nombreUsuario, contraseña, nombreCompleto);
        ArchivoUsuario.guardar(nuevo);
        
        ArchivoEstadisticas.inicializar(nombreUsuario);
        
        GestorRanking.actualizarPuntuacion(nombreUsuario, 0, 1, 0L);

        usuarioActivo = nuevo;
        return nuevo;
    }
    
    public Usuario iniciarSesion(String nombreUsuario, String contraseña)
            throws UsuarioNoEncontradoException, ContraseñaIncorrectaException, ArchivoCorruptoException {
        
        Usuario usuario = ArchivoUsuario.cargar(nombreUsuario);
        
        if (!usuario.verificarContrasena(contraseña)) {
            throw new ContraseñaIncorrectaException("Contraseña incorrecta.");
        }
        
        usuario.actualizarUltimaSesion();
        usuarioActivo = usuario;
        
        guardarEnHiloSecundario(usuario);
        
        return usuario;
    }
    
    public void guardarProgreso() {
        if (usuarioActivo != null) guardarEnHiloSecundario(usuarioActivo);
    }
    
    private void guardarEnHiloSecundario(Usuario usuario) {
        new Thread(() -> {
            try {
                ArchivoUsuario.guardar(usuario);
            } catch (IOException e) {
                System.err.println("Error guardando usuario: " + e.getMessage());
            }
        }, "GuardadoUsuario-" + usuario.getNombreUsuario()).start();
    }
    
    public void cerrarSesion() {
        guardarProgreso();
        usuarioActivo = null;
    }
    
    public List<String> getRequisitosContraseña(String contraseña) {
        List<String> fallos = new ArrayList<>();
        if (contraseña.length() < 8)
            fallos.add("Mínimo 8 caracteres");
        if (!contraseña.matches(".*[A-Z].*"))
            fallos.add("Al menos una mayúscula");
        if (!contraseña.matches(".*[0-9].*"))
            fallos.add("Al menos un número");
        if (!contraseña.matches(".*[!@#$%^&*].*"))
            fallos.add("Al menos un símbolo (!@#$%^&*)");
        return fallos;
    }
    
    private void validarContrasena(String c) throws ContraseñaInvalidaException {
        List<String> fallos = getRequisitosContraseña(c);
        if (!fallos.isEmpty())
            throw new ContraseñaInvalidaException(String.join(", ", fallos));
    }
    
    public Usuario getUsuarioActivo() { return usuarioActivo; }
}
