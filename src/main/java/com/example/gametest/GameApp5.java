package com.example.gametest;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameApp5 extends Application {

    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final long SHOOTING_COOLDOWN = 1_000_000_000; // 100 milliseconds in nanoseconds
    private static final long FRAME_DURATION = 1_000_000_000 / 60; // Cap at 60 FPS
    public Pane root;

    private CharacterTest character;
    public Character test;
    private List<Character> characters = new ArrayList<>();
    private Set<String> keysPressed = new HashSet<>();
    private List<Projectile4> projectiles = new ArrayList<>();
    private long lastShotTime = 0;
    private long lastUpdateTime = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        root = new Pane(); // Initialize root here

        // MC Character
        character = new CharacterTest();
        character.getImageView().setX(WINDOW_WIDTH / 2 - Character.CHARACTER_SIZE / 2);
        character.getImageView().setY(WINDOW_HEIGHT / 2 - Character.CHARACTER_SIZE / 2);

        root.getChildren().add(character.getImageView());

        // Bot
        test = new Character("chaewon icon.jpeg");
        test.getImageView().setX(700);
        test.getImageView().setY(500);
        root.getChildren().add(test.getImageView());
        characters.add(test);


        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.setOnKeyPressed(this::handleKeyPressed);
        scene.setOnKeyReleased(this::handleKeyReleased);

        primaryStage.setTitle("SsamYen");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Request focus on the root pane to receive key events
        root.requestFocus();

        // Start an animation timer to update the character's position
        new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdateTime >= FRAME_DURATION) {
                    update(now);
                    render();
                    lastUpdateTime = now;
                }
            }
        }.start();
    }

    private void update(long now) {
        updateCharacterPosition();
        shootingTest(now);
        updateProjectiles();
    }

    private void render() {
        // Render code if needed
    }

    private void handleKeyPressed(KeyEvent event) {
        keysPressed.add(event.getCode().toString());
    }

    private void handleKeyReleased(KeyEvent event) {
        keysPressed.remove(event.getCode().toString());
    }

    private void updateCharacterPosition() {
        // Gets where character is located
        double characterX = character.getImageView().getX();
        double characterY = character.getImageView().getY();

        if (keysPressed.contains("W") && !collisionInDirection(characterX, characterY, test, "W")) {
            character.moveUp();
        }
        if (keysPressed.contains("A") && !collisionInDirection(characterX, characterY, test, "A")) {
            character.moveLeft();
        }
        if (keysPressed.contains("S") && !collisionInDirection(characterX, characterY, test, "S")) {
            character.moveDown(WINDOW_HEIGHT);
        }
        if (keysPressed.contains("D") && !collisionInDirection(characterX, characterY, test, "D")) {
            character.moveRight(WINDOW_WIDTH);
        }
    }

    private void shootingTest(long now) {
        if (now - lastShotTime < SHOOTING_COOLDOWN) {
            return; // Enforce cooldown
        }

        if (keysPressed.contains("UP")) {
            System.out.println("Up Pressed");
            createProjectile("UP");
            lastShotTime = now;
        }
        if (keysPressed.contains("DOWN")) {
            System.out.println("Down Pressed");
            createProjectile("DOWN");
            lastShotTime = now;
        }
        if (keysPressed.contains("LEFT")) {
            System.out.println("Left Pressed");
            createProjectile("LEFT");
            lastShotTime = now;
        }
        if (keysPressed.contains("RIGHT")) {
            System.out.println("Right Pressed");
            createProjectile("RIGHT");
            lastShotTime = now;
        }
    }

    private void createProjectile(String direction) {
        Projectile4 projectile = new Projectile4("projectile.png", character.getImageView().getX(), character.getImageView().getY(), direction);
        projectiles.add(projectile);
        root.getChildren().add(projectile.getprojectImageView());
    }

    private void updateProjectiles() {
        List<Projectile4> projectilesToRemove = new ArrayList<>();
        for (Projectile4 projectile : projectiles) {
            projectile.move();
            if (projectile.getprojectImageView().getX() < 0 || projectile.getprojectImageView().getX() > WINDOW_WIDTH
                    || projectile.getprojectImageView().getY() < 0 || projectile.getprojectImageView().getY() > WINDOW_HEIGHT) {
                projectilesToRemove.add(projectile);
                root.getChildren().remove(projectile.getprojectImageView());
            }
        }
        projectiles.removeAll(projectilesToRemove);
        System.out.println(projectiles.toString());
    }

    private void checkProjectileCollisions() {
        List<Projectile4> projectilesToRemove = new ArrayList<>();
        for (Projectile4 projectile : projectiles) {
            for (Character character : characters) {
                if (projectile.collidesWith(character)) {
                    projectilesToRemove.add(projectile);
                    root.getChildren().remove(projectile.getprojectImageView());
                    handleCharacterHit(character);
                }
            }
        }
        projectiles.removeAll(projectilesToRemove);
    }

    private void handleCharacterHit(Character character) {
        // Implement the logic for when a character is hit by a projectile
        System.out.println("Character hit: " + character);
    }

    private boolean collisionInDirection(double characterX, double characterY, Character other, String direction) {
        double testX = other.getImageView().getX();
        double testY = other.getImageView().getY();

        // Calculate the edges of the characters' images
        double characterLeft = characterX;
        double characterRight = characterX + Character.CHARACTER_SIZE;
        double characterTop = characterY;
        double characterBottom = characterY + Character.CHARACTER_SIZE;

        double testLeft = testX;
        double testRight = testX + Character.CHARACTER_SIZE;
        double testTop = testY;
        double testBottom = testY + Character.CHARACTER_SIZE;

        // Check for partial intersection between the edges of the characters' images
        switch (direction) {
            case "W": // Moving up
                return characterRight > testLeft && characterLeft < testRight &&
                        characterTop - Character.MOVE_DISTANCE < testBottom && characterTop > testTop;
            case "A": // Moving left
                return characterBottom > testTop && characterTop < testBottom &&
                        characterLeft - Character.MOVE_DISTANCE < testRight && characterLeft > testLeft;
            case "S": // Moving down
                return characterRight > testLeft && characterLeft < testRight &&
                        characterBottom + Character.MOVE_DISTANCE > testTop && characterBottom < testBottom;
            case "D": // Moving right
                return characterBottom > testTop && characterTop < testBottom &&
                        characterRight + Character.MOVE_DISTANCE > testLeft && characterRight < testRight;
            default:
                return false;
        }
    }

    private void checkCollisions() {
        if (character.collidesWith(test)) {
            // Handle collision (e.g., stop movement, reduce health, etc.)
        }
    }
}
