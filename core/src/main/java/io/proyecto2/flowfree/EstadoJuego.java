package io.proyecto2.flowfree;


public enum EstadoJuego {
    MENU,
    JUGANDO,
    PAUSADO,
    VICTORIA,
    DERROTA,
    CARGANDO;

    public boolean estaActivo() {
        return this == JUGANDO;
    }
}