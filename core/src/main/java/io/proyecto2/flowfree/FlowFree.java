package io.proyecto2.flowfree;


import io.proyecto2.flowfree.usuario.Usuario;
import java.io.*;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class FlowFree extends Juego implements Nivelable {

    private static final ColorFlow[] COLORES_NIVEL = {
        ColorFlow.ROJO, ColorFlow.AZUL, ColorFlow.VERDE, ColorFlow.AMARILLO,
        ColorFlow.NARANJA, ColorFlow.CYAN, ColorFlow.MORADO, ColorFlow.MARRON
    };

    private Celda[][] grid;
    private Flujo[] flujos;
    private NivelFlowFree nivelCargado;
    private int tamañoCuadricula;
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
        this.tamañoCuadricula = nivel.getTamano();
        this.totalCeldas = tamañoCuadricula * tamañoCuadricula;
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
        if (numeroNivel > usuarioActivo.getNivelActual()) {
            throw new NivelBloqueadoException("Nivel " + numeroNivel + " bloqueado. Debes completar el anterior.");
        }

        NivelFlowFree nivel = LevelFactory.crear(numeroNivel);
        cargarNivel(nivel);
    }
    
    @Override
    protected boolean verificarVictoria() {
        if (flujos == null) return false;

        int ocupadas = 0;
        for (int f = 0; f < tamañoCuadricula; f++) {
            for (int c = 0; c < tamañoCuadricula; c++) {
                if (grid[f][c].getColor() != ColorFlow.VACIO) ocupadas++;
            }
        }
        if (ocupadas != totalCeldas) return false;

        for (Flujo flujo : flujos) {
            if (!flujoConectadoEnGrid(flujo)) return false;
        }
        return true;
    }

    private boolean flujoConectadoEnGrid(Flujo flujo) {
        ColorFlow color = flujo.getColor();
        Celda origen = flujo.getOrigen();
        Celda destino = flujo.getDestino();

        if (origen.getColor() != color || destino.getColor() != color) return false;

        Set<Celda> componente = componenteColor(color, origen);
        if (!componente.contains(destino)) return false;

        for (int f = 0; f < tamañoCuadricula; f++) {
            for (int c = 0; c < tamañoCuadricula; c++) {
                Celda celda = grid[f][c];
                if (celda.getColor() == color && !componente.contains(celda)) return false;
            }
        }
        return true;
    }

    private Set<Celda> componenteColor(ColorFlow color, Celda inicio) {
        Set<Celda> visitadas = new HashSet<>();
        ArrayDeque<Celda> pendientes = new ArrayDeque<>();
        pendientes.add(inicio);
        visitadas.add(inicio);

        while (!pendientes.isEmpty()) {
            Celda actual = pendientes.removeFirst();
            for (Celda vecina : vecinas(actual)) {
                if (vecina.getColor() == color && visitadas.add(vecina)) {
                    pendientes.add(vecina);
                }
            }
        }
        return visitadas;
    }

    private Celda[] vecinas(Celda celda) {
        int f = celda.getFila();
        int c = celda.getCol();
        ArrayDeque<Celda> lista = new ArrayDeque<>();
        if (f > 0) lista.add(grid[f - 1][c]);
        if (f < tamañoCuadricula - 1) lista.add(grid[f + 1][c]);
        if (c > 0) lista.add(grid[f][c - 1]);
        if (c < tamañoCuadricula - 1) lista.add(grid[f][c + 1]);
        return lista.toArray(Celda[]::new);
    }
    
    @Override
    protected void aplicarDerrota() {
        usuarioActivo.setVidasRestantes(0);
    }

    @Override
    protected int calcularPuntuacion() {
        int tiempoRestante = getTiempoRestante();
        int bonus  = nivelCargado.getPuntajeBase();
        int penalizacion   = fallos * 50;
        
        return Math.max(0, bonus + (tiempoRestante * 10) - penalizacion);
    }
    
    private void construirGrid() {
        grid = new Celda[tamañoCuadricula][tamañoCuadricula];
        for (int f = 0; f < tamañoCuadricula; f++) {
            for (int c = 0; c < tamañoCuadricula; c++) {
                grid[f][c] = new Celda(f, c);
            }
        }
    }
    
    private void inicializarFlujos() {
        int[][] puntos = nivelCargado.getPuntosIniciales();
        flujos = new Flujo[puntos.length];
        
        for (int i = 0; i < flujos.length; i++) {
            ColorFlow color = COLORES_NIVEL[i];
            Celda origen = grid[puntos[i][0]][puntos[i][1]];
            Celda destino = grid[puntos[i][2]][puntos[i][3]];
            flujos[i] = new Flujo(color, origen, destino);
            origen.setColor(color); origen.setEsPuntoFijo(true);
            destino.setColor(color); destino.setEsPuntoFijo(true);
            celdaOcupadas += 2;
        }
    }

    private int limpiarTrazo(Flujo flujo) {
        int liberadas = 0;
        ColorFlow color = flujo.getColor();
        for (int f = 0; f < tamañoCuadricula; f++) {
            for (int c = 0; c < tamañoCuadricula; c++) {
                Celda celda = grid[f][c];
                if (celda.getColor() == color && !celda.isEsPuntoFijo()) {
                    celda.limpiar();
                    liberadas++;
                }
            }
        }
        flujo.resetearLista();
        return liberadas;
    }
    
    public void onToqueCelda(int fila, int col) {
        if (estado != EstadoJuego.JUGANDO) return;
        Celda celda = grid[fila][col];

        if (celda.getColor() != ColorFlow.VACIO) {
            flujoActivo = encontrarFlujo(celda.getColor());
            if (flujoActivo != null) {
                if (flujoActivo.contiene(celda)) {
                    int liberadas = flujoActivo.retrocederHasta(celda);
                    celdaOcupadas -= liberadas;
                } else if (!celda.isEsPuntoFijo()) {
                    int liberadas = limpiarTrazo(flujoActivo);
                    celdaOcupadas -= liberadas;
                }
            }
            arrastrandoFlujo = true;
        }
    }
    
    public void onArrastreCelda(int fila, int col) {
        if (!arrastrandoFlujo || flujoActivo == null) return;
        if (!esCeldaValida(fila, col)) return;

        Celda celda = grid[fila][col];
        Celda cola = flujoActivo.getCeldaCola();

        if (!esAdyacente(cola, celda)) return;

        if (celda.isEsPuntoFijo() && celda.getColor() != flujoActivo.getColor()) return;

        if (flujoActivo.contiene(celda)) {
            int celdas = flujoActivo.retrocederHasta(celda);
            celdaOcupadas -= celdas;
            return;
        }

        if (celda.getColor() != ColorFlow.VACIO) {
            Flujo otro = encontrarFlujo(celda.getColor());
            if (otro != null && otro != flujoActivo) {
                int liberadas = limpiarTrazo(otro);
                celdaOcupadas -= liberadas;
            }
        }

        if (celda.getColor() == flujoActivo.getColor() && !flujoActivo.contiene(celda)) {
            if (flujoActivo.avanzar(celda)) {
                pasos++;
            }
            return;
        }

        if (flujoActivo.avanzar(celda)) {
            boolean celdaNueva = celda.getColor() == ColorFlow.VACIO;
            celda.setColor(flujoActivo.getColor());
            if (celdaNueva) celdaOcupadas++;
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
        out.writeInt(tamañoCuadricula);
        
        for (int f = 0; f < tamañoCuadricula; f++)
            for (int c = 0; c < tamañoCuadricula; c++)
                grid[f][c].guardar(out);
    }
    
    @Override
    public void cargar(DataInputStream in) throws IOException {
        super.cargar(in);
        pasos             = in.readInt();
        tamañoCuadricula  = in.readInt();
        grid = new Celda[tamañoCuadricula][tamañoCuadricula];
        for (int f = 0; f < tamañoCuadricula; f++)
            for (int c = 0; c < tamañoCuadricula; c++) {
                grid[f][c] = new Celda(f, c);
                grid[f][c].cargar(in);
            }
        
        reconstruirFlujos();
    }
    
    private boolean esCeldaValida(int f, int c) {
        return f >= 0 && f < tamañoCuadricula && c >= 0 && c < tamañoCuadricula;
    }
    
    private Flujo encontrarFlujo(ColorFlow color) {
        for (Flujo f : flujos)
            if (f.getColor() == color) return f;
        return null;
    }
    
    private void reconstruirFlujos() {
        int[][] puntos = nivelCargado.getPuntosIniciales();
        flujos = new Flujo[puntos.length];
        celdaOcupadas = 0;

        for (int i = 0; i < flujos.length; i++) {
            ColorFlow color = COLORES_NIVEL[i];
            Celda origen = grid[puntos[i][0]][puntos[i][1]];
            Celda destino = grid[puntos[i][2]][puntos[i][3]];
            flujos[i] = new Flujo(color, origen, destino);

            for (int f = 0; f < tamañoCuadricula; f++) {
                for (int c = 0; c < tamañoCuadricula; c++) {
                    Celda celda = grid[f][c];
                    if (celda.getColor() == color && !flujos[i].contiene(celda)) {
                        flujos[i].avanzar(celda);
                    }
                }
            }
            if (grid[origen.getFila()][origen.getCol()].getColor() == ColorFlow.VACIO) {
                origen.setColor(color);
                origen.setEsPuntoFijo(true);
            }
            if (grid[destino.getFila()][destino.getCol()].getColor() == ColorFlow.VACIO) {
                destino.setColor(color);
                destino.setEsPuntoFijo(true);
            }
        }

        celdaOcupadas = 0;
        for (int f = 0; f < tamañoCuadricula; f++) {
            for (int c = 0; c < tamañoCuadricula; c++) {
                if (grid[f][c].getColor() != ColorFlow.VACIO) celdaOcupadas++;
            }
        }
    }

    private boolean esAdyacente(Celda a, Celda b) {
        int df = Math.abs(a.getFila() - b.getFila());
        int dc = Math.abs(a.getCol() - b.getCol());
        return (df + dc) == 1;
    }
    
    public Celda[][] getGrid() { return grid; }
    public Flujo[] getFlujos() { return flujos; }
    public int getTamanoCuadricula() { return tamañoCuadricula; }
    public int getPasos() { return pasos; }

    public int contarCeldasOcupadas() {
        int ocupadas = 0;
        for (int f = 0; f < tamañoCuadricula; f++) {
            for (int c = 0; c < tamañoCuadricula; c++) {
                if (grid[f][c].getColor() != ColorFlow.VACIO) ocupadas++;
            }
        }
        return ocupadas;
    }

    public boolean flujosConectados() {
        if (flujos == null) return false;
        for (Flujo flujo : flujos) {
            if (!flujoConectadoEnGrid(flujo)) return false;
        }
        return true;
    }

    public boolean todosFlujosCerrados() {
        return flujosConectados() && contarCeldasOcupadas() == totalCeldas;
    }

    public int getTotalCeldas() { return totalCeldas; }
}
