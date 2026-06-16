package io.proyecto2.flowfree;


import com.badlogic.gdx.graphics.Color;

public enum ColorFlow {
    
    ROJO (new Color(0.95f, 0.20f, 0.20f, 1f)),
    AZUL (new Color(0.20f, 0.50f, 0.95f, 1f)),
    VERDE (new Color(0.10f, 0.85f, 0.30f, 1f)),
    AMARILLO (new Color(0.95f, 0.85f, 0.10f, 1f)),
    NARANJA (new Color(0.95f, 0.55f, 0.10f, 1f)),
    CYAN (new Color(0.00f, 0.85f, 0.90f, 1f)),
    MORADO (new Color(0.60f, 0.20f, 0.90f, 1f)),
    MARRON (new Color(0.55f, 0.27f, 0.07f, 1f)),
    VACIO (Color.CLEAR);
    
    public final Color gdxColor;
    
    ColorFlow(Color c) { this.gdxColor = c; }
}
