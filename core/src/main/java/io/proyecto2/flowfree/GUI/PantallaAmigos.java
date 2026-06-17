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
import io.proyecto2.flowfree.datos.SolicitudAmistad;
import io.proyecto2.flowfree.datos.exceptions.AmistadException;
import io.proyecto2.flowfree.usuario.Usuario;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class PantallaAmigos implements Screen {

    private enum Tab { AMIGOS, SOLICITUDES, AGREGAR }

    private final Main app;
    private final Usuario usuario;

    private Tab tab = Tab.AMIGOS;
    private List<String> amigos = Collections.emptyList();
    private List<SolicitudAmistad> entrada = Collections.emptyList();
    private List<SolicitudAmistad> enviadas = Collections.emptyList();
    private List<Desafio> desafios = Collections.emptyList();

    private final StringBuilder campoUsuario = new StringBuilder();
    private boolean campoActivo = false;
    private String mensaje = "";
    private float mensajeT = 0f;

    private static final float TAB_Y = 500f;
    private static final float TAB_W = 150f;
    private static final float TAB_H = 36f;
    private static final float TAB_X0 = (800f - 3 * TAB_W - 20f) / 2f;
    private static final float LIST_Y = 140f;
    private static final float LIST_H = 350f;

    public PantallaAmigos(Main app, Usuario usuario) {
        this.app = app;
        this.usuario = usuario;
    }

    @Override
    public void show() {
        recargar();
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int key) {
                if (campoActivo && key == Input.Keys.BACKSPACE) {
                    if (campoUsuario.length() > 0) campoUsuario.deleteCharAt(campoUsuario.length() - 1);
                    return true;
                }
                if (key == Input.Keys.ESCAPE) {
                    app.cambiarPantalla(new PantallaMenu(app, usuario));
                    return true;
                }
                return true;
            }

            @Override
            public boolean keyTyped(char c) {
                if (!campoActivo || c < 32 || c == 127) return false;
                if (campoUsuario.length() < 20) campoUsuario.append(c);
                return true;
            }

            @Override
            public boolean touchDown(int sx, int sy, int p, int b) {
                manejarClic(sx, Gdx.graphics.getHeight() - sy);
                return true;
            }
        });
    }

    private void recargar() {
        try {
            amigos = List.copyOf(usuario.getAmigos().getAmigos());
            entrada = GestorAmigos.solicitudesEntrada(usuario.getNombreUsuario());
            enviadas = GestorAmigos.solicitudesEnviadas(usuario.getNombreUsuario());
            desafios = GestorAmigos.desafiosEntrada(usuario.getNombreUsuario());
        } catch (IOException e) {
            mostrarError("No se pudieron cargar los datos sociales.");
        }
    }

    private void manejarClic(float x, float y) {
        if (hit(x, y, 30f, 20f, 120f, 36f)) {
            app.cambiarPantalla(new PantallaMenu(app, usuario));
            return;
        }

        for (int i = 0; i < 3; i++) {
            float tx = TAB_X0 + i * (TAB_W + 10f);
            if (hit(x, y, tx, TAB_Y, TAB_W, TAB_H)) {
                tab = Tab.values()[i];
                campoActivo = tab == Tab.AGREGAR;
                return;
            }
        }

        switch (tab) {
            case AMIGOS -> manejarClicAmigos(x, y);
            case SOLICITUDES -> manejarClicSolicitudes(x, y);
            case AGREGAR -> manejarClicAgregar(x, y);
        }
    }

    private void manejarClicAmigos(float x, float y) {
        float rowY = LIST_Y + LIST_H - 40f;
        for (String amigo : amigos) {
            if (hit(x, y, 520f, rowY - 8f, 100f, 34f)) {
                app.cambiarPantalla(new PantallaDesafio(app, usuario, amigo));
                return;
            }
            if (hit(x, y, 630f, rowY - 8f, 85f, 34f)) {
                try {
                    GestorAmigos.enviarDesafio(usuario, amigo, usuario.getNivelActual());
                    mostrarExito("Desafío enviado a " + amigo);
                } catch (AmistadException | IOException e) {
                    mostrarError(e.getMessage());
                }
                return;
            }
            if (hit(x, y, 720f, rowY - 8f, 40f, 34f)) {
                try {
                    GestorAmigos.eliminarAmigo(usuario, amigo);
                    recargar();
                    mostrarExito("Amigo eliminado.");
                } catch (AmistadException | IOException e) {
                    mostrarError(e.getMessage());
                }
                return;
            }
            rowY -= 42f;
        }
    }

    private void manejarClicSolicitudes(float x, float y) {
        float rowY = LIST_Y + LIST_H - 50f;
        for (SolicitudAmistad s : entrada) {
            if (hit(x, y, 420f, rowY - 8f, 80f, 30f)) {
                try {
                    GestorAmigos.aceptarSolicitud(usuario, s.remitente());
                    recargar();
                    mostrarExito("Ahora sois amigos con " + s.remitente());
                } catch (AmistadException | IOException e) {
                    mostrarError(e.getMessage());
                }
                return;
            }
            if (hit(x, y, 510f, rowY - 8f, 80f, 30f)) {
                try {
                    GestorAmigos.rechazarSolicitud(usuario, s.remitente());
                    recargar();
                    mostrarExito("Solicitud rechazada.");
                } catch (IOException e) {
                    mostrarError(e.getMessage());
                }
                return;
            }
            rowY -= 38f;
        }

        rowY = LIST_Y + 120f;
        for (SolicitudAmistad s : enviadas) {
            if (hit(x, y, 580f, rowY - 8f, 90f, 30f)) {
                try {
                    GestorAmigos.cancelarSolicitudEnviada(usuario, s.remitente());
                    recargar();
                    mostrarExito("Solicitud cancelada.");
                } catch (IOException e) {
                    mostrarError(e.getMessage());
                }
                return;
            }
            rowY -= 34f;
        }
    }

    private void manejarClicAgregar(float x, float y) {
        campoActivo = hit(x, y, 200f, 360f, 400f, Estilos.ALTO_INPUT);
        if (hit(x, y, 250f, 290f, 300f, Estilos.ALTO_BOTON)) {
            try {
                GestorAmigos.enviarSolicitud(usuario, campoUsuario.toString());
                campoUsuario.setLength(0);
                recargar();
                tab = Tab.SOLICITUDES;
                mostrarExito("Solicitud enviada.");
            } catch (AmistadException | IOException e) {
                mostrarError(e.getMessage());
            }
        }
    }

    @Override
    public void render(float delta) {
        if (mensajeT > 0f) {
            mensajeT -= delta;
            if (mensajeT <= 0f) mensaje = "";
        }

        Gdx.gl.glClearColor(Estilos.COLOR_FONDO_OSCURO.r, Estilos.COLOR_FONDO_OSCURO.g, Estilos.COLOR_FONDO_OSCURO.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        app.batch.begin();
        dibujarCabecera();
        dibujarTabs();
        dibujarContenido();
        dibujarBotonVolver();
        dibujarMensaje();
        app.batch.end();
    }

    private void dibujarCabecera() {
        app.fontGrande.setColor(Estilos.COLOR_ACENTO_CYAN);
        GlyphLayout t = new GlyphLayout(app.fontGrande, "AMIGOS");
        app.fontGrande.draw(app.batch, "AMIGOS", (800f - t.width) / 2f, 560f);

        if (!desafios.isEmpty()) {
            app.fontPequena.setColor(Estilos.COLOR_ACENTO_AMARILLO);
            app.fontPequena.draw(app.batch,
                desafios.size() + " desafío(s) pendiente(s) — abre un amigo para comparar",
                60f, 475f);
        }
    }

    private void dibujarTabs() {
        String[] labels = {"AMIGOS", "SOLICITUDES", "AGREGAR"};
        for (int i = 0; i < labels.length; i++) {
            Tab actual = Tab.values()[i];
            float tx = TAB_X0 + i * (TAB_W + 10f);
            boolean activo = tab == actual;
            app.dibujarRect(tx, TAB_Y, TAB_W, TAB_H, activo ? Estilos.COLOR_PANEL_INPUT : Estilos.COLOR_PANEL_CARD);
            app.dibujarBorde(tx, TAB_Y, TAB_W, TAB_H, 1f, activo ? Estilos.COLOR_ACENTO_CYAN : Estilos.COLOR_BORDE_PANEL);
            app.fontPequena.setColor(activo ? Estilos.COLOR_ACENTO_CYAN : Estilos.COLOR_TEXTO_GRIS);
            GlyphLayout g = new GlyphLayout(app.fontPequena, labels[i]);
            app.fontPequena.draw(app.batch, labels[i], tx + (TAB_W - g.width) / 2f, TAB_Y + 24f);
            if (actual == Tab.SOLICITUDES && !entrada.isEmpty()) {
                app.fontPequena.setColor(Estilos.COLOR_ACENTO_ROJO);
                app.fontPequena.draw(app.batch, "(" + entrada.size() + ")", tx + TAB_W - 28f, TAB_Y + TAB_H - 6f);
            }
        }
    }

    private void dibujarContenido() {
        app.dibujarRect(40f, LIST_Y, 720f, LIST_H, Estilos.COLOR_PANEL_CARD);
        app.dibujarBorde(40f, LIST_Y, 720f, LIST_H, 1f, Estilos.COLOR_BORDE_PANEL);
        switch (tab) {
            case AMIGOS -> dibujarListaAmigos();
            case SOLICITUDES -> dibujarSolicitudes();
            case AGREGAR -> dibujarAgregar();
        }
    }

    private void dibujarListaAmigos() {
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, "USUARIO", 60f, LIST_Y + LIST_H - 18f);
        app.fontPequena.draw(app.batch, "PTS", 360f, LIST_Y + LIST_H - 18f);
        app.fontPequena.draw(app.batch, "NIVEL", 430f, LIST_Y + LIST_H - 18f);
        app.fontPequena.draw(app.batch, "ACCIONES", 580f, LIST_Y + LIST_H - 18f);

        if (amigos.isEmpty()) {
            app.fontPequena.setColor(Estilos.COLOR_TEXTO_DISABLED);
            app.fontPequena.draw(app.batch, "Aún no tienes amigos. Usa AGREGAR para enviar solicitudes.", 60f, LIST_Y + LIST_H - 55f);
            return;
        }

        float rowY = LIST_Y + LIST_H - 48f;
        int mostrados = 0;
        for (String amigo : amigos) {
            if (mostrados >= 7) break;
            dibujarFilaAmigo(amigo, rowY);
            rowY -= 42f;
            mostrados++;
        }
    }

    private void dibujarFilaAmigo(String amigo, float rowY) {
        GestorRanking.EntradaRanking stats = null;
        try {
            stats = GestorRanking.leerUsuario(amigo);
        } catch (IOException ignored) {}

        app.fontPequena.setColor(Estilos.COLOR_TEXTO_CLARO);
        app.fontPequena.draw(app.batch, amigo, 60f, rowY);
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, stats != null ? String.valueOf(stats.puntuacion()) : "-", 360f, rowY);
        app.fontPequena.draw(app.batch, stats != null ? String.valueOf(stats.nivelAlcanzado()) : "-", 430f, rowY);

        dibujarMiniBoton("COMPARAR", 520f, rowY - 8f, 100f, Estilos.COLOR_ACENTO_CYAN);
        dibujarMiniBoton("DESAFIAR", 630f, rowY - 8f, 85f, Estilos.COLOR_ACENTO_AMARILLO);
        dibujarMiniBoton("X", 720f, rowY - 8f, 40f, Estilos.COLOR_TEXTO_GRIS);
    }

    private void dibujarSolicitudes() {
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, "RECIBIDAS", 60f, LIST_Y + LIST_H - 18f);

        if (entrada.isEmpty()) {
            app.fontPequena.setColor(Estilos.COLOR_TEXTO_DISABLED);
            app.fontPequena.draw(app.batch, "No tienes solicitudes pendientes.", 60f, LIST_Y + LIST_H - 50f);
        } else {
            float rowY = LIST_Y + LIST_H - 48f;
            for (SolicitudAmistad s : entrada) {
                app.fontPequena.setColor(Estilos.COLOR_TEXTO_CLARO);
                app.fontPequena.draw(app.batch, s.remitente(), 60f, rowY);
                dibujarMiniBoton("ACEPTAR", 420f, rowY - 8f, 80f, Estilos.COLOR_ACENTO_VERDE);
                dibujarMiniBoton("RECHAZAR", 510f, rowY - 8f, 80f, Estilos.COLOR_ACENTO_ROJO);
                rowY -= 38f;
            }
        }

        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, "ENVIADAS", 60f, LIST_Y + 140f);
        if (enviadas.isEmpty()) {
            app.fontPequena.setColor(Estilos.COLOR_TEXTO_DISABLED);
            app.fontPequena.draw(app.batch, "No has enviado solicitudes.", 60f, LIST_Y + 115f);
        } else {
            float rowY = LIST_Y + 108f;
            for (SolicitudAmistad s : enviadas) {
                app.fontPequena.setColor(Estilos.COLOR_TEXTO_CLARO);
                app.fontPequena.draw(app.batch, "Para: " + s.remitente(), 60f, rowY);
                dibujarMiniBoton("CANCELAR", 580f, rowY - 8f, 90f, Estilos.COLOR_TEXTO_GRIS);
                rowY -= 34f;
            }
        }
    }

    private void dibujarAgregar() {
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, "Nombre de usuario del jugador", 200f, 418f);

        float inputY = 360f;
        app.dibujarRect(200f, inputY, 400f, Estilos.ALTO_INPUT, Estilos.COLOR_PANEL_INPUT);
        app.dibujarBorde(200f, inputY, 400f, Estilos.ALTO_INPUT, Estilos.GROSOR_BORDE,
            campoActivo ? Estilos.COLOR_BORDE_ACTIVO : Estilos.COLOR_BORDE_PANEL);

        String txt = campoUsuario.toString();
        if (campoActivo && (System.currentTimeMillis() / 500) % 2 == 0) txt += "|";
        if (!txt.isEmpty()) {
            app.fontPequena.setColor(Estilos.COLOR_TEXTO_CLARO);
            app.fontPequena.draw(app.batch, txt, 212f, inputY + Estilos.ALTO_INPUT / 2f + 6f);
        }

        app.fontPequena.setColor(Estilos.COLOR_TEXTO_DISABLED);
        app.fontPequena.draw(app.batch, "Los jugadores deben estar registrados en este equipo.", 200f, 330f);

        dibujarBotonGrande("ENVIAR SOLICITUD", 250f, 290f);
    }

    private void dibujarBotonGrande(String txt, float x, float y) {
        float mx = Gdx.input.getX(), my = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean hover = hit(mx, my, x, y, 300f, Estilos.ALTO_BOTON);
        app.dibujarRect(x, y, 300f, Estilos.ALTO_BOTON, hover ? Estilos.COLOR_ACENTO_CYAN_DIM : Estilos.COLOR_PANEL_INPUT);
        app.dibujarBorde(x, y, 300f, Estilos.ALTO_BOTON, 1f, Estilos.COLOR_ACENTO_CYAN);
        app.fontMediana.setColor(Estilos.COLOR_ACENTO_CYAN);
        GlyphLayout g = new GlyphLayout(app.fontMediana, txt);
        app.fontMediana.draw(app.batch, txt, x + (300f - g.width) / 2f, y + 30f);
    }

    private void dibujarMiniBoton(String txt, float x, float y, float w, com.badlogic.gdx.graphics.Color color) {
        app.dibujarRect(x, y, w, 30f, Estilos.COLOR_PANEL_INPUT);
        app.dibujarBorde(x, y, w, 30f, 1f, Estilos.COLOR_BORDE_PANEL);
        app.fontPequena.setColor(color);
        GlyphLayout g = new GlyphLayout(app.fontPequena, txt);
        app.fontPequena.draw(app.batch, txt, x + (w - g.width) / 2f, y + 20f);
    }

    private void dibujarBotonVolver() {
        float mx = Gdx.input.getX(), my = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean hover = hit(mx, my, 30f, 20f, 120f, 36f);
        app.dibujarRect(30f, 20f, 120f, 36f, hover ? Estilos.COLOR_HOVER : Estilos.COLOR_PANEL_CARD);
        app.dibujarBorde(30f, 20f, 120f, 36f, 1f, Estilos.COLOR_BORDE_PANEL);
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, "< VOLVER", 45f, 44f);
    }

    private void dibujarMensaje() {
        if (mensaje.isEmpty()) return;
        boolean error = mensaje.startsWith("No") || mensaje.contains("no") || mensaje.contains("No");
        app.fontPequena.setColor(error ? Estilos.COLOR_ACENTO_ROJO : Estilos.COLOR_ACENTO_VERDE);
        GlyphLayout g = new GlyphLayout(app.fontPequena, mensaje);
        app.fontPequena.draw(app.batch, mensaje, (800f - g.width) / 2f, 100f);
    }

    private void mostrarError(String msg) { mensaje = msg; mensajeT = 4f; }
    private void mostrarExito(String msg) { mensaje = msg; mensajeT = 3f; }

    private boolean hit(float px, float py, float rx, float ry, float rw, float rh) {
        return px >= rx && px <= rx + rw && py >= ry && py <= ry + rh;
    }

    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { Gdx.input.setInputProcessor(null); }
    @Override public void dispose() {}
}
