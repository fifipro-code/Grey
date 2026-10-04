package com.rpg.game.assets;

import javafx.scene.image.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestor central de assets gráficos (sprites y fondos).
 *
 * Decisión de diseño importante: carga desde una carpeta EXTERNA
 * (assets/sprites/, assets/backgrounds/) relativa a donde se ejecuta el
 * juego, NO desde dentro del JAR. Esto es a propósito: mientras vos vas
 * generando arte, lo tirás en esas carpetas y el juego lo lee directo, sin
 * recompilar. Cuando lleguemos a la etapa de empaquetado (más adelante en
 * el cronograma), decidimos si conviene embeber los assets finales en el
 * JAR o seguir distribuyéndolos como carpeta aparte.
 *
 * Es un singleton simple: un solo gestor de assets para todo el juego,
 * evita cargar la misma imagen de disco más de una vez.
 */
public class AssetManager {

    private static final AssetManager INSTANCE = new AssetManager();

    // Rutas base. Si cambiamos la estructura de carpetas del repo, se
    // ajustan solo acá.
    private static final String SPRITES_PATH = "assets/sprites/";
    private static final String BACKGROUNDS_PATH = "assets/backgrounds/";

    private final Map<String, Image> spriteCache = new HashMap<>();
    private final Map<String, Image> backgroundCache = new HashMap<>();

    private AssetManager() {
    }

    public static AssetManager getInstance() {
        return INSTANCE;
    }

    /**
     * Devuelve un sprite por nombre de archivo (ej: "protagonista_feliz.png").
     * Si ya fue cargado antes, lo devuelve desde cache. Si no existe el
     * archivo, devuelve null y loguea el error (el juego NO debe crashear
     * por un asset faltante).
     */
    public Image getSprite(String fileName) {
        return spriteCache.computeIfAbsent(fileName, name -> loadImage(SPRITES_PATH + name));
    }

    public Image getBackground(String fileName) {
        return backgroundCache.computeIfAbsent(fileName, name -> loadImage(BACKGROUNDS_PATH + name));
    }

    /**
     * Precarga TODOS los .png/.jpg de una carpeta de una vez. Útil para
     * cargar, por ejemplo, todos los sprites de un personaje al arrancar
     * un capítulo, en vez de ir cargando de a uno mientras se juega.
     */
    private void preloadDirectory(String directoryPath, Map<String, Image> targetCache) {
        File dir = new File(directoryPath);
        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("[AssetManager] Carpeta no encontrada: " + directoryPath);
            return;
        }

        File[] files = dir.listFiles((d, name) -> {
            String lower = name.toLowerCase();
            return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg");
        });

        if (files == null) return;

        for (File file : files) {
            Image image = loadImage(file.getPath());
            if (image != null) {
                targetCache.put(file.getName(), image);
                System.out.println("[AssetManager] Cargado: " + file.getName());
            }
        }
    }

    public void preloadSprites() {
        preloadDirectory(SPRITES_PATH, spriteCache);
    }

    public void preloadBackgrounds() {
        preloadDirectory(BACKGROUNDS_PATH, backgroundCache);
    }

    private Image loadImage(String path) {
        try (FileInputStream stream = new FileInputStream(path)) {
            return new Image(stream);
        } catch (FileNotFoundException e) {
            System.err.println("[AssetManager] No se encontró el archivo: " + path);
            return null;
        } catch (Exception e) {
            System.err.println("[AssetManager] Error cargando '" + path + "': " + e.getMessage());
            return null;
        }
    }

    /** Útil para tests o para forzar una recarga de assets durante desarrollo. */
    public void clearCache() {
        spriteCache.clear();
        backgroundCache.clear();
    }
}
