package io.proyecto2.flowfree.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import io.proyecto2.flowfree.Main;
import io.proyecto2.flowfree.constantes.Estilos;
import io.proyecto2.flowfree.datos.Desafio;
import io.proyecto2.flowfree.datos.GestorAmigos;
import io.proyecto2.flowfree.datos.GestorRanking;
import io.proyecto2.flowfree.datos.exceptions.UsuarioNoEncontradoException;
import io.proyecto2.flowfree.usuario.Usuario;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class PantallaDesafio implements Screen {

    private final Main app;
    private final Usuario usuario;
    private final String rival;

    private GestorAmigos.ResumenJugador yo;
    private GestorAmigos.ResumenJugador otro;
    private List<GestorRanking.EntradaRanking> rankingAmigos = Collections.emptyList();
    private Desafio desafioActivo;
    private String mensaje = "";

    public PantallaDesafio(Main app, Usuario usuario, String rival) {
        this.app = app;
        this.usuario = usuario;
        this.rival = rival;
    }

    @Override
    public void show() {
        cargarDatos();
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int key) {
                if (key == Input.Keys.ESCAPE) {
                    app.cambiarPantalla(new PantallaAmigos(app, usuario));
                }
                return true;
            }

            @Override
            public boolean touchDown(int sx, int sy, int p, int b) {
                manejarClic(sx, Gdx.graphics.getHeight() - sy);
                return true;
            }
        });
    }

    private void cargarDatos() {
        try {
            yo = GestorAmigos.resumenJugador(usuario.getNombreUsuario());
            otro = GestorAmigos.resumenJugador(rival);
            rankingAmigos = GestorAmigos.rankingAmigos(usuario);
            desafioActivo = GestorAmigos.desafiosEntrada(usuario.getNombreUsuario()).stream()
                .filter(d -> d.retador().equalsIgnoreCase(rival))
                .findFirst()
                .orElse(null);
        } catch (IOException | UsuarioNoEncontradoException e) {
            mensaje = "No se pudo cargar el perfil del rival.";
        }
    }

    private void manejarClic(float x, float y) {
        if (hit(x, y, 30f, 20f, 120f, 36f)) {
            app.cambiarPantalla(new PantallaAmigos(app, usuario));
            return;
        }
        if (hit(x, y, 280f, 120f, 240f, 44f)) {
            try {
                GestorAmigos.enviarDesafio(usuario, rival, usuario.getNivelActual());
                mostrarMensaje("Desafío enviado a " + rival);
            } catch (Exception e) {
                mostrarMensaje(e.getMessage());
            }
            return;
        }
        if (desafioActivo != null && hit(x, y, 540f, 120f, 240f, 44f)) {
            try {
                GestorAmigos.descartarDesafio(usuario, rival);
                desafioActivo = null;
                mostrarMensaje("Desafío descartado.");
            } catch (IOException e) {
                mostrarMensaje("No se pudo descartar el desafío.");
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(Estilos.COLOR_FONDO_OSCURO.r, Estilos.COLOR_FONDO_OSCURO.g, Estilos.COLOR_FONDO_OSCURO.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        app.batch.begin();

        app.fontGrande.setColor(Estilos.COLOR_ACENTO_CYAN);
        GlyphLayout titulo = new GlyphLayout(app.fontGrande, "COMPARAR");
        app.fontGrande.draw(app.batch, "COMPARAR", (800f - titulo.width) / 2f, 550f);

        if (yo != null && otro != null) {
            dibujarTarjeta(yo, 60f, 320f, true);
            dibujarTarjeta(otro, 420f, 320f, false);
            dibujarComparativa();
            dibujarDesafio();
            dibujarRankingAmigos();
        }

        if (!mensaje.isEmpty()) {
            app.fontPequena.setColor(Estilos.COLOR_ACENTO_AMARILLO);
            GlyphLayout g = new GlyphLayout(app.fontPequena, mensaje);
            app.fontPequena.draw(app.batch, mensaje, (800f - g.width) / 2f, 70f);
        }

        dibujarBotonVolver();
        app.batch.end();
    }

    private void dibujarTarjeta(GestorAmigos.ResumenJugador j, float x, float y, boolean esYo) {
        app.dibujarRect(x, y, 320f, 200f, Estilos.COLOR_PANEL_CARD);
        app.dibujarBorde(x, y, 320f, 200f, 1f, esYo ? Estilos.COLOR_ACENTO_CYAN : Estilos.COLOR_BORDE_PANEL);

        app.fontMediana.setColor(esYo ? Estilos.COLOR_ACENTO_CYAN : Estilos.COLOR_TEXTO_CLARO);
        app.fontMediana.draw(app.batch, esYo ? "TÚ" : j.nombreUsuario(), x + 20f, y + 175f);
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, j.nombreCompleto(), x + 20f, y + 150f);

        float ly = y + 120f;
        lineaStat(x + 20f, ly, "Puntuación", String.valueOf(j.puntuacion())); ly -= 28f;
        lineaStat(x + 20f, ly, "Nivel", String.valueOf(j.nivel())); ly -= 28f;
        lineaStat(x + 20f, ly, "Partidas", String.valueOf(j.partidas())); ly -= 28f;
        lineaStat(x + 20f, ly, "Mejor racha", String.valueOf(j.rachaMejor())); ly -= 28f;
        lineaStat(x + 20f, ly, "Tiempo total", j.tiempoFormateado());
    }

    private void lineaStat(float x, float y, String lbl, String val) {
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, lbl + ":", x, y);
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_CLARO);
        app.fontPequena.draw(app.batch, val, x + 130f, y);
    }

    private void dibujarComparativa() {
        if (yo == null || otro == null) return;

        app.dibujarRect(60f, 230f, 680f, 70f, Estilos.COLOR_PANEL_INPUT);
        app.dibujarBorde(60f, 230f, 680f, 70f, 1f, Estilos.COLOR_BORDE_PANEL);

        int diffPts = yo.puntuacion() - otro.puntuacion();
        int diffNivel = yo.nivel() - otro.nivel();

        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, "RESULTADO", 80f, 285f);

        if (diffPts > 0) {
            app.fontMediana.setColor(Estilos.COLOR_ACENTO_VERDE);
            app.fontMediana.draw(app.batch, "Vas ganando por " + diffPts + " pts", 80f, 255f);
        } else if (diffPts < 0) {
            app.fontMediana.setColor(Estilos.COLOR_ACENTO_ROJO);
            app.fontMediana.draw(app.batch, "Vas perdiendo por " + (-diffPts) + " pts", 80f, 255f);
        } else {
            app.fontMediana.setColor(Estilos.COLOR_ACENTO_AMARILLO);
            app.fontMediana.draw(app.batch, "Empate en puntuación", 80f, 255f);
        }

        String nivelTxt = diffNivel > 0 ? "Tú vas " + diffNivel + " nivel(es) por delante"
            : diffNivel < 0 ? "Tu rival va " + (-diffNivel) + " nivel(es) por delante"
            : "Mismo nivel alcanzado";
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_CLARO);
        app.fontPequena.draw(app.batch, nivelTxt, 420f, 255f);
    }

    private void dibujarDesafio() {
        if (desafioActivo != null) {
            app.dibujarRect(60f, 175f, 680f, 42f, Estilos.COLOR_PANEL_CARD);
            app.fontPequena.setColor(Estilos.COLOR_ACENTO_AMARILLO);
            app.fontPequena.draw(app.batch,
                rival + " te desafió en el nivel " + desafioActivo.nivel()
                    + " con " + desafioActivo.puntosRetador() + " pts.",
                80f, 200f);
        }

        dibujarBotonAccion("ENVIAR DESAFÍO", 280f, 120f, Estilos.COLOR_ACENTO_CYAN);
        if (desafioActivo != null) {
            dibujarBotonAccion("DESCARTAR DESAFÍO", 540f, 120f, Estilos.COLOR_TEXTO_GRIS);
        }
    }

    private void dibujarRankingAmigos() {
        app.dibujarRect(60f, 20f, 680f, 85f, Estilos.COLOR_PANEL_CARD);
        app.dibujarBorde(60f, 20f, 680f, 85f, 1f, Estilos.COLOR_BORDE_PANEL);
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, "RANKING DE AMIGOS", 80f, 88f);

        float x = 80f;
        int pos = 1;
        for (GestorRanking.EntradaRanking e : rankingAmigos) {
            if (pos > 4) break;
            boolean soyYo = e.nombre().equalsIgnoreCase(usuario.getNombreUsuario());
            boolean esRival = e.nombre().equalsIgnoreCase(rival);
            app.fontPequena.setColor(soyYo ? Estilos.COLOR_ACENTO_CYAN
                : esRival ? Estilos.COLOR_ACENTO_AMARILLO : Estilos.COLOR_TEXTO_CLARO);
            app.fontPequena.draw(app.batch,
                "#" + pos + " " + e.nombre() + " (" + e.puntuacion() + " pts)",
                x, 55f);
            x += 170f;
            pos++;
        }
    }

    private void dibujarBotonAccion(String txt, float x, float y, com.badlogic.gdx.graphics.Color color) {
        float mx = Gdx.input.getX(), my = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean hover = hit(mx, my, x, y, 240f, 44f);
        app.dibujarRect(x, y, 240f, 44f, hover ? Estilos.COLOR_HOVER : Estilos.COLOR_PANEL_INPUT);
        app.dibujarBorde(x, y, 240f, 44f, 1f, color);
        app.fontPequena.setColor(color);
        GlyphLayout g = new GlyphLayout(app.fontPequena, txt);
        app.fontPequena.draw(app.batch, txt, x + (240f - g.width) / 2f, y + 28f);
    }

    private void dibujarBotonVolver() {
        float mx = Gdx.input.getX(), my = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean hover = hit(mx, my, 30f, 520f, 120f, 36f);
        app.dibujarRect(30f, 520f, 120f, 36f, hover ? Estilos.COLOR_HOVER : Estilos.COLOR_PANEL_CARD);
        app.dibujarBorde(30f, 520f, 120f, 36f, 1f, Estilos.COLOR_BORDE_PANEL);
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, "< AMIGOS", 45f, 544f);
    }

    private void mostrarMensaje(String msg) { mensaje = msg; }

    private boolean hit(float px, float py, float rx, float ry, float rw, float rh) {
        return px >= rx && px <= rx + rw && py >= ry && py <= ry + rh;
    }

    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { Gdx.input.setInputProcessor(null); }
    @Override public void dispose() {}
}
