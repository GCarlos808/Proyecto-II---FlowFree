package io.proyecto2.flowfree;

import com.badlogic.gdx.Gdx;
import java.util.concurrent.atomic.AtomicBoolean;


public class TimerNivel extends Thread {

    private volatile int     tiempoRestante;
    private volatile boolean pausado  = false;
    private final AtomicBoolean corriendo = new AtomicBoolean(false);
    private final Runnable   onTiempoAgotado;

    public TimerNivel(int segundosIniciales, Runnable onTiempoAgotado) {
        super("TimerNivel");
        this.tiempoRestante    = segundosIniciales;
        this.onTiempoAgotado   = onTiempoAgotado;
        setDaemon(true);
    }

    @Override
    public void run() {
        corriendo.set(true);

        while (corriendo.get() && tiempoRestante > 0) {
            
            while (pausado && corriendo.get()) {
                try { Thread.sleep(50); }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            
            if (!pausado && corriendo.get()) {
                tiempoRestante--;
            }
        }
        
        if (corriendo.get() && tiempoRestante <= 0) {
            Gdx.app.postRunnable(onTiempoAgotado);
        }
    }
    
    public void pausar()   { pausado = true; }
    public void reanudar() { pausado = false; }

    public void detener() {
        corriendo.set(false);
        interrupt();
    }
    
    public int     getTiempoRestante() { return tiempoRestante; }
    public boolean estaCorreindo()     { return corriendo.get(); }
}
