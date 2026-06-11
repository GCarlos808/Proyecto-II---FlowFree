package io.proyecto2.flowfree.usuario;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public interface Guardable {
    void guardar(DataOutputStream out) throws IOException;
    void cargar(DataInputStream in)   throws IOException;
}
