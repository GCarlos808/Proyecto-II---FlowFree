package io.proyecto2.flowfree.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import io.proyecto2.flowfree.Main;
import io.proyecto2.flowfree.FlowFree;
import io.proyecto2.flowfree.Celda;
import io.proyecto2.flowfree.ColorFlow;
import io.proyecto2.flowfree.EstadoJuego;
import io.proyecto2.flowfree.constantes.Constantes;
import io.proyecto2.flowfree.constantes.Estilos;
import io.proyecto2.flowfree.hilos.AutosaveThread;
import io.proyecto2.flowfree.usuario.Usuario;

public class PantallaJuego implements Screen {

    private final Main app;
    private final FlowFree juego;
    private final AutosaveThread autosave;
    private float gridOffsetX, gridOffsetY, celdaSize;
    
    private int ultimaFilaTocada = -1, ultimaColTocada = -1;
    
    private boolean pausado = false;
    
    public PantallaJuego(Main app, Usuario usuario, int numeroNivel) {
        this.app   = app;
        this.juego = new FlowFree(usuario);
        this.autosave = new AutosaveThread(usuario);
        this.autosave.iniciar();
        
        juego.iniciarDesdeNivel(numeroNivel);
        
        calcularGeometria(juego.getTamanoCuadricula());
    }
    
    private void calcularGeometria(int tamaño) {
        float areaDisponibleH = 800f - 40f;
        float areaDisponibleV = 600f - 120f;
        celdaSize = Math.min(areaDisponibleH / tamaño, areaDisponibleV / tamaño);
        float totalW = celdaSize * tamaño;
        float totalH = celdaSize * tamaño;
        gridOffsetX = (800f - totalW) / 2f;
        gridOffsetY = (600f - totalH) / 2f - 20f;
    }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            
            @Override
            public boolean touchDown(int sx, int sy, int p, int b) {
                if (pausado) return true;
                int[] rc = screenToGrid(sx, sy);
                if (rc != null) {
                    ultimaFilaTocada = rc[0];
                    ultimaColTocada  = rc[1];
                    juego.onToqueCelda(rc[0], rc[1]);
                }
                return true;
            }
            
            @Override
            public boolean touchDragged(int sx, int sy, int p) {
                if (pausado) return true;
                int[] rc = screenToGrid(sx, sy);
                if (rc != null && (rc[0] != ultimaFilaTocada || rc[1] != ultimaColTocada)) {
                    ultimaFilaTocada = rc[0];
                    ultimaColTocada = rc[1];
                    juego.onArrastreCelda(rc[0], rc[1]);
                }
                return true;
            }
            
            @Override
            public boolean touchUp(int sx, int sy, int p, int b) {
                juego.onSoltarCelda();
                ultimaFilaTocada = -1;
                ultimaColTocada = -1;
                return true;
            }
            
            @Override
            public boolean keyDown(int key) {
                if (key == com.badlogic.gdx.Input.Keys.ESCAPE) {
                    
                    app.cambiarPantalla(new PantallaMapa(app, juego.getUsuarioActivo()));
                } else if (key == com.badlogic.gdx.Input.Keys.P) {
                    
                    if (pausado) { juego.reanudar(); pausado = false; }
                    else         { juego.pausar();   pausado = true;  }
                }
                return true;
            }
        });
    }
    
    private int[] screenToGrid(int sx, int sy) {
        float worldX = sx;
        float worldY = Gdx.graphics.getHeight() - sy;

        int col = (int)((worldX - gridOffsetX) / celdaSize);
        int fil = (int)((worldY - gridOffsetY) / celdaSize);
        int tam = juego.getTamanoCuadricula();

        if (col < 0 || col >= tam || fil < 0 || fil >= tam) return null;
        return new int[]{ fil, col };
    }
    

    @Override
    public void render(float delta) {
        
        if (!pausado) juego.actualizar(delta);
        manejarEstado();
        
        Gdx.gl.glClearColor(Estilos.COLOR_FONDO_OSCURO.r, Estilos.COLOR_FONDO_OSCURO.g, Estilos.COLOR_FONDO_OSCURO.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        app.batch.begin();
        dibujarGrid();
        dibujarHUD();
        if (pausado) dibujarPausa();
        app.batch.end();
    }
    
    private void manejarEstado() {
        EstadoJuego estado = juego.getEstado();
        if (estado == EstadoJuego.VICTORIA) {
            autosave.guardarAhora();
            int completado = juego.getUltimoNivelCompletado();
            if (completado >= Constantes.NIVEL_MAX) {
                app.cambiarPantalla(new PantallaVictoria(app, juego.getUsuarioActivo()));
            } else {
                app.cambiarPantalla(new PantallaNivelCompletado(
                    app,
                    juego.getUsuarioActivo(),
                    completado,
                    juego.getPuntosUltimoNivel(),
                    juego.getPasos()));
            }
        } else if (estado == EstadoJuego.DERROTA) {
            app.cambiarPantalla(new PantallaMenu(app, juego.getUsuarioActivo()));
        }
    }
    
    private void dibujarGrid() {
        Celda[][] grid = juego.getGrid();
        if (grid == null) return;

        int tam = juego.getTamanoCuadricula();
        float pad = celdaSize * 0.06f;
        
        for (int f = 0; f < tam; f++) {
            for (int c = 0; c < tam; c++) {
                float x = gridOffsetX + c * celdaSize;
                float y = gridOffsetY + f * celdaSize;
                
                app.dibujarRect(x + pad, y + pad,
                    celdaSize - 2*pad, celdaSize - 2*pad,
                    Estilos.COLOR_PANEL_CARD);
                
                app.dibujarBorde(x + pad, y + pad,
                    celdaSize - 2*pad, celdaSize - 2*pad,
                    1f, Estilos.COLOR_BORDE_PANEL);
                
                ColorFlow color = grid[f][c].getColor();
                if (color != ColorFlow.VACIO) {
                    float r = celdaSize * 0.3f;
                    boolean esFijo = grid[f][c].isEsPuntoFijo();
                    
                    if (esFijo) {
                        
                        float cs = celdaSize * 0.65f;
                        float cx2 = x + (celdaSize - cs) / 2f;
                        float cy2 = y + (celdaSize - cs) / 2f;
                        app.dibujarRect(cx2, cy2, cs, cs, color.gdxColor);
                        
                    } else {
                        float ts = celdaSize * 0.45f;
                        float tx = x + (celdaSize - ts) / 2f;
                        float ty = y + (celdaSize - ts) / 2f;
                        app.dibujarRect(tx, ty, ts, ts, color.gdxColor);
                    }
                }
            }
        }
        
        app.dibujarBorde(gridOffsetX, gridOffsetY,
            tam * celdaSize, tam * celdaSize,
            2f, Estilos.COLOR_BORDE_PANEL);
    }
    
    private void dibujarHUD() {
        
        app.dibujarRect(0f, 560f, 800f, 40f, Estilos.COLOR_PANEL_CARD);
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, "NIVEL", 20f, 590f);
        app.fontMediana.setColor(Estilos.COLOR_ACENTO_CYAN);
        app.fontMediana.draw(app.batch, String.valueOf(juego.getNivelActual()), 20f, 574f);
        
        int tiempo = juego.getTiempoRestante();
        String tiempoStr = String.format("%02d:%02d", tiempo / 60, tiempo % 60);
        app.fontGrande.setColor(tiempo <= 20 ? Estilos.COLOR_ACENTO_ROJO : Estilos.COLOR_ACENTO_CYAN);
        GlyphLayout gt = new GlyphLayout(app.fontGrande, tiempoStr);
        app.fontGrande.draw(app.batch, tiempoStr, (800f - gt.width) / 2f, 592f);
        
        String vidas = "X".repeat(Math.max(0, juego.getVidas()));
        app.fontMediana.setColor(Estilos.COLOR_ACENTO_ROJO);
        GlyphLayout gv = new GlyphLayout(app.fontMediana, vidas);
        app.fontMediana.draw(app.batch, vidas, 800f - gv.width - 20f, 584f);
        
        app.dibujarRect(0f, 0f, 800f, 36f, Estilos.COLOR_PANEL_CARD);
        
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, "PASOS: " + juego.getPasos(), 20f, 24f);

        int ocupadas = juego.contarCeldasOcupadas();
        int total = juego.getTotalCeldas();
        app.fontPequena.setColor(
            ocupadas == total ? Estilos.COLOR_ACENTO_VERDE : Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, "CELDA: " + ocupadas + "/" + total, 130f, 24f);

        if (juego.flujosConectados() && ocupadas < total) {
            app.fontPequena.setColor(Estilos.COLOR_ACENTO_AMARILLO);
            GlyphLayout gh = new GlyphLayout(app.fontPequena, "Llena toda la cuadricula");
            app.fontPequena.draw(app.batch, "Llena toda la cuadricula",
                (800f - gh.width) / 2f, 55f);
        } else if (ocupadas == total && !juego.esCompleto()) {
            app.fontPequena.setColor(Estilos.COLOR_ACENTO_AMARILLO);
            GlyphLayout gh = new GlyphLayout(app.fontPequena, "Conecta cada par de puntos del mismo color");
            app.fontPequena.draw(app.batch, "Conecta cada par de puntos del mismo color",
                (800f - gh.width) / 2f, 55f);
        }
        
        app.fontPequena.setColor(Estilos.COLOR_ACENTO_ROJO);
        app.fontPequena.draw(app.batch, "FALLOS: " + juego.getFallos(), 200f, 24f);
        
        app.fontPequena.setColor(Estilos.COLOR_ACENTO_AMARILLO);
        String pts = "PTS: " + juego.getPuntuacion();
        GlyphLayout gp = new GlyphLayout(app.fontPequena, pts);
        app.fontPequena.draw(app.batch, pts, 800f - gp.width - 20f, 24f);
        
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_DISABLED);
        app.fontPequena.draw(app.batch, "[P] Pausar  [ESC] Salir", 340f, 24f);
    }

    private void dibujarPausa() {
        
        app.batch.setColor(0f, 0f, 0f, 0.6f);
        app.batch.draw(app.pixel, 0f, 0f, 800f, 600f);
        app.batch.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        
        app.fontGrande.setColor(Estilos.COLOR_ACENTO_CYAN);
        GlyphLayout gl = new GlyphLayout(app.fontGrande, "PAUSADO");
        app.fontGrande.draw(app.batch, "PAUSADO", (800f - gl.width) / 2f, 330f);
        
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        GlyphLayout gs = new GlyphLayout(app.fontPequena, "Presiona P para continuar");
        app.fontPequena.draw(app.batch, "Presiona P para continuar",
            (800f - gs.width) / 2f, 280f);
    }
    
    @Override
    public void dispose() {
        autosave.detener();
        juego.dispose();
    }
    
    @Override public void resize(int w, int h) { calcularGeometria(juego.getTamanoCuadricula()); }
    @Override public void pause() { if (!pausado) { juego.pausar(); pausado = true;  } }
    @Override public void resume() { if (pausado)  { juego.reanudar(); pausado = false; } }
    @Override public void hide() { Gdx.input.setInputProcessor(null); }
}
