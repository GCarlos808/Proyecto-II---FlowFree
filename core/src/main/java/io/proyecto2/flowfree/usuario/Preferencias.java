package io.proyecto2.flowfree.usuario;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class Preferencias implements Guardable, Serializable {

    private static final long serialVersionUID = 1L;

    private float volumen;
    private String idioma;
    private String controles;

    public Preferencias() {
        this.volumen = 0.8f;
        this.idioma = "es";
        this.controles = "mouse";
    }

    @Override
    public void guardar(DataOutputStream out) throws IOException {
        out.writeFloat(volumen);
        out.writeUTF(idioma);
        out.writeUTF(controles);
    }

    @Override
    public void cargar(DataInputStream in) throws IOException {
        volumen = in.readFloat();
        idioma = in.readUTF();
        controles = in.readUTF();
    }

    public float getVolumen() { return volumen; }
    public void setVolumen(float volumen) { this.volumen = volumen; }
    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }
    public String getControles() { return controles; }
    public void setControles(String controles) { this.controles = controles; }
}
