package io.proyecto2.flowfree;


public interface Juegable {
    
    void iniciar();
    void pausar();
    void reanudar();
    void reiniciar();
    void actualizar(float delta);
}
