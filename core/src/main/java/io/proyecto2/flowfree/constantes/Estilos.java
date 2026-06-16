package io.proyecto2.flowfree.constantes;

import com.badlogic.gdx.graphics.Color;


public final class Estilos {

    private Estilos() {}
    
    public static final Color COLOR_FONDO_OSCURO = new Color(0.06f, 0.06f, 0.10f, 1f);
    public static final Color COLOR_PANEL_CARD = new Color(0.12f, 0.12f, 0.18f, 1f);
    public static final Color COLOR_PANEL_INPUT = new Color(0.16f, 0.16f, 0.24f, 1f);
    public static final Color COLOR_HOVER = new Color(0.20f, 0.20f, 0.30f, 1f);
    
    public static final Color COLOR_ACENTO_CYAN = new Color(0.00f, 0.85f, 0.90f, 1f);
    public static final Color COLOR_ACENTO_CYAN_DIM = new Color(0.00f, 0.45f, 0.50f, 1f);
    public static final Color COLOR_ACENTO_VERDE = new Color(0.10f, 0.90f, 0.30f, 1f);
    public static final Color COLOR_ACENTO_ROJO = new Color(0.95f, 0.20f, 0.20f, 1f);
    public static final Color COLOR_ACENTO_AMARILLO = new Color(0.95f, 0.85f, 0.10f, 1f);
    public static final Color COLOR_ACENTO_NARANJA = new Color(0.95f, 0.55f, 0.10f, 1f);
    public static final Color COLOR_ACENTO_MORADO = new Color(0.60f, 0.20f, 0.90f, 1f);
    
    public static final Color COLOR_TEXTO_CLARO = new Color(0.92f, 0.92f, 0.95f, 1f);
    public static final Color COLOR_TEXTO_GRIS = new Color(0.60f, 0.60f, 0.65f, 1f);
    public static final Color COLOR_TEXTO_DISABLED = new Color(0.35f, 0.35f, 0.40f, 1f);
    public static final Color COLOR_BORDE_PANEL  = new Color(0.25f, 0.25f, 0.35f, 1f);
    public static final Color COLOR_BORDE_ACTIVO = new Color(0.00f, 0.85f, 0.90f, 0.8f);
    public static final Color COLOR_BORDE_ERROR  = new Color(0.95f, 0.20f, 0.20f, 0.8f);
    
    public static final Color[] COLORES_FLUJO = {
        new Color(0.95f, 0.20f, 0.20f, 1f), //rojo
        new Color(0.20f, 0.50f, 0.95f, 1f), //azul
        new Color(0.10f, 0.85f, 0.30f, 1f), //verde
        new Color(0.95f, 0.85f, 0.10f, 1f), //amarillo
        new Color(0.95f, 0.55f, 0.10f, 1f), //naranja
        new Color(0.00f, 0.85f, 0.90f, 1f), //cyan
        new Color(0.60f, 0.20f, 0.90f, 1f), //morado
        new Color(0.55f, 0.27f, 0.07f, 1f), //café
    };
    
    public static final float ALTO_BOTON = 48f;
    public static final float ALTO_INPUT  = 44f;
    public static final float PADDING_INPUT = 12f;
    public static final float GROSOR_BORDE = 2f;
    public static final float ANCHO_FORM = 360f;
}
