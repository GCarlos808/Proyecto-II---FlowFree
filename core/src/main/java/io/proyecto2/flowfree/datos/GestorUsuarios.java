package io.proyecto2.flowfree.datos;

import io.proyecto2.flowfree.datos.exceptions.*;
import io.proyecto2.flowfree.usuario.Usuario;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class GestorUsuarios {

    private static GestorUsuarios instancia;
    private final Object lockSesion = new Object();
    private final ExecutorService escritor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "GuardadoUsuario");
        t.setDaemon(true);
        return t;
    });
    private Usuario usuarioActivo;
    
    private GestorUsuarios() {}
    
    public static GestorUsuarios getInstance() {
        if (instancia == null) instancia = new GestorUsuarios();
        return instancia;
    }
    
    public Usuario registrar(String nombreUsuario, String contraseña, String nombreCompleto) throws UsuarioYaExisteException, ContraseñaInvalidaException, IOException {
        if (nombreUsuario == null || !nombreUsuario.matches("[a-zA-Z0-9._-]{3,20}")) {
            throw new ContraseñaInvalidaException(
                "El usuario debe tener 3-20 caracteres y solo usar letras, números, ., _ o -");
        }

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

        synchronized (lockSesion) {
            usuarioActivo = nuevo;
        }
        return nuevo;
    }
    
    public Usuario iniciarSesion(String nombreUsuario, String contraseña)
            throws UsuarioNoEncontradoException, ContraseñaIncorrectaException, ArchivoCorruptoException {
        
        Usuario usuario = ArchivoUsuario.cargar(nombreUsuario);
        
        if (!usuario.verificarContrasena(contraseña)) {
            throw new ContraseñaIncorrectaException("Contraseña incorrecta.");
        }
        
        usuario.actualizarUltimaSesion();
        synchronized (lockSesion) {
            usuarioActivo = usuario;
        }
        
        guardarEnHiloSecundario(usuario);
        
        return usuario;
    }
    
    public void guardarProgreso() {
        Usuario local;
        synchronized (lockSesion) {
            local = usuarioActivo;
        }
        if (local != null) guardarEnHiloSecundario(local);
    }

    public void guardarProgresoAhora() throws IOException {
        Usuario local;
        synchronized (lockSesion) {
            local = usuarioActivo;
        }
        if (local != null) {
            ArchivoUsuario.guardar(local);
        }
    }
    
    private void guardarEnHiloSecundario(Usuario usuario) {
        escritor.submit(() -> {
            try {
                ArchivoUsuario.guardar(usuario);
            } catch (IOException e) {
                System.err.println("Error guardando usuario: " + e.getMessage());
            }
        });
    }
    
    public void cerrarSesion() {
        guardarProgreso();
        synchronized (lockSesion) {
            usuarioActivo = null;
        }
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

    /** Alias sin tilde para compatibilidad con pantallas existentes. */
    public List<String> getRequisitosContrasena(String contraseña) {
        return getRequisitosContraseña(contraseña);
    }
    
    private void validarContrasena(String c) throws ContraseñaInvalidaException {
        List<String> fallos = getRequisitosContraseña(c);
        if (!fallos.isEmpty())
            throw new ContraseñaInvalidaException(String.join(", ", fallos));
    }
    
    public void detener() {
        escritor.shutdown();
        try {
            if (!escritor.awaitTermination(3, TimeUnit.SECONDS)) {
                escritor.shutdownNow();
            }
        } catch (InterruptedException e) {
            escritor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    public Usuario getUsuarioActivo() {
        synchronized (lockSesion) {
            return usuarioActivo;
        }
    }
}
