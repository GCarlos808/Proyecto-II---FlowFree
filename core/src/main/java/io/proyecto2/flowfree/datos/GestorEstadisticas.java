package io.proyecto2.flowfree.datos;


import com.badlogic.gdx.Gdx;
import io.proyecto2.flowfree.usuario.Usuario;
import io.proyecto2.flowfree.usuario.Estadisticas;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class GestorEstadisticas {

    private static GestorEstadisticas instancia;

    private final Usuario  usuario;
    private final Estadisticas stats;
    private final HistorialPartidas historial;
    private final Object lockDatos = new Object();
    private final AtomicBoolean detenido = new AtomicBoolean(false);
    private final ExecutorService escritor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "StatsWriter");
            t.setDaemon(true);
            return t;
        });
    
    private volatile Runnable onGuardadoCompleto;
    
    private GestorEstadisticas(Usuario usuario) {
        this.usuario = usuario;
        this.stats = usuario.getEstadisticas();
        this.historial = new HistorialPartidas();
    }
    
    public static GestorEstadisticas getInstance(Usuario usuario) {
        if (instancia == null || instancia.usuario != usuario)
            instancia = new GestorEstadisticas(usuario);
        return instancia;
    }
    
    public void setOnGuardadoCompleto(Runnable callback) {
        this.onGuardadoCompleto = callback;
    }
    
    public void registrarNivelCompletado(int nivel, long tiempoMs, int puntuacion, int fallos) {
        if (detenido.get()) return;
        
        synchronized (lockDatos) {
            stats.registrarNivelCompletado(nivel, tiempoMs, puntuacion);
            RegistroPartida registro = new RegistroPartida(nivel, puntuacion, tiempoMs, fallos, true);
            historial.agregar(registro);
        }
        
        guardarEnDisco(puntuacion);
    }
    
    public void registrarFallo() {
        synchronized (lockDatos) {
            stats.registrarFallo();
        }
    }

    public void registrarPartidaIniciada() {
        synchronized (lockDatos) {
            stats.registrarPartidaIniciada();
        }
    }
    
    private void guardarEnDisco(int nuevaPuntuacion) {
        if (detenido.get()) return;
        String nombreUsuario = usuario.getNombreUsuario();
        
        escritor.submit(() -> {
            try {
                synchronized (lockDatos) {
                    ArchivoEstadisticas.guardar(nombreUsuario, stats);
                    ArchivoHistorial.guardar(nombreUsuario, historial);
                    GestorRanking.actualizarPuntuacion(
                        nombreUsuario,
                        nuevaPuntuacion,
                        usuario.getNivelActual(),
                        stats.getTiempoTotalMs()
                    );
                }
                
                Gdx.app.log("GestorEstadisticas",
                    "Stats guardadas para: " + nombreUsuario);
                
                if (onGuardadoCompleto != null) {
                    Gdx.app.postRunnable(onGuardadoCompleto);
                }
                
            } catch (Exception e) {
                Gdx.app.error("GestorEstadisticas",
                    "Error guardando stats", e);
            }
        });
    }
    
    public Estadisticas getEstadisticas() { return stats; }
    public HistorialPartidas getHistorial() { return historial; }
    
    public void detener() {
        if (!detenido.compareAndSet(false, true)) return;
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
}
