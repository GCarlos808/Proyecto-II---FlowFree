package io.proyecto2.flowfree.datos.exceptions;

import java.io.IOException;


public class ArchivoCorruptoException extends IOException {
    public ArchivoCorruptoException(String msg, Throwable cause) { super(msg, cause); }
}