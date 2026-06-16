package io.proyecto2.flowfree.hilos;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class CargadorRecursos {

    private final AssetManager assets;
    private volatile float progreso = 0f;
    private volatile boolean listo  = false;
    
    public CargadorRecursos(AssetManager assets) {
        this.assets = assets;
        assets.load("textures/celda.png",    Texture.class);
        assets.load("textures/flujo.png",    Texture.class);
        assets.load("textures/fondo.png",    Texture.class);
        assets.load("textures/boton.png",    Texture.class);
        assets.load("textures/avatar_default.png", Texture.class);
    }
    
    public boolean actualizar() {
        
        if (listo) return true;

        if (assets.update()) {
            listo    = true;
            progreso = 1f;
            Gdx.app.log("CargadorRecursos", "Todos los assets cargados.");
        } else {
            progreso = assets.getProgress();
        }
        return listo;
    }
    
    public Texture getTextura(String ruta) {
        return assets.get(ruta, Texture.class);
    }
    
    public float getProgreso() { return progreso; }
    public boolean isListo()   { return listo; }
}