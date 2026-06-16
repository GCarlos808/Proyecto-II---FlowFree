package io.proyecto2.flowfree.GUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import io.proyecto2.flowfree.Main;
import io.proyecto2.flowfree.constantes.Estilos;
import io.proyecto2.flowfree.datos.GestorUsuarios;
import io.proyecto2.flowfree.datos.exceptions.*;
import io.proyecto2.flowfree.usuario.Usuario;

import java.util.Collections;
import java.util.List;

public class PantallaRegistro implements Screen {

    private final Main app;
    private StringBuilder campoUsuario = new StringBuilder();
    private StringBuilder campoNombre  = new StringBuilder();
    private StringBuilder campoPass    = new StringBuilder();
    private StringBuilder campoConf    = new StringBuilder();
    
    private enum Campo { USUARIO, NOMBRE, PASS, CONF, NINGUNO }
    private Campo campoActivo = Campo.NINGUNO;
    private boolean mostrarPass = false;
    private String mensajeError = "";
    private float tiempoError  = 0f;
    private List<String> requisitos = Collections.emptyList();
    
    private static final float CX = 220f;
    private static final float FORM_W = 360f;
    private static final float IN_H = Estilos.ALTO_INPUT;
    private static final float BTN_H = Estilos.ALTO_BOTON;
    
    private static final float Y_TIT = 545f;
    private static final float Y_LU = 480f; private static final float Y_IU = 455f;
    private static final float Y_LN = 405f; private static final float Y_IN = 380f;
    private static final float Y_LP = 330f; private static final float Y_IP = 305f;
    private static final float Y_LC = 255f; private static final float Y_IC = 230f;
    private static final float Y_BTN1 = 140f;
    private static final float Y_BTN2 = 82f;
    
    public PantallaRegistro(Main app) { this.app = app; }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int key) {
                if (key == Input.Keys.TAB)   avanzar();
                else if (key == Input.Keys.ENTER) intentarRegistro();
                else if (key == Input.Keys.BACKSPACE) borrar();
                return true;
            }
            
            @Override
            public boolean keyTyped(char c) {
                if (c < 32 || c == 127) return false;
                switch (campoActivo) {
                    case USUARIO: if (campoUsuario.length() < 20) campoUsuario.append(c); break;
                    case NOMBRE:  if (campoNombre.length() < 40)  campoNombre.append(c);  break;
                    case PASS:
                        if (campoPass.length() < 30) {
                            campoPass.append(c);
                            requisitos = GestorUsuarios.getInstance()
                                .getRequisitosContrasena(campoPass.toString());
                        }
                        break;
                    case CONF: if (campoConf.length() < 30) campoConf.append(c); break;
                    default: break;
                }
                return true;
            }
            @Override
            public boolean touchDown(int sx, int sy, int p, int b) {
                manejarClic(sx, Gdx.graphics.getHeight() - sy); return true;
            }
        });
    }
    
    private void avanzar() {
        Campo[] orden = { Campo.USUARIO, Campo.NOMBRE, Campo.PASS, Campo.CONF };
        for (int i = 0; i < orden.length; i++)
            if (campoActivo == orden[i]) { campoActivo = orden[(i + 1) % orden.length]; return; }
        campoActivo = Campo.USUARIO;
    }
    
    private void borrar() {
        switch (campoActivo) {
            case USUARIO: if (campoUsuario.length() > 0) campoUsuario.deleteCharAt(campoUsuario.length()-1); break;
            case NOMBRE:  if (campoNombre.length()  > 0) campoNombre.deleteCharAt(campoNombre.length()-1);   break;
            case PASS:
                if (campoPass.length() > 0) {
                    campoPass.deleteCharAt(campoPass.length()-1);
                    requisitos = GestorUsuarios.getInstance().getRequisitosContrasena(campoPass.toString());
                }
                break;
            case CONF: if (campoConf.length() > 0) campoConf.deleteCharAt(campoConf.length()-1); break;
            default: break;
        }
    }
    
    private void manejarClic(float x, float y) {
        if (hit(x,y,CX,Y_IU,FORM_W,IN_H)) { campoActivo=Campo.USUARIO; return; }
        if (hit(x,y,CX,Y_IN,FORM_W,IN_H)) { campoActivo=Campo.NOMBRE; return; }
        if (hit(x,y,CX,Y_IP,FORM_W,IN_H)) { campoActivo=Campo.PASS; return; }
        if (hit(x,y,CX,Y_IC,FORM_W,IN_H)) { campoActivo=Campo.CONF; return; }
        if (hit(x,y,CX+FORM_W-36f,Y_IP,32f,IN_H)) { mostrarPass = !mostrarPass; return; }
        if (hit(x,y,CX,Y_BTN1,FORM_W,BTN_H)) { intentarRegistro(); return; }
        if (hit(x,y,CX,Y_BTN2,FORM_W,BTN_H)) { app.cambiarPantalla(new PantallaLogin(app)); return; }
        campoActivo = Campo.NINGUNO;
    }
    
    @Override
    public void render(float delta) {
        if (tiempoError > 0) { tiempoError -= delta; if (tiempoError <= 0) mensajeError = ""; }
        
        Gdx.gl.glClearColor(Estilos.COLOR_FONDO_OSCURO.r, Estilos.COLOR_FONDO_OSCURO.g, Estilos.COLOR_FONDO_OSCURO.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        app.batch.begin();
        app.dibujarRect(CX-20, 60f, FORM_W+40, 500f, Estilos.COLOR_PANEL_CARD);
        app.dibujarBorde(CX-20, 60f, FORM_W+40, 500f, 1f, Estilos.COLOR_BORDE_PANEL);
        
        app.fontGrande.setColor(Estilos.COLOR_ACENTO_CYAN);
        GlyphLayout gt = new GlyphLayout(app.fontGrande, "CREAR CUENTA");
        app.fontGrande.draw(app.batch, "CREAR CUENTA", (800f-gt.width)/2f, Y_TIT);
        
        campo("Nombre de usuario", campoUsuario.toString(), Y_LU, Y_IU, campoActivo==Campo.USUARIO, false);
        campo("Nombre completo", campoNombre.toString(),  Y_LN, Y_IN, campoActivo==Campo.NOMBRE,  false);
        campo("Contraseña", campoPass.toString(), Y_LP, Y_IP, campoActivo==Campo.PASS, !mostrarPass);
        campo("Confirmar contraseña", campoConf.toString(), Y_LC, Y_IC, campoActivo==Campo.CONF, !mostrarPass);
        
        app.dibujarRect(CX+FORM_W-36f, Y_IP, 32f, IN_H, Estilos.COLOR_PANEL_INPUT);
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, mostrarPass?"O":"Ø", CX+FORM_W-28f, Y_IP+IN_H/2f+6f);
        
        if (campoPass.length() > 0) {
            String[] todos = {"Mínimo 8 caracteres","Al menos una mayúscula","Al menos un número","Al menos un símbolo"};
            float ry = Y_IC - 8f;
            for (String req : todos) {
                boolean falla = requisitos.contains(req);
                app.fontPequena.setColor(falla ? Estilos.COLOR_ACENTO_ROJO : Estilos.COLOR_ACENTO_VERDE);
                app.fontPequena.draw(app.batch, (falla?"✗ ":"✓ ")+req, CX, ry);
                ry -= 15f;
            }
        }
        
        boton("REGISTRARSE", CX, Y_BTN1, true);
        boton("VOLVER", CX, Y_BTN2, false);

        if (!mensajeError.isEmpty()) {
            app.fontPequena.setColor(Estilos.COLOR_ACENTO_ROJO);
            GlyphLayout ge = new GlyphLayout(app.fontPequena, mensajeError);
            app.fontPequena.draw(app.batch, mensajeError, (800f-ge.width)/2f, 68f);
        }
        app.batch.end();
    }
    
    private void campo(String lbl, String val, float yL, float yI, boolean act, boolean ocultar) {
        app.fontPequena.setColor(act ? Estilos.COLOR_ACENTO_CYAN : Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, lbl, CX, yL+16f);
        app.dibujarRect(CX, yI, FORM_W, IN_H, Estilos.COLOR_PANEL_INPUT);
        app.dibujarBorde(CX, yI, FORM_W, IN_H, Estilos.GROSOR_BORDE,
            act ? Estilos.COLOR_BORDE_ACTIVO : Estilos.COLOR_BORDE_PANEL);
        String txt = ocultar ? "*".repeat(val.length()) : val;
        if (act && (System.currentTimeMillis()/500)%2==0) txt+="|";
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_CLARO);
        app.fontPequena.draw(app.batch, txt, CX+Estilos.PADDING_INPUT, yI+IN_H/2f+6f);
    }
    
    private void boton(String txt, float x, float y, boolean prim) {
        float mx=Gdx.input.getX(), my=Gdx.graphics.getHeight()-Gdx.input.getY();
        boolean hov=hit(mx,my,x,y,FORM_W,BTN_H);
        com.badlogic.gdx.graphics.Color bg = prim ? (hov?Estilos.COLOR_ACENTO_CYAN_DIM:Estilos.COLOR_PANEL_INPUT) : (hov?Estilos.COLOR_HOVER:Estilos.COLOR_PANEL_CARD);
        app.dibujarRect(x,y,FORM_W,BTN_H,bg);
        app.dibujarBorde(x,y,FORM_W,BTN_H,Estilos.GROSOR_BORDE, prim?Estilos.COLOR_ACENTO_CYAN:Estilos.COLOR_BORDE_PANEL);
        app.fontMediana.setColor(prim?Estilos.COLOR_ACENTO_CYAN:Estilos.COLOR_TEXTO_GRIS);
        GlyphLayout gl=new GlyphLayout(app.fontMediana,txt);
        app.fontMediana.draw(app.batch,txt,x+(FORM_W-gl.width)/2f,y+BTN_H/2f+gl.height/2f);
    }
    
    private void intentarRegistro() {
        String u=campoUsuario.toString().trim(), n=campoNombre.toString().trim();
        String p=campoPass.toString(), c=campoConf.toString();
        if (u.isEmpty()||n.isEmpty()||p.isEmpty()||c.isEmpty()) { error("Completa todos los campos."); return; }
        if (!p.equals(c)) { error("Las contraseñas no coinciden."); return; }
        if (!requisitos.isEmpty()) { error("La contraseña no cumple los requisitos."); return; }
        try {
            Usuario usr = GestorUsuarios.getInstance().registrar(u, p, n);
            app.cambiarPantalla(new PantallaMenu(app, usr));
        } catch (UsuarioYaExisteException e) { error("El usuario ya está en uso."); }
          catch (ContraseñaInvalidaException e) { error("Contraseña inválida."); }
          catch (Exception e) { error("Error al guardar. Intenta de nuevo."); }
    }

    private void error(String msg) { mensajeError=msg; tiempoError=4f; }
    private boolean hit(float px,float py,float rx,float ry,float rw,float rh) {
        return px>=rx&&px<=rx+rw&&py>=ry&&py<=ry+rh;
    }
    
    @Override public void resize(int w,int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { Gdx.input.setInputProcessor(null); }
    @Override public void dispose() {}
}
