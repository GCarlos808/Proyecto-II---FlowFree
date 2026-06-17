package io.proyecto2.flowfree.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import io.proyecto2.flowfree.Main;
import io.proyecto2.flowfree.constantes.Constantes;
import io.proyecto2.flowfree.constantes.Estilos;
import io.proyecto2.flowfree.usuario.Usuario;

public class PantallaNivelCompletado implements Screen {

    private final Main app;
    private final Usuario usuario;
    private final int nivelCompletado;
    private final int puntosNivel;
    private final int pasos;

    private static final float BTN_W = 220f;
    private static final float BTN_H = Estilos.ALTO_BOTON;

    public PantallaNivelCompletado(Main app, Usuario usuario, int nivelCompletado, int puntosNivel, int pasos) {
        this.app = app;
        this.usuario = usuario;
        this.nivelCompletado = nivelCompletado;
        this.puntosNivel = puntosNivel;
        this.pasos = pasos;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int sx, int sy, int p, int b) {
                float y = Gdx.graphics.getHeight() - sy;
                if (nivelCompletado < Constantes.NIVEL_MAX
                        && hit(sx, y, 290f, 170f, BTN_W, BTN_H)) {
                    app.cambiarPantalla(new PantallaJuego(app, usuario, nivelCompletado + 1));
                } else if (hit(sx, y, 80f, 170f, BTN_W, BTN_H)) {
                    app.cambiarPantalla(new PantallaMapa(app, usuario));
                } else if (hit(sx, y, 500f, 170f, BTN_W, BTN_H)) {
                    app.cambiarPantalla(new PantallaMenu(app, usuario));
                }
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
        GlyphLayout titulo = new GlyphLayout(app.fontTitulo, "¡NIVEL COMPLETADO!");
        app.fontTitulo.draw(app.batch, "¡NIVEL COMPLETADO!", (800f - titulo.width) / 2f, 470f);

        app.fontGrande.setColor(Estilos.COLOR_ACENTO_CYAN);
        String nivelTxt = "Nivel " + nivelCompletado;
        GlyphLayout gn = new GlyphLayout(app.fontGrande, nivelTxt);
        app.fontGrande.draw(app.batch, nivelTxt, (800f - gn.width) / 2f, 400f);

        float px = (800f - 460f) / 2f;
        app.dibujarRect(px, 300f, 460f, 80f, Estilos.COLOR_PANEL_CARD);
        app.dibujarBorde(px, 300f, 460f, 80f, 1f, Estilos.COLOR_ACENTO_VERDE);

        stat(px + 30f, 350f, "PUNTOS NIVEL", String.valueOf(puntosNivel));
        stat(px + 200f, 350f, "TOTAL", String.valueOf(usuario.getPuntuacion()));
        stat(px + 340f, 350f, "PASOS", String.valueOf(pasos));

        if (nivelCompletado < Constantes.NIVEL_MAX) {
            boton("SIGUIENTE NIVEL", 290f, 170f, true);
        }
        boton("MAPA", 80f, 170f, false);
        boton("MENÚ", 500f, 170f, false);

        app.batch.end();
    }

    private void stat(float x, float y, String lbl, String val) {
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, lbl, x, y);
        app.fontMediana.setColor(Estilos.COLOR_ACENTO_CYAN);
        app.fontMediana.draw(app.batch, val, x, y - 22f);
    }

    private void boton(String txt, float x, float y, boolean primario) {
        float mx = Gdx.input.getX(), my = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean hover = hit(mx, my, x, y, BTN_W, BTN_H);
        app.dibujarRect(x, y, BTN_W, BTN_H,
            hover ? Estilos.COLOR_HOVER : (primario ? Estilos.COLOR_PANEL_INPUT : Estilos.COLOR_PANEL_CARD));
        app.dibujarBorde(x, y, BTN_W, BTN_H, Estilos.GROSOR_BORDE,
            primario ? Estilos.COLOR_ACENTO_CYAN : Estilos.COLOR_BORDE_PANEL);
        app.fontMediana.setColor(primario ? Estilos.COLOR_ACENTO_CYAN : Estilos.COLOR_TEXTO_GRIS);
        GlyphLayout g = new GlyphLayout(app.fontMediana, txt);
        app.fontMediana.draw(app.batch, txt, x + (BTN_W - g.width) / 2f, y + BTN_H / 2f + g.height / 2f);
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
