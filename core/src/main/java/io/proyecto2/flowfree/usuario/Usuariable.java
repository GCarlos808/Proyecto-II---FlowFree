package io.proyecto2.flowfree.usuario;


public interface Usuariable {

    String getNombreUsuario();
    boolean verificarContrasena(String contrasena);
    Estadisticas getEstadisticas();
}
