package io.proyecto2.flowfree.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import io.proyecto2.flowfree.Main;
import io.proyecto2.flowfree.constantes.Estilos;
import io.proyecto2.flowfree.datos.GestorUsuarios;
import io.proyecto2.flowfree.usuario.Usuario;

public class PantallaMenu implements Screen {

    private final Main app;
    private final Usuario usuario;
    
    private static final float BTN_W = 300f;
    private static final float BTN_H = Estilos.ALTO_BOTON;
    private static final float BTN_X = (800f - BTN_W) / 2f;
    private static final float Y_JUGAR = 310f;
    private static final float Y_PERFIL = 248f;
    private static final float Y_RANKING = 186f;
    private static final float Y_SALIR = 110f;
    
    
    public PantallaMenu(Main app, Usuario usuario) {
        this.app     = app;
        this.usuario = usuario;
    }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int sx, int sy, int p, int b) {
                manejarClic(sx, Gdx.graphics.getHeight() - sy); return true;
            }
        });
    }
    
    private void manejarClic(float x, float y) {
        if (hit(x,y,BTN_X,Y_JUGAR,  BTN_W,BTN_H)) { app.cambiarPantalla(new PantallaMapa(app,usuario)); return; }
        if (hit(x,y,BTN_X,Y_PERFIL, BTN_W,BTN_H)) { Gdx.app.log("Menu","Perfil — pendiente"); return; }
        if (hit(x,y,BTN_X,Y_RANKING,BTN_W,BTN_H)) { Gdx.app.log("Menu","Ranking — pendiente"); return; }
        if (hit(x,y,BTN_X,Y_SALIR,  BTN_W,BTN_H)) {
            GestorUsuarios.getInstance().cerrarSesion();
            
            app.cambiarPantalla(new PantallaLogin(app));
        }
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(Estilos.COLOR_FONDO_OSCURO.r, Estilos.COLOR_FONDO_OSCURO.g, Estilos.COLOR_FONDO_OSCURO.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        app.batch.begin();
        
        app.fontGrande.setColor(Estilos.COLOR_ACENTO_CYAN);
        GlyphLayout gt = new GlyphLayout(app.fontGrande, "FLOW FREE");
        app.fontGrande.draw(app.batch, "FLOW FREE", (800f-gt.width)/2f, 545f);
        
        String saludo = "Bienvenido, " + usuario.getNombreCompleto();
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_CLARO);
        GlyphLayout gs = new GlyphLayout(app.fontPequena, saludo);
        app.fontPequena.draw(app.batch, saludo, (800f-gs.width)/2f, 500f);
        
        float px = (800f-500f)/2f;
        app.dibujarRect(px, 388f, 500f, 68f, Estilos.COLOR_PANEL_CARD);
        app.dibujarBorde(px, 388f, 500f, 68f, 1f, Estilos.COLOR_BORDE_PANEL);
        stat(px+20f,  430f, "NIVEL",    String.valueOf(usuario.getNivelActual()));
        stat(px+130f, 430f, "PUNTOS",   String.valueOf(usuario.getPuntuacion()));
        stat(px+280f, 430f, "VIDAS",    String.valueOf(usuario.getVidasRestantes()));
        stat(px+380f, 430f, "PARTIDAS", String.valueOf(usuario.getEstadisticas().getPartidasJugadas()));
        
        boton("JUGAR", BTN_X, Y_JUGAR,   true);
        boton("PERFIL", BTN_X, Y_PERFIL,  false);
        boton("RANKING", BTN_X, Y_RANKING, false);
        boton("CERRAR SESIÓN", BTN_X, Y_SALIR,   false);
        
        app.batch.end();
    }
    
    private void stat(float x, float y, String lbl, String val) {
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, lbl, x, y);
        app.fontMediana.setColor(Estilos.COLOR_ACENTO_CYAN);
        app.fontMediana.draw(app.batch, val, x, y-20f);
    }
    
    private void boton(String txt, float x, float y, boolean prim) {
        float mx=Gdx.input.getX(), my=Gdx.graphics.getHeight()-Gdx.input.getY();
        boolean hov=hit(mx,my,x,y,BTN_W,BTN_H);
        com.badlogic.gdx.graphics.Color bg = prim ? (hov?Estilos.COLOR_ACENTO_CYAN_DIM:Estilos.COLOR_PANEL_INPUT) : (hov?Estilos.COLOR_HOVER:Estilos.COLOR_PANEL_CARD);
        app.dibujarRect(x,y,BTN_W,BTN_H,bg);
        app.dibujarBorde(x,y,BTN_W,BTN_H,Estilos.GROSOR_BORDE, prim?Estilos.COLOR_ACENTO_CYAN:Estilos.COLOR_BORDE_PANEL);
        app.fontMediana.setColor(prim?Estilos.COLOR_ACENTO_CYAN:Estilos.COLOR_TEXTO_GRIS);
        GlyphLayout gl=new GlyphLayout(app.fontMediana,txt);
        app.fontMediana.draw(app.batch,txt,x+(BTN_W-gl.width)/2f,y+BTN_H/2f+gl.height/2f);
    }
    
    private boolean hit(float px,float py,float rx,float ry,float rw,float rh) {
        
        return px>=rx&&px<=rx+rw&&py>=ry&&py<=ry+rh;
    }
    
    @Override public void resize(int w,int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { Gdx.input.setInputProcessor(null); }
    @Override public void dispose() {}
}
