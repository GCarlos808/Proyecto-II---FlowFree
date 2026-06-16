package io.proyecto2.flowfree.usuario;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListaAmigos implements Guardable, Serializable {

    private static final long serialVersionUID = 1L;

    private final List<String> amigos;

    public ListaAmigos() {
        this.amigos = new ArrayList<>();
    }

    public void agregar(String nombreUsuario) {
        if (!nombreUsuario.isBlank() && !amigos.contains(nombreUsuario)) {
            amigos.add(nombreUsuario);
        }
    }

    public void eliminar(String nombreUsuario) {
        amigos.remove(nombreUsuario);
    }

    public List<String> getAmigos() {
        return Collections.unmodifiableList(amigos);
    }

    @Override
    public void guardar(DataOutputStream out) throws IOException {
        out.writeInt(amigos.size());
        for (String amigo : amigos) {
            out.writeUTF(amigo);
        }
    }

    @Override
    public void cargar(DataInputStream in) throws IOException {
        amigos.clear();
        int total = in.readInt();
        for (int i = 0; i < total; i++) {
            amigos.add(in.readUTF());
        }
    }
}
