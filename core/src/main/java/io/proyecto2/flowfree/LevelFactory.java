package io.proyecto2.flowfree;


/**
 * Niveles con solución garantizada.
 * Cada fila en {@code puntos} es un par: {fila1, col1, fila2, col2}.
 *
 * Los niveles 2–5 amplían el mismo diseño base (6×6 resoluble) añadiendo
 * un borde en R (arriba/derecha) y en Y (abajo) para aumentar tamaño y dificultad.
 */
public final class LevelFactory {

    private LevelFactory() {}

    public static NivelFlowFree crear(int numero) {
        return switch (numero) {
            case 1 -> nivel1();
            case 2 -> nivel2();
            case 3 -> nivel3();
            case 4 -> nivel4();
            case 5 -> nivel5();
            default -> throw new NivelInvalidoException(
                "Nivel " + numero + " no existe. Rango: 1–5");
        };
    }

 
    private static NivelFlowFree nivel1() {
        // 5×5, 3 colores — tutorial (bloque central verde entre tuberías R y B)
        int[][] puntos = {
            {0, 0, 0, 2}, // rojo:   arriba-izquierda ↔ arriba-centro
            {0, 3, 4, 2}, // azul:   arriba-derecha ↔ abajo-derecha
            {1, 2, 1, 3}, // verde:  centro-izquierda ↔ centro-derecha
        };
        return new NivelFlowFree(1, 5, puntos, 120, 1000, "Inicio");
    }

    private static NivelFlowFree nivel2() {
        // 6×6, 3 colores — marco R + tubería B + bloque G interior
        int[][] puntos = {
            {0, 0, 5, 4}, // rojo
            {1, 4, 5, 3}, // azul
            {2, 1, 4, 4}, // verde
        };
        return new NivelFlowFree(2, 6, puntos, 100, 1500, "Calentando");
    }

    private static NivelFlowFree nivel3() {
        // 7×7, 4 colores — nivel 2 + columna R y fila Y
        int[][] puntos = {
            {0, 0, 6, 6}, // rojo
            {1, 4, 5, 3}, // azul
            {2, 1, 4, 4}, // verde
            {6, 0, 6, 5}, // amarillo (4.º color)
        };
        return new NivelFlowFree(3, 7, puntos, 90, 2000, "Intermedio");
    }

    private static NivelFlowFree nivel4() {
        // 8×8, 4 colores — nivel 3 ampliado
        int[][] puntos = {
            {0, 0, 7, 7}, // rojo
            {1, 4, 5, 3}, // azul
            {2, 1, 4, 4}, // verde
            {6, 0, 7, 6}, // amarillo
        };
        return new NivelFlowFree(4, 8, puntos, 75, 2800, "Avanzado");
    }

    private static NivelFlowFree nivel5() {
        // 9×9, 4 colores — nivel 4 ampliado (máxima extensión del mismo diseño)
        int[][] puntos = {
            {0, 0, 8, 8}, // rojo
            {1, 4, 5, 3}, // azul
            {2, 1, 4, 4}, // verde
            {6, 0, 8, 7}, // amarillo
        };
        return new NivelFlowFree(5, 9, puntos, 60, 4000, "Maestro");
    }
}
