package io.proyecto2.flowfree.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import io.proyecto2.flowfree.Main;
import io.proyecto2.flowfree.constantes.Constantes;
import io.proyecto2.flowfree.constantes.Estilos;
import io.proyecto2.flowfree.usuario.Usuario;

public class PantallaMapa implements Screen {

    private final Main app;
    private final Usuario usuario;
    
    private static final String[] NOMBRES = {"Inicio","Calentando","Intermedio","Avanzado","Maestro"};
    private static final String[] GRIDS = {"5x5","6x6","7x7","8x8","9x9"};

    private static final float CW=170f, CH=100f, GAP=20f;
    
    private static final float[] X1 = {
        (800f-3*CW-2*GAP)/2f,
        (800f-3*CW-2*GAP)/2f+CW+GAP,
        (800f-3*CW-2*GAP)/2f+2*(CW+GAP)
    };
    private static final float Y1 = 285f;
    private static final float[] X2 = {
        (800f-2*CW-GAP)/2f,
        (800f-2*CW-GAP)/2f+CW+GAP
    };
    
    private static final float Y2 = 165f;
    
    public PantallaMapa(Main app, Usuario usuario) {
        this.app=app; this.usuario=usuario;
    }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int sx,int sy,int p,int b) {
                manejarClic(sx, Gdx.graphics.getHeight()-sy); return true;
            }
        });
    }
    
    private void manejarClic(float x, float y) {
        
        if (hit(x,y,30f,20f,120f,36f)) { app.cambiarPantalla(new PantallaMenu(app,usuario)); return; }
        int nDes = usuario.getNivelActual();
        for (int i=0;i<3;i++) {
            if (hit(x,y,X1[i],Y1,CW,CH) && (i+1)<=nDes) { iniciar(i+1); return; }
        }
        for (int i=0;i<2;i++) {
            if (hit(x,y,X2[i],Y2,CW,CH) && (i+4)<=nDes) { iniciar(i+4); return; }
        }
    }
    
    private void iniciar(int n) {
        app.cambiarPantalla(new PantallaJuego(app, usuario, n));
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(Estilos.COLOR_FONDO_OSCURO.r,Estilos.COLOR_FONDO_OSCURO.g, Estilos.COLOR_FONDO_OSCURO.b,1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        app.batch.begin();
        
        app.fontGrande.setColor(Estilos.COLOR_ACENTO_CYAN);
        GlyphLayout gt=new GlyphLayout(app.fontGrande,"SELECCIONA NIVEL");
        app.fontGrande.draw(app.batch,"SELECCIONA NIVEL",(800f-gt.width)/2f,530f);
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        String sub="Nivel actual: "+usuario.getNivelActual()+" / "+Constantes.NIVEL_MAX;
        GlyphLayout gs=new GlyphLayout(app.fontPequena,sub);
        app.fontPequena.draw(app.batch,sub,(800f-gs.width)/2f,495f);
        
        for (int i=0;i<3;i++) tarjeta(i+1, X1[i], Y1);
        for (int i=0;i<2;i++) tarjeta(i+4, X2[i], Y2);
        
        float mx=Gdx.input.getX(), my=Gdx.graphics.getHeight()-Gdx.input.getY();
        boolean hov=hit(mx,my,30f,20f,120f,36f);
        app.dibujarRect(30f,20f,120f,36f,hov?Estilos.COLOR_HOVER:Estilos.COLOR_PANEL_CARD);
        app.dibujarBorde(30f,20f,120f,36f,1f,Estilos.COLOR_BORDE_PANEL);
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch,"< VOLVER",46f,44f);
        app.batch.end();
    }
    
    private void tarjeta(int num, float x, float y) {
        int nDes=usuario.getNivelActual();
        boolean des=num<=nDes, esAct=num==nDes;
        float mx=Gdx.input.getX(), my=Gdx.graphics.getHeight()-Gdx.input.getY();
        boolean hov=des&&hit(mx,my,x,y,CW,CH);
        
        com.badlogic.gdx.graphics.Color bg = des ? (hov?Estilos.COLOR_HOVER:Estilos.COLOR_PANEL_CARD) : new com.badlogic.gdx.graphics.Color(0.08f,0.08f,0.12f,1f);
        app.dibujarRect(x,y,CW,CH,bg);
        app.dibujarBorde(x,y,CW,CH,esAct?2f:1f, esAct?Estilos.COLOR_ACENTO_CYAN:
            (des?Estilos.COLOR_BORDE_PANEL: new com.badlogic.gdx.graphics.Color(0.18f,0.18f,0.22f,1f)));
        
        if (des) {
            app.fontGrande.setColor(esAct?Estilos.COLOR_ACENTO_CYAN:Estilos.COLOR_TEXTO_CLARO);
            app.fontGrande.draw(app.batch, String.valueOf(num), x+14f, y+CH-10f);
            app.fontPequena.setColor(esAct?Estilos.COLOR_ACENTO_CYAN:Estilos.COLOR_TEXTO_GRIS);
            app.fontPequena.draw(app.batch, NOMBRES[num-1], x+14f, y+50f);
            app.fontPequena.setColor(Estilos.COLOR_TEXTO_DISABLED);
            app.fontPequena.draw(app.batch, GRIDS[num-1], x+14f, y+32f);
            
        } else {
            app.fontGrande.setColor(Estilos.COLOR_TEXTO_DISABLED);
            GlyphLayout gl=new GlyphLayout(app.fontGrande,"□");
            app.fontGrande.draw(app.batch,"□",x+(CW-gl.width)/2f,y+CH/2f+15f);
            app.fontPequena.setColor(Estilos.COLOR_TEXTO_DISABLED);
            GlyphLayout gb=new GlyphLayout(app.fontPequena,"BLOQUEADO");
            app.fontPequena.draw(app.batch,"BLOQUEADO",x+(CW-gb.width)/2f,y+26f);
        }
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
