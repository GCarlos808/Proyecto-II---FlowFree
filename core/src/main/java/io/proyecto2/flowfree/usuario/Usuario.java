package io.proyecto2.flowfree.usuario;

import java.io.*;
import java.time.LocalDateTime;

public class Usuario implements Guardable, Serializable {

    private static final long serialVersionUID = 1L;
    
    private String nombreUsuario;
    private String contraseñaHash;
    private String nombreCompleto;
    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimaSesion;
    private String rutaAvatar;
    
    private int nivelActual;
    private int vidasRestantes;
    private int puntuacion;
    private long tiempoTotalJugadoMs;
    
    private Preferencias preferencias;
    private Estadisticas estadisticas;
    private ListaAmigos amigos;
    
    public Usuario(String nombreUsuario, String contraseña, String nombreCompleto) {
        this.nombreUsuario = nombreUsuario;
        this.contraseñaHash = hashContrasena(contraseña);
        this.nombreCompleto = nombreCompleto;
        this.fechaRegistro = LocalDateTime.now();
        this.ultimaSesion = LocalDateTime.now();
        this.nivelActual = 1;
        this.vidasRestantes = 3;
        this.puntuacion = 0;
        this.tiempoTotalJugadoMs = 0;
        this.preferencias = new Preferencias();
        this.estadisticas = new Estadisticas();
        this.amigos = new ListaAmigos();
        this.rutaAvatar = "avatars/default.png";
    }
    
    private String hashContrasena(String contraseña) {
        try {
            var digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(contraseña.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error al hashear contraseña", e);
        }
    }
    
    public boolean verificarContrasena(String contraseña) {
        return this.contraseñaHash.equals(hashContrasena(contraseña));
    }
    
    @Override
    public void guardar(DataOutputStream out) throws IOException {
        out.writeUTF(nombreUsuario);
        out.writeUTF(contraseñaHash);
        out.writeUTF(nombreCompleto);
        out.writeUTF(fechaRegistro.toString());
        out.writeUTF(ultimaSesion.toString());
        out.writeUTF(rutaAvatar);
        out.writeInt(nivelActual);
        out.writeInt(vidasRestantes);
        out.writeInt(puntuacion);
        out.writeLong(tiempoTotalJugadoMs);
        preferencias.guardar(out);
        estadisticas.guardar(out);
        amigos.guardar(out);
    }
    
    @Override
    public void cargar(DataInputStream in) throws IOException {
        nombreUsuario = in.readUTF();
        contraseñaHash = in.readUTF();
        nombreCompleto = in.readUTF();
        fechaRegistro = LocalDateTime.parse(in.readUTF());
        ultimaSesion = LocalDateTime.parse(in.readUTF());
        rutaAvatar = in.readUTF();
        nivelActual = in.readInt();
        vidasRestantes = in.readInt();
        puntuacion = in.readInt();
        tiempoTotalJugadoMs = in.readLong();
        preferencias = new Preferencias(); preferencias.cargar(in);
        estadisticas = new Estadisticas(); estadisticas.cargar(in);
        amigos = new ListaAmigos();  amigos.cargar(in);
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
            "\n  nombreUsuario  = '" + nombreUsuario + "'" +
            "\n  nombreCompleto = '" + nombreCompleto + "'" +
            "\n  fechaRegistro  = " + fechaRegistro +
            "\n  ultimaSesion   = " + ultimaSesion +
            "\n  nivelActual    = " + nivelActual +
            "\n  vidas          = " + vidasRestantes +
            "\n  puntuacion     = " + puntuacion +
            "\n  partidas       = " + estadisticas.getPartidasJugadas() +
            "\n}";
    }
    
    public String getNombreUsuario() { return nombreUsuario; }
    public Estadisticas getEstadisticas() { return estadisticas; }
    public int getNivelActual() { return nivelActual; }
    public void setNivelActual(int n) { this.nivelActual = n; }
    public void actualizarUltimaSesion() { this.ultimaSesion = LocalDateTime.now(); }
    public void agregarTiempoJugado(long ms) { this.tiempoTotalJugadoMs += ms; }
}
