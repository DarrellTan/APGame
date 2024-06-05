package com.example.gametest;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;

public class GameApp extends Application {

    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final int CHARACTER_SIZE = 40;  // Assuming the character image is 40x40 pixels
    private static final int MOVE_DISTANCE = 5;

    private ImageView character;
    private Set<String> keysPressed = new HashSet<>();

    public static void main(String[] args) {
        launch(args);
    }

    // Movement Test
    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();

        Image characterImage = new Image("yena icon.jpeg");
        character = new ImageView(characterImage);
        character.setFitWidth(CHARACTER_SIZE);
        character.setFitHeight(CHARACTER_SIZE);
        character.setX(WINDOW_WIDTH / 2 - CHARACTER_SIZE / 2);
        character.setY(WINDOW_HEIGHT / 2 - CHARACTER_SIZE / 2);

        root.getChildren().add(character);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.setOnKeyPressed(this::handleKeyPressed);
        scene.setOnKeyReleased(this::handleKeyReleased);

        primaryStage.setTitle("JavaFX Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Request focus on the root pane to receive key events
        root.requestFocus();

        // Start an animation timer to update the character's position
        new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                updateCharacterPosition();
            }
        }.start();
    }

    // Checks if Key is pressed or not
    private void handleKeyPressed(KeyEvent event) {
        keysPressed.add(event.getCode().toString());
    }

    private void handleKeyReleased(KeyEvent event) {
        keysPressed.remove(event.getCode().toString());
    }

    private void updateCharacterPosition() {
        if (keysPressed.contains("W")) {
            moveUp();
        }
        if (keysPressed.contains("A")) {
            moveLeft();
        }
        if (keysPressed.contains("S")) {
            moveDown();
        }
        if (keysPressed.contains("D")) {
            moveRight();
        }
    }

    private void moveUp() {
        if (character.getY() - MOVE_DISTANCE >= 0) {
            character.setY(character.getY() - MOVE_DISTANCE);
        }
    }

    private void moveDown() {
        if (character.getY() + MOVE_DISTANCE + CHARACTER_SIZE <= WINDOW_HEIGHT) {
            character.setY(character.getY() + MOVE_DISTANCE);
        }
    }

    private void moveLeft() {
        if (character.getX() - MOVE_DISTANCE >= 0) {
            character.setX(character.getX() - MOVE_DISTANCE);
        }
    }

    private void moveRight() {
        if (character.getX() + MOVE_DISTANCE + CHARACTER_SIZE <= WINDOW_WIDTH) {
            character.setX(character.getX() + MOVE_DISTANCE);
        }
    }
}
