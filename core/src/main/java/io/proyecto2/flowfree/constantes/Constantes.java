package io.proyecto2.flowfree.constantes;


public final class Constantes {

    private Constantes() {}
    
    public static final String CARPETA_DATOS = "data";
    public static final String CARPETA_USUARIOS = "data/usuarios";
    public static final String RANKING_GLOBAL = "data/ranking_global.dat";
    
    public static final int ANCHO_VIRTUAL = 800;
    public static final int ALTO_VIRTUAL  = 600;
    
    public static final int    ANCHO_VENTANA = 800;
    public static final int    ALTO_VENTANA  = 600;
    public static final String TITULO_APP    = "Flow Free";
    
    public static final int NIVEL_MAX    = 5;
    public static final int NIVEL_MIN    = 1;
    public static final int VIDAS_INICIO = 3;
    
    public static final int AUTOSAVE_INTERVALO = 30;
    
    public static final int RAF_LONG_NOMBRE      = 32;
    public static final int RAF_TAMANO_REGISTRO  = 56; // 32+4+4+8+8
    
    public static final int PASS_MIN_LONGITUD = 8;
}
