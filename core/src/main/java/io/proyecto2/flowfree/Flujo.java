package io.proyecto2.flowfree;


public class Flujo {
    private final ColorFlow color;
    private final Celda origen;
    private final Celda destino;
    private NodoCelda head;
    private NodoCelda cola;
    private int longitud;
    
    public Flujo(ColorFlow color, Celda origen, Celda destino) {
        this.color = color;
        this.origen = origen;
        this.destino = destino;
        this.head= new NodoCelda(origen);
        this.cola = head;
        this.longitud = 1;
    }
    
    public boolean avanzar(Celda celda) {
        NodoCelda nuevo = new NodoCelda(celda);
        cola.siguiente = nuevo;
        cola = nuevo;
        longitud++;
        return true;
    }
    
    public int retrocederHasta(Celda celda) {
        int limpiadas = 0;
        while (cola != null && cola.celda != celda && cola != head) {
            if (!cola.celda.isEsPuntoFijo()) cola.celda.limpiar();
            cola = penultimo();
            if (cola != null) cola.siguiente = null;
            longitud--;
            limpiadas++;
        }
        return limpiadas;
    }
    
    public int limpiar() {
        int liberadas = 0;
        NodoCelda actual = head.siguiente;
        while (actual != null) {
            if (!actual.celda.isEsPuntoFijo()) { actual.celda.limpiar(); liberadas++; }
            actual = actual.siguiente;
        }
        resetearLista();
        return liberadas;
    }

    public void resetearLista() {
        head.siguiente = null;
        cola = head;
        longitud = 1;
    }
    
    public boolean contiene(Celda celda) {
        NodoCelda n = head;
        while (n != null) { if (n.celda == celda) return true; n = n.siguiente; }
        return false;
    }
    
    public boolean estaCerrado() {
        return longitud >= 2 && contiene(origen) && contiene(destino);
    }
    
    private NodoCelda penultimo() {
        if (head == cola) return null;
        NodoCelda n = head;
        while (n.siguiente != cola) n = n.siguiente;
        return n;
    }
    
    public ColorFlow getColor() { return color; }
    public Celda getOrigen() { return origen; }
    public Celda getDestino() { return destino; }
    public int getLongitud() { return longitud; }
    public Celda getCeldaCola() { return cola != null ? cola.celda : origen; }
    
    private static class NodoCelda {
        Celda celda;
        NodoCelda siguiente;
        NodoCelda(Celda c) { celda = c; }
    }
}
