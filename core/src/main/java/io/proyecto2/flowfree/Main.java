package io.proyecto2.flowfree;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import io.proyecto2.flowfree.constantes.Constantes;
import io.proyecto2.flowfree.constantes.Estilos;
import io.proyecto2.flowfree.GUI.PantallaCarga;

import java.io.IOException;
import java.nio.file.*;


public class Main extends Game {
    
    public SpriteBatch  batch;
    public AssetManager assets;
    public Texture pixel;
    
    public BitmapFont fontPequena;
    public BitmapFont fontMediana; 
    public BitmapFont fontGrande;
    public BitmapFont fontTitulo;
    
    @Override
    public void create() {
        inicializarRecursos();
        inicializarSistemaDatos();
        setScreen(new PantallaCarga(this));
    }
    
    private void inicializarRecursos() {
        batch  = new SpriteBatch();
        assets = new AssetManager();
        
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        pixel = new Texture(pm);
        pm.dispose();

        cargarFuentes();
    }
    
    private void cargarFuentes() {
        try {
            FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Rajdhani-SemiBold.ttf"));

            fontPequena = buildFont(gen, 16, Estilos.COLOR_TEXTO_CLARO);
            fontMediana = buildFont(gen, 24, Estilos.COLOR_TEXTO_CLARO);
            fontGrande  = buildFont(gen, 40, Estilos.COLOR_ACENTO_CYAN);
            fontTitulo  = buildFont(gen, 64, Estilos.COLOR_ACENTO_CYAN);
            gen.dispose();

        } catch (Exception e) {
            
            Gdx.app.log("Main", "Usando fuente por defecto (falta Rajdhani-SemiBold.ttf)");
            fontPequena = new BitmapFont();
            fontMediana = new BitmapFont();
            fontGrande  = new BitmapFont();
            fontTitulo  = new BitmapFont();
            fontMediana.getData().setScale(1.5f);
            fontGrande.getData().setScale(2.5f);
            fontTitulo.getData().setScale(4f);
        }
    }
    
    private BitmapFont buildFont(FreeTypeFontGenerator gen, int size, Color color) {
        FreeTypeFontParameter p = new FreeTypeFontParameter();
        p.size = size;
        p.color  = color;
        p.borderWidth = 1f;
        p.borderColor = new Color(0f, 0f, 0f, 0.5f);
        
        p.characters  = FreeTypeFontParameter.DEFAULT_CHARS + "áéíóúÁÉÍÓÚñÑüÜ¿¡";
        p.magFilter   = Texture.TextureFilter.Linear;
        p.minFilter   = Texture.TextureFilter.Linear;
        return gen.generateFont(p);
    }
    
    private void inicializarSistemaDatos() {
        try {
            Files.createDirectories(Paths.get(Constantes.CARPETA_USUARIOS));
            Gdx.app.log("Main", "Sistema de datos listo en: "
                + Paths.get(Constantes.CARPETA_DATOS).toAbsolutePath());
        } catch (IOException e) {
            Gdx.app.error("Main", "ERROR CRÍTICO: no se pudo crear carpeta de datos", e);
        }
    }
    
    @Override
    public void render() {
        super.render();
    }
    
    @Override
    public void resize(int w, int h) {
        super.resize(w, h);
    }
    
    @Override
    public void dispose() {
        if (getScreen() != null) getScreen().dispose();
        batch.dispose();
        assets.dispose();
        pixel.dispose();
        
        if (fontPequena != null) fontPequena.dispose();
        if (fontMediana != null) fontMediana.dispose();
        if (fontGrande  != null) fontGrande.dispose();
        if (fontTitulo  != null) fontTitulo.dispose();
    }
    
    public void cambiarPantalla(Screen nueva) {
        Screen anterior = getScreen();
        setScreen(nueva);
        if (anterior != null) anterior.dispose();
    }
    
    public void dibujarRect(float x, float y, float w, float h, Color color) {
        batch.setColor(color);
        batch.draw(pixel, x, y, w, h);
        batch.setColor(Color.WHITE);
    }
    
    public void dibujarBorde(float x, float y, float w, float h, float grosor, Color color) {
        dibujarRect(x, y, w, grosor, color);
        dibujarRect(x, y + h - grosor, w, grosor, color);
        dibujarRect(x, y, grosor, h, color);
        dibujarRect(x + w - grosor, y, grosor, h, color);
    }
}
