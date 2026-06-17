package io.proyecto2.flowfree.util;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class GestorAvatares {

    public static final String[] IDS = {"default", "space", "retro", "neon"};
    private static final int SIZE = 64;

    private static final Map<String, Texture> cache = new HashMap<>();

    private GestorAvatares() {}

    public static String normalizar(String ruta) {
        if (ruta == null || ruta.isBlank()) return IDS[0];
        String nombre = ruta;
        int slash = Math.max(nombre.lastIndexOf('/'), nombre.lastIndexOf('\\'));
        if (slash >= 0) nombre = nombre.substring(slash + 1);
        if (nombre.endsWith(".png")) nombre = nombre.substring(0, nombre.length() - 4);
        for (String id : IDS) {
            if (id.equalsIgnoreCase(nombre)) return id;
        }
        return IDS[0];
    }

    public static int indiceDe(String ruta) {
        String id = normalizar(ruta);
        for (int i = 0; i < IDS.length; i++) {
            if (IDS[i].equals(id)) return i;
        }
        return 0;
    }

    public static Texture obtener(String ruta) {
        String id = normalizar(ruta);
        return cache.computeIfAbsent(id, GestorAvatares::crearTextura);
    }

    public static void exportar(Path destino, String ruta) throws IOException {
        if (destino.getParent() != null) {
            Files.createDirectories(destino.getParent());
        }
        Pixmap pixmap = crearPixmap(normalizar(ruta));
        try {
            PixmapIO.writePNG(new FileHandle(destino.toFile()), pixmap);
        } finally {
            pixmap.dispose();
        }
    }

    public static void dispose() {
        for (Texture texture : cache.values()) {
            texture.dispose();
        }
        cache.clear();
    }

    private static Texture crearTextura(String id) {
        Pixmap pixmap = crearPixmap(id);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return texture;
    }

    private static Pixmap crearPixmap(String id) {
        Pixmap pixmap = new Pixmap(SIZE, SIZE, Pixmap.Format.RGBA8888);
        switch (id) {
            case "space" -> dibujarSpace(pixmap);
            case "retro" -> dibujarRetro(pixmap);
            case "neon"  -> dibujarNeon(pixmap);
            default      -> dibujarDefault(pixmap);
        }
        return pixmap;
    }

    private static void dibujarDefault(Pixmap pm) {
        pm.setColor(0.10f, 0.12f, 0.20f, 1f);
        pm.fill();
        pm.setColor(0.00f, 0.85f, 0.90f, 1f);
        pm.fillCircle(SIZE / 2, SIZE / 2, 24);
        pm.setColor(0.10f, 0.12f, 0.20f, 1f);
        pm.fillCircle(SIZE / 2 - 9, SIZE / 2 + 6, 5);
        pm.fillCircle(SIZE / 2 + 9, SIZE / 2 + 6, 5);
        pm.fillCircle(SIZE / 2, SIZE / 2 - 4, 8);
    }

    private static void dibujarSpace(Pixmap pm) {
        pm.setColor(0.05f, 0.04f, 0.16f, 1f);
        pm.fill();
        pm.setColor(1f, 1f, 1f, 0.9f);
        pm.drawPixel(10, 48);
        pm.drawPixel(22, 54);
        pm.drawPixel(48, 44);
        pm.drawPixel(54, 18);
        pm.drawPixel(14, 20);
        pm.setColor(0.55f, 0.35f, 0.95f, 1f);
        pm.fillCircle(36, 30, 14);
        pm.setColor(0.80f, 0.65f, 1.00f, 1f);
        pm.fillCircle(40, 34, 5);
        pm.setColor(0.95f, 0.55f, 0.20f, 1f);
        pm.fillCircle(18, 28, 10);
    }

    private static void dibujarRetro(Pixmap pm) {
        pm.setColor(0.04f, 0.10f, 0.04f, 1f);
        pm.fill();
        pm.setColor(0.10f, 0.85f, 0.30f, 0.35f);
        for (int x = 0; x < SIZE; x += 8) pm.drawLine(x, 0, x, SIZE - 1);
        for (int y = 0; y < SIZE; y += 8) pm.drawLine(0, y, SIZE - 1, y);
        pm.setColor(0.10f, 0.95f, 0.35f, 1f);
        pm.fillRectangle(14, 14, 36, 36);
        pm.setColor(0.04f, 0.10f, 0.04f, 1f);
        pm.fillRectangle(22, 22, 8, 8);
        pm.fillRectangle(34, 22, 8, 8);
        pm.fillRectangle(26, 36, 12, 6);
    }

    private static void dibujarNeon(Pixmap pm) {
        pm.setColor(0.08f, 0.04f, 0.14f, 1f);
        pm.fill();
        pm.setColor(0.95f, 0.20f, 0.75f, 0.35f);
        pm.fillCircle(SIZE / 2, SIZE / 2, 28);
        pm.setColor(0.95f, 0.20f, 0.75f, 1f);
        pm.drawCircle(SIZE / 2, SIZE / 2, 22);
        pm.setColor(0.00f, 0.95f, 0.95f, 1f);
        pm.fillCircle(SIZE / 2, SIZE / 2, 12);
        pm.setColor(1f, 1f, 1f, 1f);
        pm.fillCircle(SIZE / 2 - 4, SIZE / 2 + 2, 3);
        pm.fillCircle(SIZE / 2 + 4, SIZE / 2 + 2, 3);
    }
}
