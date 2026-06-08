package io.proyecto2.flowfree;


import io.proyecto2.flowfree.usuario.Usuario;
import java.io.*;

public class FlowFree extends Juego implements Nivelable {
    
    private Celda[][] grid;
    private Flujo[] flujos;
    private NivelFlowFree nivelCargado;
    private int tamanoCuadricula;
    private int totalCeldas;
    private int celdaOcupadas;
    private int pasos;
    
    private Flujo    flujoActivo;
    private boolean  arrastrandoFlujo;

    public FlowFree(Usuario usuario) {
        super(usuario);
    }
    
    @Override
    public void cargarNivel(NivelFlowFree nivel) {
        this.nivelCargado = nivel;
        this.tamanoCuadricula = nivel.getTamano();
        this.totalCeldas = tamanoCuadricula * tamanoCuadricula;
        this.tiempoLimiteSegundos = nivel.getTiempoLimite();
        this.pasos = 0;
        this.celdaOcupadas = 0;
        this.flujoActivo = null;

        construirGrid();
        inicializarFlujos();
    }
    
    @Override
    public boolean esCompleto() {
        return verificarVictoria();
    }
    
    @Override
    public int getNumeroPasos() {
        return pasos;
    }
    
    @Override
    protected void iniciarNivel(int numeroNivel) {
        if (numeroNivel > usuarioActivo.getNivelActual() + 1) {
            throw new NivelBloqueadoException("Nivel " + numeroNivel + " bloqueado. Debes completar el anterior.");
        }
        
        NivelFlowFree nivel = FabricaNiveles.crear(numeroNivel);
        cargarNivel(nivel);
    }
    
    @Override
    protected boolean verificarVictoria() {
        boolean todosFlujosCerrados = true;
        for (Flujo f : flujos) {
            if (!f.estaCerrado()) {
                todosFlujosCerrados = false;
                break;
            }
        }
        return todosFlujosCerrados && (celdaOcupadas == totalCeldas);
    }
    
    @Override
    protected void aplicarDerrota() {
        
    }

    @Override
    protected int calcularPuntuacion() {
        int tiempoRestante = getTiempoRestante();
        int bonus          = nivelCargado.getPuntajeBase();
        int penalizacion   = fallos * 50;
        
        return Math.max(0, bonus + (tiempoRestante * 10) - penalizacion);
    }
    
    private void construirGrid() {
        grid = new Celda[tamanoCuadricula][tamanoCuadricula];
        for (int f = 0; f < tamanoCuadricula; f++) {
            for (int c = 0; c < tamanoCuadricula; c++) {
                grid[f][c] = new Celda(f, c);
            }
        }
    }
    
    private void inicializarFlujos() {
        int[][] puntos = nivelCargado.getPuntosIniciales();
        flujos = new Flujo[puntos.length / 2];
        
        for (int i = 0; i < flujos.length; i++) {
            ColorFlujo color = ColorFlujo.values()[i];
            Celda origen = grid[puntos[i * 2][0]][puntos[i * 2][1]];
            Celda destino = grid[puntos[i * 2 + 1][0]][puntos[i * 2 + 1][1]];
            flujos[i] = new Flujo(color, origen, destino);
            origen.setColor(color); origen.setEsPuntoFijo(true);
            destino.setColor(color); destino.setEsPuntoFijo(true);
            celdaOcupadas += 2;
        }
    }
    
    public void onToqueCelda(int fila, int col) {
        if (estado != EstadoJuego.JUGANDO) return;
        Celda celda = grid[fila][col];

        if (celda.getColor() != ColorFlujo.VACIO) {
            
            flujoActivo    = encontrarFlujo(celda.getColor());
            arrastrandoFlujo = true;
        }
    }
    
    public void onArrastreCelda(int fila, int col) {
        if (!arrastrandoFlujo || flujoActivo == null) return;
        if (!esCeldaValida(fila, col)) return;

        Celda celda = grid[fila][col];
        
        if (celda.isEsPuntoFijo() && celda.getColor() != flujoActivo.getColor()) return;
        
        if (flujoActivo.contiene(celda)) {
            int celdas = flujoActivo.retrocederHasta(celda);
            celdaOcupadas -= celdas;
            return;
        }
        
        if (celda.getColor() != ColorFlujo.VACIO) {
            Flujo otro = encontrarFlujo(celda.getColor());
            int liberadas = otro.limpiar();
            celdaOcupadas -= liberadas;
        }
        
        if (flujoActivo.avanzar(celda)) {
            celda.setColor(flujoActivo.getColor());
            celdaOcupadas++;
            pasos++;
        }
    }

    public void onSoltarCelda() {
        arrastrandoFlujo = false;
        flujoActivo = null;
        actualizar(0);
    }
    
    @Override
    public void guardar(DataOutputStream out) throws IOException {
        super.guardar(out);
        out.writeInt(pasos);
        out.writeInt(tamanoCuadricula);
        
        for (int f = 0; f < tamanoCuadricula; f++)
            for (int c = 0; c < tamanoCuadricula; c++)
                grid[f][c].guardar(out);
    }
    
    @Override
    public void cargar(DataInputStream in) throws IOException {
        super.cargar(in);
        pasos             = in.readInt();
        tamanoCuadricula  = in.readInt();
        grid = new Celda[tamanoCuadricula][tamanoCuadricula];
        for (int f = 0; f < tamanoCuadricula; f++)
            for (int c = 0; c < tamanoCuadricula; c++) {
                grid[f][c] = new Celda(f, c);
                grid[f][c].cargar(in);
            }
        
        reconstruirFlujos();
    }
    
    private boolean esCeldaValida(int f, int c) {
        return f >= 0 && f < tamanoCuadricula && c >= 0 && c < tamanoCuadricula;
    }
    
    private Flujo encontrarFlujo(ColorFlujo color) {
        for (Flujo f : flujos)
            if (f.getColor() == color) return f;
        return null;
    }

    private void reconstruirFlujos() {
        
    }

    // Getters Dev B
    public Celda[][] getGrid() { return grid; }
    public Flujo[] getFlujos() { return flujos; }
    public int getTamanoCuadricula() { return tamanoCuadricula; }
    public int getPasos() { return pasos; }
}
