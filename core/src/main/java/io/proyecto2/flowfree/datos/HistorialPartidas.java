package io.proyecto2.flowfree.datos;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class HistorialPartidas implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int  MAX_REGISTROS = 50;
    
    private static class NodoPartida implements Serializable {
        RegistroPartida dato;
        NodoPartida siguiente;
        
        NodoPartida(RegistroPartida dato) { this.dato = dato; }
    }
    
    private NodoPartida cabeza;
    private int tamaño;
    
    public HistorialPartidas() {
        cabeza = null;
        tamaño = 0;
    }
    
    public void agregar(RegistroPartida registro) {
        NodoPartida nuevo = new NodoPartida(registro);
        nuevo.siguiente = cabeza;
        cabeza = nuevo;
        tamaño++;
        
        if (tamaño > MAX_REGISTROS) eliminarUltimo();
    }
    
    private void eliminarUltimo() {
        if (cabeza == null) return;
        if (cabeza.siguiente == null) { cabeza = null; tamaño--; return; }
        NodoPartida actual = cabeza;
        while (actual.siguiente.siguiente != null) actual = actual.siguiente;
        actual.siguiente = null;
        tamaño--;
    }
    
    public List<RegistroPartida> aLista() {
        List<RegistroPartida> lista = new ArrayList<>(tamaño);
        NodoPartida actual = cabeza;
        while (actual != null) {
            lista.add(actual.dato);
            actual = actual.siguiente;
        }
        return lista;
    }
    
    public int getTamano() { return tamaño; }
    public boolean estaVacio() { return cabeza == null; }
    
    public RegistroPartida getPrimero() {
        return cabeza != null ? cabeza.dato : null;
    }
}
