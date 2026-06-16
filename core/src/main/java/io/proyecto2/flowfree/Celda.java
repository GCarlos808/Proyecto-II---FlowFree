package io.proyecto2.flowfree;

import io.proyecto2.flowfree.usuario.Guardable;
import java.io.*;

public class Celda implements Guardable {
    private final int fila, col;
    private ColorFlow color;
    private boolean esPuntoFijo; 
    
    public Celda(int fila, int col) {
        this.fila = fila; this.col = col;
        this.color = ColorFlow.VACIO;
        this.esPuntoFijo = false;
    }
    
    public void limpiar() {
        if (!esPuntoFijo) color = ColorFlow.VACIO;
    }
    
    @Override
    public void guardar(DataOutputStream out) throws IOException {
        out.writeInt(fila);
        out.writeInt(col);
        out.writeUTF(color.name());
        out.writeBoolean(esPuntoFijo);
    }
    
    @Override
    public void cargar(DataInputStream in) throws IOException {
        
        in.readInt(); in.readInt();
        color = ColorFlow.valueOf(in.readUTF());
        esPuntoFijo = in.readBoolean();
    }
    
    public int getFila() { return fila; }
    public int getCol() { return col; }
    public ColorFlow getColor() { return color; }
    public void setColor(ColorFlow c) { this.color = c; }
    public boolean isEsPuntoFijo() { return esPuntoFijo; }
    public void setEsPuntoFijo(boolean b) { esPuntoFijo = b; }
}
