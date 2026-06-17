package io.proyecto2.flowfree.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import io.proyecto2.flowfree.Main;
import io.proyecto2.flowfree.constantes.Estilos;

public class PantallaCarga implements Screen {

    private final Main app;
    private float progresoVisual = 0f;
    private boolean listo = false;
    
    private static final float BARRA_W = 400f;
    private static final float BARRA_H = 10f;
    
    public PantallaCarga(Main app) { this.app = app; }
    
    @Override
    public void show() {
        Gdx.app.log("PantallaCarga", "Cargando recursos...");
        if (app.getGestorMusica() != null) {
            app.getGestorMusica().cargar();
        }
    }
    
    @Override
    public void render(float delta) {
        float progreso = app.assets.isFinished() ? 1f : app.assets.getProgress();
        if (!app.assets.update()) {
            app.assets.getProgress();
        } else if (!listo) {
            listo = true;
        }
        
        progresoVisual += (progreso - progresoVisual) * 0.08f;
        
        Gdx.gl.glClearColor(
            Estilos.COLOR_FONDO_OSCURO.r,
            Estilos.COLOR_FONDO_OSCURO.g,
            Estilos.COLOR_FONDO_OSCURO.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        app.batch.begin();
        
        app.fontTitulo.setColor(Estilos.COLOR_ACENTO_CYAN);
        GlyphLayout gl = new GlyphLayout(app.fontTitulo, "FLOW FREE");
        app.fontTitulo.draw(app.batch, "FLOW FREE", (800f - gl.width) / 2f, 370f);
        
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        GlyphLayout gs = new GlyphLayout(app.fontPequena, "Cargando...");
        app.fontPequena.draw(app.batch, "Cargando...", (800f - gs.width) / 2f, 230f);
        
        float bx = (800f - BARRA_W) / 2f;
        float by = 210f;
        app.dibujarRect(bx, by, BARRA_W, BARRA_H, Estilos.COLOR_PANEL_CARD);
        app.dibujarBorde(bx - 1, by - 1, BARRA_W + 2, BARRA_H + 2, 1f, Estilos.COLOR_BORDE_PANEL);
        
        if (progresoVisual > 0.01f)
            app.dibujarRect(bx, by, BARRA_W * progresoVisual, BARRA_H,
                Estilos.COLOR_ACENTO_CYAN);

        app.batch.end();
        
        if (listo && progresoVisual >= 0.99f) {
            if (app.getGestorMusica() != null) {
                app.getGestorMusica().reproducir();
            }
            app.cambiarPantalla(new PantallaLogin(app));
        }
    }
    
    @Override public void resize(int w, int h) {}
    @Override public void pause()   {}
    @Override public void resume()  {}
    @Override public void hide()    {}
    @Override public void dispose() {}
}
