package io.proyecto2.flowfree.datos;

import io.proyecto2.flowfree.datos.exceptions.AmistadException;
import io.proyecto2.flowfree.datos.exceptions.ArchivoCorruptoException;
import io.proyecto2.flowfree.datos.exceptions.UsuarioNoEncontradoException;
import io.proyecto2.flowfree.usuario.Usuario;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class GestorAmigos {

    private GestorAmigos() {}

    public static void enviarSolicitud(Usuario remitente, String destino) throws IOException, AmistadException {
        String yo = remitente.getNombreUsuario();
        String objetivo = destino == null ? "" : destino.trim();

        if (objetivo.isEmpty()) throw new AmistadException("Escribe un nombre de usuario.");
        if (objetivo.equalsIgnoreCase(yo)) throw new AmistadException("No puedes agregarte a ti mismo.");
        if (!GestorArchivoUsuario.existeUsuario(objetivo)) throw new AmistadException("Ese usuario no existe.");
        if (remitente.getAmigos().getAmigos().stream().anyMatch(a -> a.equalsIgnoreCase(objetivo))) {
            throw new AmistadException("Ya sois amigos.");
        }

        List<SolicitudAmistad> enviadas = leerSolicitudes(GestorArchivoUsuario.rutaSolicitudesEnviadas(yo));
        if (contieneRemitente(enviadas, objetivo) || contieneRemitente(leerSolicitudes(
                GestorArchivoUsuario.rutaSolicitudesEntrada(objetivo)), yo)) {
            throw new AmistadException("Ya hay una solicitud pendiente.");
        }

        long ahora = System.currentTimeMillis();
        List<SolicitudAmistad> bandejaDestino = leerSolicitudes(
            GestorArchivoUsuario.rutaSolicitudesEntrada(objetivo));
        bandejaDestino.add(new SolicitudAmistad(yo, ahora));
        escribirSolicitudes(GestorArchivoUsuario.rutaSolicitudesEntrada(objetivo), bandejaDestino);

        enviadas.add(new SolicitudAmistad(objetivo, ahora));
        escribirSolicitudes(GestorArchivoUsuario.rutaSolicitudesEnviadas(yo), enviadas);
    }

    public static void aceptarSolicitud(Usuario receptor, String remitente) throws IOException, AmistadException {
        String yo = receptor.getNombreUsuario();
        if (!quitarSolicitud(GestorArchivoUsuario.rutaSolicitudesEntrada(yo), remitente)) {
            throw new AmistadException("La solicitud ya no está disponible.");
        }
        quitarSolicitud(GestorArchivoUsuario.rutaSolicitudesEnviadas(remitente), yo);

        receptor.getAmigos().agregar(remitente);
        ArchivoUsuario.guardar(receptor);

        try {
            Usuario otro = ArchivoUsuario.cargar(remitente);
            otro.getAmigos().agregar(yo);
            ArchivoUsuario.guardar(otro);
        } catch (UsuarioNoEncontradoException e) {
            throw new AmistadException("El usuario ya no existe.");
        } catch (ArchivoCorruptoException e) {
            throw new AmistadException("No se pudo leer el perfil del usuario.");
        }
    }

    public static void rechazarSolicitud(Usuario receptor, String remitente) throws IOException {
        String yo = receptor.getNombreUsuario();
        quitarSolicitud(GestorArchivoUsuario.rutaSolicitudesEntrada(yo), remitente);
        quitarSolicitud(GestorArchivoUsuario.rutaSolicitudesEnviadas(remitente), yo);
    }

    public static void cancelarSolicitudEnviada(Usuario remitente, String destino) throws IOException {
        String yo = remitente.getNombreUsuario();
        quitarSolicitud(GestorArchivoUsuario.rutaSolicitudesEnviadas(yo), destino);
        quitarSolicitud(GestorArchivoUsuario.rutaSolicitudesEntrada(destino), yo);
    }

    public static void eliminarAmigo(Usuario usuario, String amigo) throws IOException, AmistadException {
        if (!usuario.getAmigos().getAmigos().stream().anyMatch(a -> a.equalsIgnoreCase(amigo))) {
            throw new AmistadException("Ese jugador no está en tu lista.");
        }
        usuario.getAmigos().eliminar(amigo);
        ArchivoUsuario.guardar(usuario);

        if (GestorArchivoUsuario.existeUsuario(amigo)) {
            try {
                Usuario otro = ArchivoUsuario.cargar(amigo);
                otro.getAmigos().eliminar(usuario.getNombreUsuario());
                ArchivoUsuario.guardar(otro);
            } catch (UsuarioNoEncontradoException | ArchivoCorruptoException ignored) {
                // El perfil remoto ya no está disponible; la eliminación local es suficiente.
            }
        }
    }

    public static void enviarDesafio(Usuario retador, String rival, int nivel) throws IOException, AmistadException {
        String yo = retador.getNombreUsuario();
        if (!retador.getAmigos().getAmigos().stream().anyMatch(a -> a.equalsIgnoreCase(rival))) {
            throw new AmistadException("Solo puedes desafiar a tus amigos.");
        }
        if (!GestorArchivoUsuario.existeUsuario(rival)) throw new AmistadException("Ese usuario no existe.");

        int nivelDesafio = nivel <= 0 ? retador.getNivelActual() : nivel;
        List<Desafio> bandeja = leerDesafios(GestorArchivoUsuario.rutaDesafiosEntrada(rival));
        bandeja.removeIf(d -> d.retador().equalsIgnoreCase(yo));
        bandeja.add(new Desafio(yo, nivelDesafio, retador.getPuntuacion(), System.currentTimeMillis()));
        escribirDesafios(GestorArchivoUsuario.rutaDesafiosEntrada(rival), bandeja);
    }

    public static void descartarDesafio(Usuario usuario, String retador) throws IOException {
        List<Desafio> bandeja = leerDesafios(GestorArchivoUsuario.rutaDesafiosEntrada(usuario.getNombreUsuario()));
        bandeja.removeIf(d -> d.retador().equalsIgnoreCase(retador));
        escribirDesafios(GestorArchivoUsuario.rutaDesafiosEntrada(usuario.getNombreUsuario()), bandeja);
    }

    public static List<SolicitudAmistad> solicitudesEntrada(String usuario) throws IOException {
        return leerSolicitudes(GestorArchivoUsuario.rutaSolicitudesEntrada(usuario));
    }

    public static List<SolicitudAmistad> solicitudesEnviadas(String usuario) throws IOException {
        return leerSolicitudes(GestorArchivoUsuario.rutaSolicitudesEnviadas(usuario));
    }

    public static List<Desafio> desafiosEntrada(String usuario) throws IOException {
        return leerDesafios(GestorArchivoUsuario.rutaDesafiosEntrada(usuario));
    }

    public static List<GestorRanking.EntradaRanking> rankingAmigos(Usuario usuario) throws IOException {
        Set<String> nombres = new HashSet<>();
        nombres.add(usuario.getNombreUsuario());
        for (String amigo : usuario.getAmigos().getAmigos()) {
            nombres.add(amigo);
        }

        List<GestorRanking.EntradaRanking> filtrado = new ArrayList<>();
        for (GestorRanking.EntradaRanking entrada : GestorRanking.leerTodos()) {
            if (nombres.stream().anyMatch(n -> n.equalsIgnoreCase(entrada.nombre()))) {
                filtrado.add(entrada);
            }
        }
        filtrado.sort((a, b) -> Integer.compare(b.puntuacion(), a.puntuacion()));
        return filtrado;
    }

    public static ResumenJugador resumenJugador(String nombreUsuario) throws IOException, UsuarioNoEncontradoException {
        GestorRanking.EntradaRanking ranking = GestorRanking.leerUsuario(nombreUsuario);
        Usuario perfil = ArchivoUsuario.cargar(nombreUsuario);
        int puntos = ranking != null ? ranking.puntuacion() : perfil.getPuntuacion();
        int nivel = ranking != null ? ranking.nivelAlcanzado() : perfil.getNivelActual();
        long tiempo = ranking != null ? ranking.tiempoTotalMs() : perfil.getEstadisticas().getTiempoTotalMs();
        return new ResumenJugador(
            nombreUsuario,
            perfil.getNombreCompleto(),
            puntos,
            nivel,
            perfil.getEstadisticas().getPartidasJugadas(),
            perfil.getEstadisticas().getMejorPuntuacion(),
            perfil.getEstadisticas().getRachaMejor(),
            tiempo
        );
    }

    public static int solicitudesPendientes(String usuario) throws IOException {
        return solicitudesEntrada(usuario).size();
    }

    private static boolean contieneRemitente(List<SolicitudAmistad> lista, String nombre) {
        return lista.stream().anyMatch(s -> s.remitente().equalsIgnoreCase(nombre));
    }

    private static boolean quitarSolicitud(Path ruta, String remitente) throws IOException {
        List<SolicitudAmistad> lista = leerSolicitudes(ruta);
        boolean removido = lista.removeIf(s -> s.remitente().equalsIgnoreCase(remitente));
        if (removido) escribirSolicitudes(ruta, lista);
        return removido;
    }

    private static List<SolicitudAmistad> leerSolicitudes(Path ruta) throws IOException {
        List<SolicitudAmistad> lista = new ArrayList<>();
        if (!Files.exists(ruta) || Files.size(ruta) == 0) return lista;
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(ruta.toFile())))) {
            int total = in.readInt();
            for (int i = 0; i < total; i++) {
                lista.add(new SolicitudAmistad(in.readUTF(), in.readLong()));
            }
        }
        return lista;
    }

    private static void escribirSolicitudes(Path ruta, List<SolicitudAmistad> lista) throws IOException {
        Files.createDirectories(ruta.getParent());
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(ruta.toFile())))) {
            out.writeInt(lista.size());
            for (SolicitudAmistad s : lista) {
                out.writeUTF(s.remitente());
                out.writeLong(s.timestamp());
            }
        }
    }

    private static List<Desafio> leerDesafios(Path ruta) throws IOException {
        List<Desafio> lista = new ArrayList<>();
        if (!Files.exists(ruta) || Files.size(ruta) == 0) return lista;
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(ruta.toFile())))) {
            int total = in.readInt();
            for (int i = 0; i < total; i++) {
                lista.add(new Desafio(in.readUTF(), in.readInt(), in.readInt(), in.readLong()));
            }
        }
        return lista;
    }

    private static void escribirDesafios(Path ruta, List<Desafio> lista) throws IOException {
        Files.createDirectories(ruta.getParent());
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(ruta.toFile())))) {
            out.writeInt(lista.size());
            for (Desafio d : lista) {
                out.writeUTF(d.retador());
                out.writeInt(d.nivel());
                out.writeInt(d.puntosRetador());
                out.writeLong(d.timestamp());
            }
        }
    }

    public record ResumenJugador(
        String nombreUsuario,
        String nombreCompleto,
        int puntuacion,
        int nivel,
        int partidas,
        int mejorPuntuacion,
        int rachaMejor,
        long tiempoTotalMs
    ) {
        public String tiempoFormateado() {
            long seg = tiempoTotalMs / 1000;
            return String.format("%02d:%02d", seg / 60, seg % 60);
        }
    }
}
