package io.proyecto2.flowfree.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import io.proyecto2.flowfree.Main;
import io.proyecto2.flowfree.constantes.Estilos;
import io.proyecto2.flowfree.usuario.Usuario;

public class PantallaVictoria implements Screen {

    private final Main app;
    private final Usuario usuario;
    private static final float BTN_W = 280f;
    private static final float BTN_H = Estilos.ALTO_BOTON;
    private static final float BTN_X = (800f - BTN_W) / 2f;
    
    public PantallaVictoria(Main app, Usuario usuario) {
        this.app = app; this.usuario = usuario;
    }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int sx, int sy, int p, int b) {
                float wy = Gdx.graphics.getHeight() - sy;
                if (hit(sx, wy, BTN_X, 220f, BTN_W, BTN_H))
                    app.cambiarPantalla(new PantallaMenu(app, usuario));
                return true;
            }
        });
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(Estilos.COLOR_FONDO_OSCURO.r, Estilos.COLOR_FONDO_OSCURO.g, Estilos.COLOR_FONDO_OSCURO.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        app.batch.begin();
        
        app.fontTitulo.setColor(Estilos.COLOR_ACENTO_VERDE);
        GlyphLayout gv = new GlyphLayout(app.fontTitulo, "¡EXCELENTE!");
        app.fontTitulo.draw(app.batch, "¡EXCELENTE!", (800f - gv.width) / 2f, 480f);
        
        app.fontMediana.setColor(Estilos.COLOR_TEXTO_CLARO);
        String sub = "¡Felicitaciones, " + usuario.getNombreCompleto() + "!";
        GlyphLayout gs = new GlyphLayout(app.fontMediana, sub);
        app.fontMediana.draw(app.batch, sub, (800f - gs.width) / 2f, 410f);
        
        float px = (800f - 460f) / 2f;
        app.dibujarRect(px, 320f, 460f, 70f, Estilos.COLOR_PANEL_CARD);
        app.dibujarBorde(px, 320f, 460f, 70f, 1f, Estilos.COLOR_ACENTO_VERDE);
        
        stat(px + 20f,  360f, "PUNTOS",   String.valueOf(usuario.getPuntuacion()));
        stat(px + 150f, 360f, "PARTIDAS", String.valueOf(usuario.getEstadisticas().getPartidasJugadas()));
        stat(px + 300f, 360f, "TIEMPO",   usuario.getEstadisticas().getTiempoTotalFormateado());
        
        boton("IR AL MENÚ", BTN_X, 220f);
        
        app.batch.end();
    }
    
    private void stat(float x, float y, String lbl, String val) {
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, lbl, x, y);
        app.fontMediana.setColor(Estilos.COLOR_ACENTO_CYAN);
        app.fontMediana.draw(app.batch, val, x, y - 20f);
    }
    
    private void boton(String txt, float x, float y) {
        float mx = Gdx.input.getX(), my = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean hov = hit(mx, my, x, y, BTN_W, BTN_H);
        app.dibujarRect(x, y, BTN_W, BTN_H, hov ? Estilos.COLOR_ACENTO_CYAN_DIM : Estilos.COLOR_PANEL_INPUT);
        app.dibujarBorde(x, y, BTN_W, BTN_H, Estilos.GROSOR_BORDE, Estilos.COLOR_ACENTO_CYAN);
        app.fontMediana.setColor(Estilos.COLOR_ACENTO_CYAN);
        GlyphLayout gl = new GlyphLayout(app.fontMediana, txt);
        app.fontMediana.draw(app.batch, txt, x + (BTN_W - gl.width) / 2f, y + BTN_H / 2f + gl.height / 2f);
    }
    
    private boolean hit(float px, float py, float rx, float ry, float rw, float rh) {
        return px >= rx && px <= rx + rw && py >= ry && py <= ry + rh;
    }
    
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { Gdx.input.setInputProcessor(null); }
    @Override public void dispose() {}
}
