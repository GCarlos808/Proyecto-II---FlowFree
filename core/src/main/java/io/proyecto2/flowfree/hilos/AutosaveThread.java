package io.proyecto2.flowfree.hilos;


import com.badlogic.gdx.Gdx;
import io.proyecto2.flowfree.usuario.Usuario;
import io.proyecto2.flowfree.constantes.Constantes;
import io.proyecto2.flowfree.datos.ArchivoUsuario;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AutosaveThread {

    private final ScheduledExecutorService executor;
    private final Usuario usuario;
    private final AtomicBoolean guardandoAhora = new AtomicBoolean(false);
    private final AtomicBoolean detenido = new AtomicBoolean(false);
    
    public AutosaveThread(Usuario usuario) {
        this.usuario  = usuario;
        
        this.executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "AutosaveThread");
            t.setDaemon(true);
            return t;
        });
    }
    
    public void iniciar() {
        if (detenido.get()) return;
        executor.scheduleAtFixedRate(
            this::guardarEnSegundo,
            Constantes.AUTOSAVE_INTERVALO,
            Constantes.AUTOSAVE_INTERVALO,
            TimeUnit.SECONDS
        );
        Gdx.app.log("AutosaveThread", "Autosave activo cada " + Constantes.AUTOSAVE_INTERVALO + "s");
    }
    
    private void guardarEnSegundo() {
        if (detenido.get()) return;
        if (!guardandoAhora.compareAndSet(false, true)) return;
        
        try {
            ArchivoUsuario.guardar(usuario);
            Gdx.app.log("AutosaveThread", "Guardado automático: " + usuario.getNombreUsuario());
            
        } catch (IOException e) {
            
            Gdx.app.error("AutosaveThread",
                "Error en autosave: " + e.getMessage());
        } finally {
            guardandoAhora.set(false);
        }
    }
    
    public void guardarAhora() {
        if (detenido.get()) return;
        executor.submit(this::guardarEnSegundo);
    }
    
    public void detener() {
        if (!detenido.compareAndSet(false, true)) return;
        executor.shutdown();
        try {
            
            if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
            
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        Gdx.app.log("AutosaveThread", "Autosave detenido.");
    }
}
