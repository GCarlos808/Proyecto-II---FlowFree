package io.proyecto2.flowfree;

import io.proyecto2.flowfree.usuario.Guardable;
import java.io.*;

public class Celda implements Guardable {
    private final int fila, col;
    private ColorFlujo color;
    private boolean esPuntoFijo; 
    
    public Celda(int fila, int col) {
        this.fila = fila; this.col = col;
        this.color = ColorFlujo.VACIO;
        this.esPuntoFijo = false;
    }
    
    public void limpiar() {
        if (!esPuntoFijo) color = ColorFlujo.VACIO;
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
        // fila/col ya establecidos en el constructor
        in.readInt(); in.readInt();
        color       = ColorFlujo.valueOf(in.readUTF());
        esPuntoFijo = in.readBoolean();
    }
    
    public int getFila() { return fila; }
    public int getCol() { return col; }
    public ColorFlujo getColor() { return color; }
    public void setColor(ColorFlujo c) { this.color = c; }
    public boolean isEsPuntoFijo() { return esPuntoFijo; }
    public void setEsPuntoFijo(boolean b) { esPuntoFijo = b; }
}
