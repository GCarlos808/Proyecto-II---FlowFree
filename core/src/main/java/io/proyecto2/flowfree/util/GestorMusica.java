package io.proyecto2.flowfree.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import io.proyecto2.flowfree.constantes.Constantes;

/**
 * Reproduce la música de fondo en bucle.
 * Coloca un archivo {@code .ogg} en {@code assets/music/} (ver {@link Constantes#MUSICA_FONDO}).
 */
public final class GestorMusica {

    private Music musica;
    private float volumen = 0.8f;
    private boolean cargada;
    private boolean reproduciendo;

    public void cargar() {
        if (cargada) return;

        if (!Gdx.files.internal(Constantes.MUSICA_FONDO).exists()) {
            Gdx.app.log("GestorMusica",
                "Archivo no encontrado: " + Constantes.MUSICA_FONDO
                    + " — añade un .ogg en assets/music/ para activar la música.");
            return;
        }

        musica = Gdx.audio.newMusic(Gdx.files.internal(Constantes.MUSICA_FONDO));
        musica.setLooping(true);
        cargada = true;
        aplicarVolumen();
        Gdx.app.log("GestorMusica", "Música cargada: " + Constantes.MUSICA_FONDO);
    }

    public void reproducir() {
        if (musica == null || reproduciendo) return;
        musica.play();
        reproduciendo = true;
    }

    public void pausar() {
        if (musica != null && musica.isPlaying()) {
            musica.pause();
            reproduciendo = false;
        }
    }

    public void reanudar() {
        if (musica == null) return;
        if (!musica.isPlaying()) {
            musica.play();
            reproduciendo = true;
        }
    }

    public void setVolumen(float volumen) {
        this.volumen = Math.max(0f, Math.min(1f, volumen));
        aplicarVolumen();
    }

    public float getVolumen() {
        return volumen;
    }

    public boolean estaDisponible() {
        return musica != null;
    }

    private void aplicarVolumen() {
        if (musica != null) musica.setVolume(volumen);
    }

    public void dispose() {
        if (musica != null) {
            musica.dispose();
            musica = null;
        }
        cargada = false;
        reproduciendo = false;
    }
}
