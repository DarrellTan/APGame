package com.example.gametest;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.math.MathContext;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameApp7 extends Application {

    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final long SHOOTING_COOLDOWN = 1_000_000_000; // 1 second in nanoseconds
    private static final long FRAME_DURATION = 1_000_000_000 / 60; // 60 FPS in nanoseconds
    private Pane root;

    private CharacterTest character;
    private Character test;
    private List<Character> characters = new ArrayList<>();
    private Set<String> keysPressed = new HashSet<>();
    private List<Projectile5> projectiles = new ArrayList<>();
    private long lastUpdateTime = 0;
    private long lastShotTime = 0;

    private double npcSpeed = 100; // Pixels per second

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
        test = new Character("chaewon icon.jpeg", 700, 500);
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
        new AnimationTimer() {
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

    private void handleKeyPressed(KeyEvent event) {
        keysPressed.add(event.getCode().toString());
    }

    private void handleKeyReleased(KeyEvent event) {
        keysPressed.remove(event.getCode().toString());
    }

    private void update(long now) {
        updateCharacterPosition();
       updateNpcPosition(now); // Update NPC position
        shootingTest(now);
        updateProjectiles();
        checkProjectileCollisions();
    }

    private void render() {
        // Render code if needed
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



    private void updateNpcPosition(long now) {
        double dt = 1.0 / 60.0;
        System.out.println("DT: " + dt);
        // Calculate the desired target position for the NPC
        double targetX = character.getImageView().getX();
        double targetY = character.getImageView().getY();
        System.out.println("Position of Test X: " + test.getImageView().getX() + " Position of Test Y: " + test.getImageView().getY());

        // Calculate the direction vector from NPC to character
        double directionX = targetX - test.getImageView().getX();
        double directionY = targetY - test.getImageView().getY();
        double length = Math.sqrt(directionX * directionX + directionY * directionY);
        System.out.println("Direction X: " + directionX + " Direction Y: " + directionY);

        // Normalize the direction vector (optional, but ensures smoother movement)
        if (length != 0) {
            directionX /= length;
            directionY /= length;
        }

        // Calculate the desired movement amount based on NPC speed and elapsed time
        double dx = directionX * npcSpeed * dt;
        double dy = directionY * npcSpeed * dt;

        System.out.println("dx: " + dx + " dy: " + dy);

        // Update the NPC's position with boundary checks
        double newX = test.getImageView().getX() + dx;
        double newY = test.getImageView().getY() + dy;
        System.out.println("Test X: " + test.getImageView().getX() + " Test Y: " + test.getImageView().getY());
        System.out.println("newX: " + newX + " newY: " + newY);
        newX = Math.max(0, Math.min(newX, WINDOW_WIDTH - Character.CHARACTER_SIZE));
        newY = Math.max(0, Math.min(newY, WINDOW_HEIGHT - Character.CHARACTER_SIZE));

        // Move the NPC
        test.moveTo(newX, newY);
        System.out.println("Target X: " + targetX + " Target Y: " + targetY + " Direction X: " + directionX + " Direction Y: " + directionY + " Test X: " + test.getImageView().getX() + " Test Y: " + test.getImageView().getY());
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
        Projectile5 projectile = new Projectile5("projectile.png", character.getImageView().getX(), character.getImageView().getY(), direction);
        projectiles.add(projectile);
        root.getChildren().add(projectile.getProjectileImageView());
    }

    private void updateProjectiles() {
        List<Projectile5> projectilesToRemove = new ArrayList<>();
        for (Projectile5 projectile : projectiles) {
            projectile.move();
            if (projectile.getProjectileImageView().getX() < 0 || projectile.getProjectileImageView().getX() > WINDOW_WIDTH
                    || projectile.getProjectileImageView().getY() < 0 || projectile.getProjectileImageView().getY() > WINDOW_HEIGHT) {
                projectilesToRemove.add(projectile);
                root.getChildren().remove(projectile.getProjectileImageView());
            }
        }
        projectiles.removeAll(projectilesToRemove);
    }

    private void checkProjectileCollisions() {
        List<Projectile5> projectilesToRemove = new ArrayList<>();
        List<Character> charactersToRemove = new ArrayList<>();

        for (Projectile5 projectile : projectiles) {
            for (Character character : characters) {
                if (collisionCheck(projectile, character)) {
                    projectilesToRemove.add(projectile);
                    root.getChildren().remove(projectile.getProjectileImageView());
                    handleCharacterHit(character); // Pass the hit character to handleCharacterHit
                    charactersToRemove.add(character); // Add character to remove from characters list
                }
            }
        }

        projectiles.removeAll(projectilesToRemove);
        characters.removeAll(charactersToRemove); // Remove all hit characters from characters list
    }

    private boolean collisionCheck(Projectile5 projectile, Character character) {
        return projectile.getProjectileImageView().getBoundsInParent().intersects(character.getImageView().getBoundsInParent());
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

    private void handleCharacterHit(Character character) {
        // Implement your logic here to handle the hit character
        // For example, remove character from the scene or perform other actions
        root.getChildren().remove(character.getImageView()); // Remove character from scene graph
        // Optionally, perform other actions like reducing health, updating score, etc.
    }
}
