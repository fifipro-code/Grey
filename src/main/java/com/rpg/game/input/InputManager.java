package com.rpg.game.input;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

import java.util.HashSet;
import java.util.Set;

/**
 * Gestor central de input de teclado.
 *
 * ¿Por qué un singleton aparte y no manejar las teclas directo en
 * GameApplication? Porque CUALQUIER sistema del juego va a necesitar saber
 * qué tecla está apretada: movimiento, menús, combate, avance de diálogo.
 * Centralizarlo acá evita que cada clase tenga que registrar su propio
 * listener de teclado por separado.
 *
 * Funciona con un Set de teclas actualmente presionadas: se agregan en
 * KEY_PRESSED y se sacan en KEY_RELEASED. Cualquier clase puede preguntar
 * "¿está KeyCode.W apretada ahora mismo?" con isKeyDown().
 */
public class InputManager {

    private static final InputManager INSTANCE = new InputManager();

    private final Set<KeyCode> pressedKeys = new HashSet<>();

    private InputManager() {
    }

    public static InputManager getInstance() {
        return INSTANCE;
    }

    /**
     * Conecta este InputManager a una Scene de JavaFX. Se llama UNA sola vez,
     * al armar la ventana (en GameApplication.start()).
     */
    public void attachTo(Scene scene) {
        scene.setOnKeyPressed(event -> pressedKeys.add(event.getCode()));
        scene.setOnKeyReleased(event -> pressedKeys.remove(event.getCode()));
    }

    public boolean isKeyDown(KeyCode key) {
        return pressedKeys.contains(key);
    }
}