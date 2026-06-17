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

public class PantallaLogin implements Screen {

    private final Main app;
    
    private StringBuilder campoUsuario    = new StringBuilder();
    private StringBuilder campoContrasena = new StringBuilder();
    private enum Campo { USUARIO, CONTRASENA, NINGUNO }
    private Campo campoActivo = Campo.NINGUNO;
    
    private String  mensajeError = "";
    private float   tiempoError  = 0f;
    private boolean mostrarPass  = false;
    
    private static final float CX = 220f;
    private static final float FORM_W = 360f;
    private static final float IN_H = Estilos.ALTO_INPUT;
    private static final float BTN_H = Estilos.ALTO_BOTON;
    
    private static final float Y_TITULO  = 500f;
    private static final float Y_LBL_U = 400f;
    private static final float Y_INP_U = 375f;
    private static final float Y_LBL_P = 325f;
    private static final float Y_INP_P = 300f;
    private static final float Y_BTN_ENT = 240f;
    private static final float Y_BTN_REG = 180f;
    private static final float BTN_PASS_W = 36f;
    private static final float BTN_PASS_X = CX + FORM_W - BTN_PASS_W;
    private static final float INPUT_W = FORM_W - BTN_PASS_W;
    
    public PantallaLogin(Main app) { this.app = app; }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int key) {
                if (key == Input.Keys.TAB)
                    campoActivo = campoActivo == Campo.USUARIO ? Campo.CONTRASENA : Campo.USUARIO;
                
                else if (key == Input.Keys.ENTER) intentarLogin();
                else if (key == Input.Keys.BACKSPACE) borrar();
                return true;
            }
            @Override
            public boolean keyTyped(char c) {
                if (c < 32 || c == 127) return false;
                if (campoActivo == Campo.USUARIO && campoUsuario.length() < 20)
                    campoUsuario.append(c);
                else if (campoActivo == Campo.CONTRASENA && campoContrasena.length() < 30)
                    campoContrasena.append(c);
                return true;
            }
            @Override
            public boolean touchDown(int sx, int sy, int p, int b) {
                manejarClic(sx, Gdx.graphics.getHeight() - sy);
                return true;
            }
        });
    }
    
    private void manejarClic(float x, float y) {
        if (hit(x, y, CX, Y_INP_U, FORM_W, IN_H))  { campoActivo = Campo.USUARIO;    return; }
        if (hit(x, y, BTN_PASS_X, Y_INP_P, BTN_PASS_W, IN_H)) { mostrarPass = !mostrarPass; return; }
        if (hit(x, y, CX, Y_INP_P, INPUT_W, IN_H))  { campoActivo = Campo.CONTRASENA; return; }
        if (hit(x, y, CX, Y_BTN_ENT, FORM_W, BTN_H)) { intentarLogin(); return; }
        if (hit(x, y, CX, Y_BTN_REG, FORM_W, BTN_H)) { app.cambiarPantalla(new PantallaRegistro(app)); return; }
        campoActivo = Campo.NINGUNO;
    }
    
    private void borrar() {
        if (campoActivo == Campo.USUARIO && campoUsuario.length() > 0)
            campoUsuario.deleteCharAt(campoUsuario.length() - 1);
        else if (campoActivo == Campo.CONTRASENA && campoContrasena.length() > 0)
            campoContrasena.deleteCharAt(campoContrasena.length() - 1);
    }
    
    @Override
    public void render(float delta) {
        if (tiempoError > 0) { tiempoError -= delta; if (tiempoError <= 0) mensajeError = ""; }
        
        Gdx.gl.glClearColor(Estilos.COLOR_FONDO_OSCURO.r, Estilos.COLOR_FONDO_OSCURO.g, Estilos.COLOR_FONDO_OSCURO.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        app.batch.begin();
        app.dibujarRect(CX - 20, 160f, FORM_W + 40, 330f, Estilos.COLOR_PANEL_CARD);
        app.dibujarBorde(CX - 20, 160f, FORM_W + 40, 330f, 1f, Estilos.COLOR_BORDE_PANEL);
        
        app.fontGrande.setColor(Estilos.COLOR_ACENTO_CYAN);
        GlyphLayout gt = new GlyphLayout(app.fontGrande, "FLOW FREE");
        app.fontGrande.draw(app.batch, "FLOW FREE", (800f - gt.width) / 2f, Y_TITULO);
        
        app.fontPequena.setColor(Estilos.COLOR_TEXTO_GRIS);
        GlyphLayout gs = new GlyphLayout(app.fontPequena, "Inicia sesión para continuar");
        app.fontPequena.draw(app.batch, "Inicia sesión para continuar", (800f - gs.width) / 2f, Y_TITULO - 42f);
        
        campo("Usuario",    campoUsuario.toString(),    Y_LBL_U, Y_INP_U, campoActivo == Campo.USUARIO, false);
        campo("Contraseña", campoContrasena.toString(), Y_LBL_P, Y_INP_P, campoActivo == Campo.CONTRASENA, !mostrarPass);
        dibujarTogglePass(Y_INP_P);
        
        boton("ENTRAR",     CX, Y_BTN_ENT, true);
        boton("REGISTRARSE", CX, Y_BTN_REG, false);
        
        if (!mensajeError.isEmpty()) {
            app.fontPequena.setColor(Estilos.COLOR_ACENTO_ROJO);
            GlyphLayout ge = new GlyphLayout(app.fontPequena, mensajeError);
            app.fontPequena.draw(app.batch, mensajeError, (800f - ge.width) / 2f, 172f);
        }
        app.batch.end();
    }
    
    private void campo(String lbl, String valor, float yL, float yI, boolean activo, boolean ocultar) {
        float labelY = yI + IN_H + 12f;
        app.fontPequena.setColor(activo ? Estilos.COLOR_ACENTO_CYAN : Estilos.COLOR_TEXTO_GRIS);
        app.fontPequena.draw(app.batch, lbl, CX, labelY);
        app.dibujarRect(CX, yI, FORM_W, IN_H, Estilos.COLOR_PANEL_INPUT);
        app.dibujarBorde(CX, yI, FORM_W, IN_H, Estilos.GROSOR_BORDE, activo ? Estilos.COLOR_BORDE_ACTIVO : Estilos.COLOR_BORDE_PANEL);
        String txt = ocultar ? "*".repeat(valor.length()) : valor;
        if (activo && (System.currentTimeMillis() / 500) % 2 == 0) txt += "|";
        if (!txt.isEmpty()) {
            app.fontPequena.setColor(Estilos.COLOR_TEXTO_CLARO);
            app.fontPequena.draw(app.batch, txt, CX + Estilos.PADDING_INPUT, yI + IN_H / 2f + 6f);
        }
    }
    
    private void dibujarTogglePass(float inputY) {
        float mx = Gdx.input.getX(), my = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean hover = hit(mx, my, BTN_PASS_X, inputY, BTN_PASS_W, IN_H);
        app.dibujarRect(BTN_PASS_X, inputY, BTN_PASS_W, IN_H,
            hover ? Estilos.COLOR_HOVER : Estilos.COLOR_PANEL_INPUT);
        app.dibujarBorde(BTN_PASS_X, inputY, BTN_PASS_W, IN_H, Estilos.GROSOR_BORDE,
            mostrarPass ? Estilos.COLOR_BORDE_ACTIVO : Estilos.COLOR_BORDE_PANEL);
        app.fontPequena.setColor(mostrarPass ? Estilos.COLOR_ACENTO_CYAN : Estilos.COLOR_TEXTO_GRIS);
        String icono = mostrarPass ? "Aa" : "**";
        GlyphLayout gl = new GlyphLayout(app.fontPequena, icono);
        app.fontPequena.draw(app.batch, icono,
            BTN_PASS_X + (BTN_PASS_W - gl.width) / 2f, inputY + IN_H / 2f + 6f);
    }
    
    private void boton(String txt, float x, float y, boolean primario) {
        float mx = Gdx.input.getX(), my = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean hover = hit(mx, my, x, y, FORM_W, BTN_H);
        com.badlogic.gdx.graphics.Color fondo = primario ? (hover ? Estilos.COLOR_ACENTO_CYAN_DIM : Estilos.COLOR_PANEL_INPUT) : (hover ? Estilos.COLOR_HOVER : Estilos.COLOR_PANEL_CARD);
        app.dibujarRect(x, y, FORM_W, BTN_H, fondo);
        app.dibujarBorde(x, y, FORM_W, BTN_H, Estilos.GROSOR_BORDE, primario ? Estilos.COLOR_ACENTO_CYAN : Estilos.COLOR_BORDE_PANEL);
        app.fontMediana.setColor(primario ? Estilos.COLOR_ACENTO_CYAN : Estilos.COLOR_TEXTO_GRIS);
        GlyphLayout gl = new GlyphLayout(app.fontMediana, txt);
        app.fontMediana.draw(app.batch, txt, x + (FORM_W - gl.width) / 2f, y + BTN_H / 2f + gl.height / 2f);
    }
    
    private void intentarLogin() {
        String u = campoUsuario.toString().trim();
        String p = campoContrasena.toString();
        if (u.isEmpty() || p.isEmpty()) { error("Completa todos los campos."); return; }
        try {
            Usuario usr = GestorUsuarios.getInstance().iniciarSesion(u, p);
            app.aplicarVolumenUsuario(usr);
            app.cambiarPantalla(new PantallaMenu(app, usr));
            
        } catch (UsuarioNoEncontradoException e)  { error("Usuario no encontrado."); }
          catch (ContraseñaIncorrectaException e)  { error("Contraseña incorrecta."); }
          catch (ArchivoCorruptoException e)       { error("Error al leer datos."); }
    }

    private void error(String msg) { mensajeError = msg; tiempoError = 4f; }
    private boolean hit(float px, float py, float rx, float ry, float rw, float rh) {
        return px >= rx && px <= rx + rw && py >= ry && py <= ry + rh;
    }

    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { Gdx.input.setInputProcessor(null); }
    @Override public void dispose() {}
}
