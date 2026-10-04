package com.rpg.game.core;

import com.rpg.game.assets.AssetManager;
import com.rpg.game.input.InputManager;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GameApplication extends Application {

    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;
    public static final String WINDOW_TITLE = "Nombre del Juego - Working Title";

    // Velocidad de movimiento en píxeles por segundo. Al multiplicarse por
    // deltaSeconds en vez de ser un valor fijo por frame, el movimiento anda
    // igual de rápido sin importar si la máquina corre a 60 o 144 FPS.
    private static final double MOVE_SPEED = 300.0;

    private GraphicsContext gc;
    private long lastUpdateTime = 0;

    private Image testBackground;
    private Image testSprite;

    // Posición actual del sprite en pantalla.
    private double spriteX;
    private double spriteY;

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Conectamos el InputManager a esta Scene para que empiece a
        // escuchar teclas. Tiene que hacerse acá, una sola vez.
        InputManager.getInstance().attachTo(scene);

        primaryStage.setTitle(WINDOW_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        loadTestAssets();
        startGameLoop();
    }

    private void loadTestAssets() {
        AssetManager assets = AssetManager.getInstance();
        assets.preloadBackgrounds();
        assets.preloadSprites();

        testBackground = assets.getBackground("sae.png");
        testSprite = assets.getSprite("spritetest1.png");

        // Posición inicial del sprite: centrado horizontalmente, apoyado
        // abajo de la pantalla.
        if (testSprite != null) {
            spriteX = WINDOW_WIDTH / 2.0 - testSprite.getWidth() / 2;
            spriteY = WINDOW_HEIGHT - testSprite.getHeight();
        }
    }

    private void startGameLoop() {
        AnimationTimer loop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastUpdateTime == 0) {
                    lastUpdateTime = now;
                }
                double deltaSeconds = (now - lastUpdateTime) / 1_000_000_000.0;
                lastUpdateTime = now;

                update(deltaSeconds);
                render();
            }
        };
        loop.start();
    }

    private void update(double deltaSeconds) {
        if (testSprite == null) return;

        InputManager input = InputManager.getInstance();
        double distance = MOVE_SPEED * deltaSeconds;

        // WASD y flechas, las dos mapeadas a lo mismo.
        if (input.isKeyDown(KeyCode.W) || input.isKeyDown(KeyCode.UP)) {
            spriteY -= distance;
        }
        if (input.isKeyDown(KeyCode.S) || input.isKeyDown(KeyCode.DOWN)) {
            spriteY += distance;
        }
        if (input.isKeyDown(KeyCode.A) || input.isKeyDown(KeyCode.LEFT)) {
            spriteX -= distance;
        }
        if (input.isKeyDown(KeyCode.D) || input.isKeyDown(KeyCode.RIGHT)) {
            spriteX += distance;
        }

        // Clamp: que el sprite no se salga de la ventana.
        spriteX = clamp(spriteX, 0, WINDOW_WIDTH - testSprite.getWidth());
        spriteY = clamp(spriteY, 0, WINDOW_HEIGHT - testSprite.getHeight());
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private void render() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        if (testBackground != null) {
            gc.drawImage(testBackground, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        }

        if (testSprite != null) {
            gc.drawImage(testSprite, spriteX, spriteY);
        }

        gc.setFill(Color.WHITE);
        gc.fillText("Motor base funcionando", 20, 30);
    }

    public static void main(String[] args) {
        launch(args);
    }
}