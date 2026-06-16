package io.proyecto2.flowfree.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import io.proyecto2.flowfree.Main;
import io.proyecto2.flowfree.constantes.Estilos;
import io.proyecto2.flowfree.datos.GestorUsuarios;
import io.proyecto2.flowfree.usuario.Preferencias;
import io.proyecto2.flowfree.usuario.Usuario;

import java.io.IOException;

public class PantallaPerfil implements Screen {

    private static final String[] AVATARES = {"default", "space", "retro", "neon"};
    private static final String[] IDIOMAS = {"es", "en"};
    private static final String[] CONTROLES = {"mouse", "teclado"};

    private final Main app;
    private final Usuario usuario;

    private int avatarIdx = 0;
    private int idiomaIdx = 0;
    private int controlesIdx = 0;
    private float volumen = 0.8f;

    private String mensaje = "";
    private float mensajeT = 0f;

    public PantallaPerfil(Main app, Usuario usuario) {
        this.app = app;
        this.usuario = usuario;
        cargarDesdeUsuario();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int sx, int sy, int p, int b) {
                manejarClic(sx, Gdx.graphics.getHeight() - sy);
                return true;
            }
        });
    }

    private void cargarDesdeUsuario() {
        String avatarActual = usuario.getRutaAvatar() == null ? "default" : usuario.getRutaAvatar();
        avatarIdx = buscarIndice(AVATARES, avatarActual, 0);
        Preferencias pref = usuario.getPreferencias();
        idiomaIdx = buscarIndice(IDIOMAS, pref.getIdioma(), 0);
        controlesIdx = buscarIndice(CONTROLES, pref.getControles(), 0);
        volumen = Math.max(0f, Math.min(1f, pref.getVolumen()));
    }

    private int buscarIndice(String[] arreglo, String valor, int porDefecto) {
        for (int i = 0; i < arreglo.length; i++) {
            if (arreglo[i].equalsIgnoreCase(valor)) return i;
        }
        return porDefecto;
    }

    private void manejarClic(float x, float y) {
        if (hit(x, y, 180f, 440f, 40f, 40f)) avatarIdx = (avatarIdx + AVATARES.length - 1) % AVATARES.length;
        if (hit(x, y, 580f, 440f, 40f, 40f)) avatarIdx = (avatarIdx + 1) % AVATARES.length;
        if (hit(x, y, 180f, 360f, 440f, 42f)) idiomaIdx = (idiomaIdx + 1) % IDIOMAS.length;
        if (hit(x, y, 180f, 300f, 440f, 42f)) controlesIdx = (controlesIdx + 1) % CONTROLES.length;

        if (hit(x, y, 180f, 240f, 440f, 18f)) {
            volumen = (x - 180f) / 440f;
            volumen = Math.max(0f, Math.min(1f, volumen));
        }

        if (hit(x, y, 180f, 150f, 210f, 44f)) {
            guardarPerfil();
            return;
        }

        if (hit(x, y, 410f, 150f, 210f, 44f)) {
            app.cambiarPantalla(new PantallaMenu(app, usuario));
        }
    }

    private void guardarPerfil() {
        usuario.setRutaAvatar(AVATARES[avatarIdx]);
        usuario.getPreferencias().setIdioma(IDIOMAS[idiomaIdx]);
        usuario.getPreferencias().setControles(CONTROLES[controlesIdx]);
        usuario.getPreferencias().setVolumen(volumen);

        try {
            GestorUsuarios.getInstance().guardarProgresoAhora();
            mensaje = "Perfil guardado correctamente";
        } catch (IOException e) {
            mensaje = "No se pudo guardar el perfil";
        }
        mensajeT = 3f;
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

        app.fontGrande.setColor(Estilos.COLOR_ACENTO_CYAN);
        GlyphLayout titulo = new GlyphLayout(app.fontGrande, "PERFIL");
        app.fontGrande.draw(app.batch, "PERFIL", (800f - titulo.width) / 2f, 550f);

        app.dibujarRect(150f, 120f, 500f, 380f, Estilos.COLOR_PANEL_CARD);
        app.dibujarBorde(150f, 120f, 500f, 380f, 1f, Estilos.COLOR_BORDE_PANEL);

        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, "Usuario", 180f, 482f);
        app.fontMediana.setColor(Estilos.COLOR_TEXTO_CLARO);
        app.fontMediana.draw(app.batch, usuario.getNombreUsuario(), 180f, 462f);

        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, "Avatar", 180f, 422f);
        dibujarSelectorAvatar();

        dibujarCampo("Idioma", IDIOMAS[idiomaIdx].toUpperCase(), 360f);
        dibujarCampo("Controles", CONTROLES[controlesIdx], 300f);
        dibujarVolumen();

        boton("GUARDAR", 180f, 150f, true);
        boton("VOLVER", 410f, 150f, false);

        if (!mensaje.isEmpty()) {
            app.fontPequena.setColor(mensaje.startsWith("No") ? Estilos.COLOR_ACENTO_ROJO : Estilos.COLOR_ACENTO_VERDE);
            GlyphLayout gm = new GlyphLayout(app.fontPequena, mensaje);
            app.fontPequena.draw(app.batch, mensaje, (800f - gm.width) / 2f, 100f);
        }

        app.batch.end();
    }

    private void dibujarSelectorAvatar() {
        app.dibujarRect(180f, 440f, 40f, 40f, Estilos.COLOR_PANEL_INPUT);
        app.dibujarBorde(180f, 440f, 40f, 40f, 1f, Estilos.COLOR_BORDE_PANEL);
        app.fontMediana.setColor(Estilos.COLOR_TEXTO_CLARO);
        app.fontMediana.draw(app.batch, "<", 194f, 468f);

        app.dibujarRect(240f, 440f, 320f, 40f, Estilos.COLOR_PANEL_INPUT);
        app.dibujarBorde(240f, 440f, 320f, 40f, 1f, Estilos.COLOR_BORDE_PANEL);
        app.fontMediana.setColor(Estilos.COLOR_ACENTO_CYAN);
        GlyphLayout ga = new GlyphLayout(app.fontMediana, AVATARES[avatarIdx]);
        app.fontMediana.draw(app.batch, AVATARES[avatarIdx], 240f + (320f - ga.width) / 2f, 466f);

        app.dibujarRect(580f, 440f, 40f, 40f, Estilos.COLOR_PANEL_INPUT);
        app.dibujarBorde(580f, 440f, 40f, 40f, 1f, Estilos.COLOR_BORDE_PANEL);
        app.fontMediana.setColor(Estilos.COLOR_TEXTO_CLARO);
        app.fontMediana.draw(app.batch, ">", 594f, 468f);
    }

    private void dibujarCampo(String label, String valor, float y) {
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, label, 180f, y + 32f);
        app.dibujarRect(180f, y, 440f, 42f, Estilos.COLOR_PANEL_INPUT);
        app.dibujarBorde(180f, y, 440f, 42f, 1f, Estilos.COLOR_BORDE_PANEL);
        app.fontMediana.setColor(Estilos.COLOR_TEXTO_CLARO);
        app.fontMediana.draw(app.batch, valor, 200f, y + 28f);
    }

    private void dibujarVolumen() {
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, "Volumen", 180f, 272f);

        app.dibujarRect(180f, 240f, 440f, 18f, Estilos.COLOR_PANEL_INPUT);
        app.dibujarRect(180f, 240f, 440f * volumen, 18f, Estilos.COLOR_ACENTO_CYAN);
        app.dibujarBorde(180f, 240f, 440f, 18f, 1f, Estilos.COLOR_BORDE_PANEL);

        String porcentaje = String.format("%d%%", Math.round(volumen * 100f));
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_CLARO);
        app.fontPequena.draw(app.batch, porcentaje, 630f, 255f);
    }

    private void boton(String txt, float x, float y, boolean primario) {
        app.dibujarRect(x, y, 210f, 44f, primario ? Estilos.COLOR_PANEL_INPUT : Estilos.COLOR_PANEL_CARD);
        app.dibujarBorde(x, y, 210f, 44f, 1f, primario ? Estilos.COLOR_ACENTO_CYAN : Estilos.COLOR_BORDE_PANEL);
        app.fontMediana.setColor(primario ? Estilos.COLOR_ACENTO_CYAN : Estilos.COLOR_TEXTO_GRIS);
        GlyphLayout g = new GlyphLayout(app.fontMediana, txt);
        app.fontMediana.draw(app.batch, txt, x + (210f - g.width) / 2f, y + 30f);
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
