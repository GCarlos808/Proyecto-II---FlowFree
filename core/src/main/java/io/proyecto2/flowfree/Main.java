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

import io.proyecto2.flowfree.constantes.Constantes;
import io.proyecto2.flowfree.constantes.Estilos;
import io.proyecto2.flowfree.GUI.PantallaCarga;
import io.proyecto2.flowfree.util.GestorAvatares;
import io.proyecto2.flowfree.util.GestorMusica;
import io.proyecto2.flowfree.usuario.Usuario;

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

    private GestorMusica gestorMusica;
    
    @Override
    public void create() {
        inicializarRecursos();
        inicializarSistemaDatos();
        gestorMusica = new GestorMusica();
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
        
        fontPequena = new BitmapFont();
        fontMediana = new BitmapFont();
        fontGrande  = new BitmapFont();
        fontTitulo  = new BitmapFont();
        
        fontMediana.getData().setScale(1.5f);
        fontGrande.getData().setScale(2.5f);
        fontTitulo.getData().setScale(4f);
        
        fontGrande.setColor(Estilos.COLOR_ACENTO_CYAN);
        fontTitulo.setColor(Estilos.COLOR_ACENTO_CYAN);
        
        Gdx.app.log("Main", "Usando fuente por defecto (FreeType desactivado)");
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
    public void pause() {
        if (gestorMusica != null) gestorMusica.pausar();
    }

    @Override
    public void resume() {
        if (gestorMusica != null) gestorMusica.reanudar();
    }

    @Override
    public void dispose() {
        if (getScreen() != null) getScreen().dispose();
        if (gestorMusica != null) gestorMusica.dispose();
        batch.dispose();
        assets.dispose();
        pixel.dispose();

        if (fontPequena != null) fontPequena.dispose();
        if (fontMediana != null) fontMediana.dispose();
        if (fontGrande  != null) fontGrande.dispose();
        if (fontTitulo  != null) fontTitulo.dispose();
        GestorAvatares.dispose();
    }
    
    public void cambiarPantalla(Screen nueva) {
        Screen anterior = getScreen();
        setScreen(nueva);
        if (anterior != null) anterior.dispose();
    }

    public GestorMusica getGestorMusica() {
        return gestorMusica;
    }

    public void aplicarVolumenUsuario(Usuario usuario) {
        if (gestorMusica == null || usuario == null) return;
        gestorMusica.setVolumen(usuario.getPreferencias().getVolumen());
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
