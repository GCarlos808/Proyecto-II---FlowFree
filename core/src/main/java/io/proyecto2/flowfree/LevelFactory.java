package io.proyecto2.flowfree;


public final class LevelFactory {

    private LevelFactory() {}

    public static NivelFlowFree crear(int numero) {
        return switch (numero) {
            case 1 -> nivel1();
            case 2 -> nivel2();
            case 3 -> nivel3();
            case 4 -> nivel4();
            case 5 -> nivel5();
            default -> throw new constantes.NivelInvalidoException(
                "Nivel " + numero + " no existe. Rango: 1–5");
        };
    }
    
    private static NivelFlowFree nivel1() {
        
        int[][] puntos = {
            {0,0, 4,4},  // ROJO
            {0,4, 4,0},  // AZUL
            {2,1, 2,3},  // VERDE
        };
        return new NivelFlowFree(1, 5, puntos, 90, 1000, "Inicio");
    }
    
    
    private static NivelFlowFree nivel2() {
        int[][] puntos = {
            {0,0, 5,5}, {0,5, 5,0}, {1,2, 4,3}, {2,1, 3,4}
        };
        return new NivelFlowFree(2, 6, puntos, 80, 1500, "Calentando");
    }
    
    private static NivelFlowFree nivel3() {
        int[][] puntos = {
            {0,0, 6,6}, {0,6, 6,0}, {1,3, 5,3},
            {3,1, 3,5}, {2,2, 4,4}
        };
        
        return new NivelFlowFree(3, 7, puntos, 70, 2000, "Intermedio");
    }
    
    private static NivelFlowFree nivel4() {
        int[][] puntos = {
            {0,0,7,7},{0,7,7,0},{1,1,6,6},{1,6,6,1},{3,0,3,7},{0,3,7,4}
        };
        return new NivelFlowFree(4, 8, puntos, 60, 2800, "Avanzado");
    }
    
    private static NivelFlowFree nivel5() {
        int[][] puntos = {
            {0,0,8,8},{0,8,8,0},{1,4,7,4},{4,1,4,7},
            {2,2,6,6},{2,6,6,2},{0,4,8,4}
        };
        return new NivelFlowFree(5, 9, puntos, 50, 4000, "Maestro");
    }
}
