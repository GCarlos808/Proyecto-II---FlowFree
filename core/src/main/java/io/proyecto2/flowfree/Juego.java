package io.proyecto2.flowfree;

import io.proyecto2.flowfree.hilos.TimerNivel;
import io.proyecto2.flowfree.usuario.Guardable;
import io.proyecto2.flowfree.usuario.Usuario;
import java.io.*;

public abstract class Juego implements Juegable, Guardable {

    protected EstadoJuego estado;
    protected int nivelActual;
    protected int vidas;
    protected int puntuacion;
    protected int fallos;
    protected long tiempoInicioNivel;
    protected int tiempoLimiteSegundos;

    protected Usuario usuarioActivo;
    protected TimerNivel timer;

    protected Juego(Usuario usuario) {
        this.usuarioActivo = usuario;
        this.estado = EstadoJuego.MENU;
        this.nivelActual = usuario.getNivelActual();
        this.vidas = usuario.getVidasRestantes() > 0 ? usuario.getVidasRestantes() : 3;
        this.puntuacion = usuario.getPuntuacion();
        this.fallos = 0;
        this.tiempoLimiteSegundos = 120;
    }

    protected abstract void iniciarNivel(int numeroNivel);
    protected abstract boolean verificarVictoria();
    protected abstract void aplicarDerrota();
    protected abstract int calcularPuntuacion();

    public void iniciarDesdeNivel(int numeroNivel) {
        this.nivelActual = numeroNivel;
        this.estado = EstadoJuego.JUGANDO;
        this.tiempoInicioNivel = System.currentTimeMillis();
        this.fallos = 0;
        usuarioActivo.getEstadisticas().registrarPartidaIniciada();
        iniciarNivel(numeroNivel);
        iniciarTimer();
    }

    @Override
    public void iniciar() {
        iniciarDesdeNivel(nivelActual);
    }

    @Override
    public void pausar() {
        if (estado == EstadoJuego.JUGANDO) {
            estado = EstadoJuego.PAUSADO;
            if (timer != null) timer.pausar();
        }
    }

    @Override
    public void reanudar() {
        if (estado == EstadoJuego.PAUSADO) {
            estado = EstadoJuego.JUGANDO;
            if (timer != null) timer.reanudar();
        }
    }

    @Override
    public void reiniciar() {
        fallos++;
        vidas--;
        usuarioActivo.getEstadisticas().registrarFallo();
        if (vidas <= 0) {
            estado = EstadoJuego.DERROTA;
            aplicarDerrota();
        } else {
            estado = EstadoJuego.JUGANDO;
            tiempoInicioNivel = System.currentTimeMillis();
            iniciarNivel(nivelActual);
            iniciarTimer();
        }
    }

    @Override
    public void actualizar(float delta) {
        if (estado != EstadoJuego.JUGANDO) return;
        if (verificarVictoria()) {
            onNivelCompletado();
        }
    }

    protected void onNivelCompletado() {
        if (timer != null) timer.detener();

        int puntosNivel = calcularPuntuacion();
        long tiempoMs = tiempoTranscurrido();
        puntuacion += puntosNivel;

        usuarioActivo.getEstadisticas().registrarNivelCompletado(
            nivelActual, tiempoMs, puntosNivel);
        usuarioActivo.agregarTiempoJugado(tiempoMs);
        usuarioActivo.setPuntuacion(puntuacion);
        usuarioActivo.setVidasRestantes(vidas);

        if (esUltimoNivel()) {
            estado = EstadoJuego.VICTORIA;
        } else {
            int siguiente = nivelActual + 1;
            usuarioActivo.setNivelActual(Math.max(usuarioActivo.getNivelActual(), siguiente));
            nivelActual = siguiente;
            estado = EstadoJuego.JUGANDO;
            tiempoInicioNivel = System.currentTimeMillis();
            iniciarNivel(nivelActual);
            iniciarTimer();
        }
    }

    private boolean esUltimoNivel() {
        return nivelActual >= io.proyecto2.flowfree.constantes.Constantes.NIVEL_MAX;
    }

    private void iniciarTimer() {
        if (timer != null) timer.detener();
        timer = new TimerNivel(tiempoLimiteSegundos, this::onTiempoAgotado);
        timer.start();
    }

    public void onTiempoAgotado() {
        com.badlogic.gdx.Gdx.app.postRunnable(this::reiniciar);
    }

    public int getTiempoRestante() {
        return timer != null ? timer.getTiempoRestante() : 0;
    }

    protected long tiempoTranscurrido() {
        return System.currentTimeMillis() - tiempoInicioNivel;
    }

    public void dispose() {
        if (timer != null) timer.detener();
    }

    @Override
    public void guardar(DataOutputStream out) throws IOException {
        out.writeInt(nivelActual);
        out.writeInt(vidas);
        out.writeInt(puntuacion);
        out.writeInt(fallos);
        out.writeUTF(estado.name());
    }

    @Override
    public void cargar(DataInputStream in) throws IOException {
        nivelActual = in.readInt();
        vidas = in.readInt();
        puntuacion = in.readInt();
        fallos = in.readInt();
        estado = EstadoJuego.valueOf(in.readUTF());
    }

    public Usuario getUsuarioActivo() { return usuarioActivo; }
    public EstadoJuego getEstado() { return estado; }
    public int getNivelActual() { return nivelActual; }
    public int getVidas() { return vidas; }
    public int getPuntuacion() { return puntuacion; }
    public int getFallos() { return fallos; }
}
