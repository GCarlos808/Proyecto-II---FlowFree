package io.proyecto2.flowfree.usuario;


import io.proyecto2.flowfree.usuario.exceptions.*;


public class GestorUsuarios {

    private static GestorUsuarios instancia;
    private Usuario usuarioActivo;

    private GestorUsuarios() {}

    public static GestorUsuarios getInstance() {
        if (instancia == null) instancia = new GestorUsuarios();
        return instancia;
    }
    
    public Usuario registrar(String nombreUsuario, String contraseña, String nombreCompleto)
            throws UsuarioYaExisteException, ContraseñaInvalidaException, IOException {
        
        if (ArchivoUsuario.existeUsuario(nombreUsuario)) {
            throw new UsuarioYaExisteException(
                "El usuario '" + nombreUsuario + "' ya está registrado.");
        }
        
        validarContrasena(contraseña)
                
        Usuario nuevo = new Usuario(nombreUsuario, contraseña, nombreCompleto);
        ArchivoUsuario.guardar(nuevo);

        usuarioActivo = nuevo;
        return nuevo;
    }
    
    public Usuario iniciarSesion(String nombreUsuario, String contraseña)
            throws UsuarioNoEncontradoException, ContraseñaIncorrectaException, ArchivoCorruptoException {
        
        Usuario usuario = ArchivoUsuario.cargar(nombreUsuario);

        if (!usuario.verificarContrasena(contrasena)) {
            throw new ContraseñaIncorrectaException("Contraseña incorrecta.");
        }
        
        usuario.actualizarUltimaSesion();
        usuarioActivo = usuario;
        
        guardarEnHiloSecundario(usuario);

        return usuario;
    }
    
    public void guardarProgreso() {
        if (usuarioActivo != null) {
            guardarEnHiloSecundario(usuarioActivo);
        }
    }
    
    private void guardarEnHiloSecundario(Usuario usuario) {
        new Thread(() -> {
            try {
                ArchivoUsuario.guardar(usuario);
            } catch (IOException e) {
                
                System.err.println("Error guardando usuario: " + e.getMessage());
            }
        }, "hilo-guardado-" + usuario.getNombreUsuario()).start();
    }
    
    public java.util.List<String> getRequisitosContrasena(String contrasena) {
        java.util.List<String> fallos = new java.util.ArrayList<>();
        if (contrasena.length() < 8)
            fallos.add("Mínimo 8 caracteres");
        if (!contrasena.matches(".*[A-Z].*"))
            fallos.add("Al menos una mayúscula");
        if (!contrasena.matches(".*[0-9].*"))
            fallos.add("Al menos un número");
        if (!contrasena.matches(".*[!@#$%^&*].*"))
            fallos.add("Al menos un símbolo (!@#$%^&*)");
        return fallos;
    }
    
    private void validarContrasena(String contrasena) throws ContraseñaInvalidaException {
        var fallos = getRequisitosContrasena(contrasena);
        if (!fallos.isEmpty()) {
            throw new ContraseñaInvalidaException(
                "Contraseña inválida: " + String.join(", ", fallos));
        }
    }

    public Usuario getUsuarioActivo() { return usuarioActivo; }
    public void cerrarSesion() { guardarProgreso(); usuarioActivo = null; }
}
