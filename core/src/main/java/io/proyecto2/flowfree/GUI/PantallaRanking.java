package io.proyecto2.flowfree.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import io.proyecto2.flowfree.Main;
import io.proyecto2.flowfree.constantes.Estilos;
import io.proyecto2.flowfree.datos.GestorAmigos;
import io.proyecto2.flowfree.datos.GestorRanking;
import io.proyecto2.flowfree.usuario.Usuario;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class PantallaRanking implements Screen {

    private final Main app;
    private final Usuario usuario;

    private List<GestorRanking.EntradaRanking> ranking = Collections.emptyList();
    private String mensajeError = "";
    private boolean vistaAmigos = false;

    public PantallaRanking(Main app, Usuario usuario) {
        this.app = app;
        this.usuario = usuario;
    }

    @Override
    public void show() {
        cargarRanking();
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int sx, int sy, int p, int b) {
                float y = Gdx.graphics.getHeight() - sy;
                if (hit(sx, y, 30f, 20f, 120f, 36f)) {
                    app.cambiarPantalla(new PantallaMenu(app, usuario));
                    return true;
                }
                if (hit(sx, y, 520f, 510f, 120f, 32f)) {
                    vistaAmigos = false;
                    cargarRanking();
                    return true;
                }
                if (hit(sx, y, 650f, 510f, 120f, 32f)) {
                    vistaAmigos = true;
                    cargarRanking();
                    return true;
                }
                return true;
            }
        });
    }

    private void cargarRanking() {
        try {
            ranking = vistaAmigos ? GestorAmigos.rankingAmigos(usuario) : GestorRanking.leerTodos();
            mensajeError = "";
        } catch (IOException e) {
            ranking = Collections.emptyList();
            mensajeError = "No se pudo leer el ranking";
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(Estilos.COLOR_FONDO_OSCURO.r, Estilos.COLOR_FONDO_OSCURO.g, Estilos.COLOR_FONDO_OSCURO.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        app.batch.begin();

        app.fontGrande.setColor(Estilos.COLOR_ACENTO_CYAN);
        String tituloTxt = vistaAmigos ? "RANKING AMIGOS" : "RANKING GLOBAL";
        GlyphLayout title = new GlyphLayout(app.fontGrande, tituloTxt);
        app.fontGrande.draw(app.batch, tituloTxt, (800f - title.width) / 2f, 550f);

        dibujarToggleVista();

        dibujarTopRanking();
        dibujarComparacion();
        dibujarBotonVolver();

        if (!mensajeError.isEmpty()) {
            app.fontPequena.setColor(Estilos.COLOR_ACENTO_ROJO);
            GlyphLayout ge = new GlyphLayout(app.fontPequena, mensajeError);
            app.fontPequena.draw(app.batch, mensajeError, (800f - ge.width) / 2f, 90f);
        }

        app.batch.end();
    }

    private void dibujarTopRanking() {
        app.dibujarRect(40f, 170f, 460f, 330f, Estilos.COLOR_PANEL_CARD);
        app.dibujarBorde(40f, 170f, 460f, 330f, 1f, Estilos.COLOR_BORDE_PANEL);

        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, vistaAmigos ? "TOP AMIGOS" : "TOP JUGADORES", 60f, 485f);
        app.fontPequena.draw(app.batch, "POS", 60f, 460f);
        app.fontPequena.draw(app.batch, "USUARIO", 120f, 460f);
        app.fontPequena.draw(app.batch, "PTS", 320f, 460f);
        app.fontPequena.draw(app.batch, "NIVEL", 390f, 460f);

        int limite = Math.min(8, ranking.size());
        if (limite == 0) {
            app.fontPequena.setColor(Estilos.COLOR_TEXTO_DISABLED);
            String vacio = vistaAmigos
                ? "Agrega amigos para competir en este ranking."
                : "Aun no hay partidas registradas.";
            app.fontPequena.draw(app.batch, vacio, 60f, 430f);
            return;
        }

        for (int i = 0; i < limite; i++) {
            GestorRanking.EntradaRanking e = ranking.get(i);
            float y = 430f - (i * 32f);
            boolean soyYo = e.nombre().equalsIgnoreCase(usuario.getNombreUsuario());

            app.fontPequena.setColor(soyYo ? Estilos.COLOR_ACENTO_CYAN : Estilos.COLOR_TEXTO_CLARO);
            app.fontPequena.draw(app.batch, "#" + (i + 1), 60f, y);
            app.fontPequena.draw(app.batch, e.nombre(), 120f, y);
            app.fontPequena.draw(app.batch, String.valueOf(e.puntuacion()), 320f, y);
            app.fontPequena.draw(app.batch, String.valueOf(e.nivelAlcanzado()), 400f, y);
        }
    }

    private void dibujarComparacion() {
        app.dibujarRect(520f, 170f, 240f, 330f, Estilos.COLOR_PANEL_CARD);
        app.dibujarBorde(520f, 170f, 240f, 330f, 1f, Estilos.COLOR_BORDE_PANEL);

        int pos = buscarPosicionUsuario();
        GestorRanking.EntradaRanking yo = buscarEntradaUsuario();
        GestorRanking.EntradaRanking rival = pos > 1 ? ranking.get(pos - 2) : null;

        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, "TU PROGRESO", 540f, 485f);

        app.fontPequena.setColor(Estilos.COLOR_TEXTO_CLARO);
        app.fontPequena.draw(app.batch, "Usuario: " + usuario.getNombreUsuario(), 540f, 455f);
        app.fontPequena.draw(app.batch, "Posicion: " + (pos > 0 ? "#" + pos : "Sin posicion"), 540f, 430f);
        app.fontPequena.draw(app.batch, "Puntuacion: " + usuario.getPuntuacion(), 540f, 405f);
        app.fontPequena.draw(app.batch, "Nivel: " + usuario.getNivelActual(), 540f, 380f);
        app.fontPequena.draw(app.batch, "Partidas: " + usuario.getEstadisticas().getPartidasJugadas(), 540f, 355f);
        app.fontPequena.draw(app.batch, "Tiempo total: " + usuario.getEstadisticas().getTiempoTotalFormateado(), 540f, 330f);

        if (yo != null && rival != null) {
            int faltan = Math.max(0, rival.puntuacion() - yo.puntuacion());
            app.fontPequena.setColor(Estilos.COLOR_ACENTO_AMARILLO);
            app.fontPequena.draw(app.batch, "Para subir a #" + (pos - 1) + " faltan:", 540f, 285f);
            app.fontMediana.setColor(Estilos.COLOR_ACENTO_CYAN);
            app.fontMediana.draw(app.batch, faltan + " pts", 540f, 258f);
        } else if (pos == 1) {
            app.fontPequena.setColor(Estilos.COLOR_ACENTO_VERDE);
            app.fontPequena.draw(app.batch, "Eres el #1 del ranking", 540f, 285f);
        }
    }

    private GestorRanking.EntradaRanking buscarEntradaUsuario() {
        String actual = usuario.getNombreUsuario();
        for (GestorRanking.EntradaRanking e : ranking) {
            if (e.nombre().equalsIgnoreCase(actual)) return e;
        }
        return null;
    }

    private int buscarPosicionUsuario() {
        String actual = usuario.getNombreUsuario();
        for (int i = 0; i < ranking.size(); i++) {
            if (ranking.get(i).nombre().equalsIgnoreCase(actual)) return i + 1;
        }
        return -1;
    }

    private void dibujarToggleVista() {
        dibujarTabRanking("GLOBAL", 520f, !vistaAmigos);
        dibujarTabRanking("AMIGOS", 650f, vistaAmigos);
    }

    private void dibujarTabRanking(String txt, float x, boolean activo) {
        app.dibujarRect(x, 510f, 120f, 32f, activo ? Estilos.COLOR_PANEL_INPUT : Estilos.COLOR_PANEL_CARD);
        app.dibujarBorde(x, 510f, 120f, 32f, 1f, activo ? Estilos.COLOR_ACENTO_CYAN : Estilos.COLOR_BORDE_PANEL);
        app.fontPequena.setColor(activo ? Estilos.COLOR_ACENTO_CYAN : Estilos.COLOR_TEXTO_GRIS);
        GlyphLayout g = new GlyphLayout(app.fontPequena, txt);
        app.fontPequena.draw(app.batch, txt, x + (120f - g.width) / 2f, 532f);
    }

    private void dibujarBotonVolver() {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean hover = hit(mx, my, 30f, 20f, 120f, 36f);
        app.dibujarRect(30f, 20f, 120f, 36f, hover ? Estilos.COLOR_HOVER : Estilos.COLOR_PANEL_CARD);
        app.dibujarBorde(30f, 20f, 120f, 36f, 1f, Estilos.COLOR_BORDE_PANEL);
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, "< VOLVER", 45f, 44f);
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
