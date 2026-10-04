package com.rpg.game;

import com.rpg.game.core.GameApplication;

/**
 * Launcher separado de la clase Application.
 *
 * ¿Por qué existe esta clase si GameApplication ya tiene su propio main()?
 * Porque cuando empaquetemos el juego como JAR ejecutable final (más adelante,
 * en la etapa de empaquetado), Java suele tirar el error
 * "JavaFX runtime components are missing" si la clase que tiene el main()
 * extiende directamente de Application. Tener un launcher separado evita
 * ese problema. Por ahora, durante desarrollo, da igual cuál uses.
 */
public class Main {
    public static void main(String[] args) {
        GameApplication.main(args);
    }
}