package com.example.gametest;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameApp extends Application {

    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final long SHOOTING_COOLDOWN = 1_000_000_000; // 1 second in nanoseconds
    private static final long FRAME_DURATION = 1_000_000_000 / 60; // 60 FPS in nanoseconds
    private Pane root;

    private PlayableCharacter character;
    private NonPlayableCharacter test;
    private List<NonPlayableCharacter> nonPlayableCharacters = new ArrayList<>();
    private Set<String> keysPressed = new HashSet<>();
    private List<Projectile> projectiles = new ArrayList<>();
    private long lastUpdateTime = 0;
    private long lastShotTime = 0;
    private AudioPlayer audioPlayer;

    private double npcSpeed = 100; // Pixels per second

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        root = new Pane(); // Initialize root here
        root.setStyle("-fx-background-image: url('room.png');"); // Inline CSS for background

        // MC Character
        character = new PlayableCharacter();
        character.getImageView().setX(WINDOW_WIDTH / 2 - NonPlayableCharacter.CHARACTER_SIZE / 2);
        character.getImageView().setY(WINDOW_HEIGHT / 2 - NonPlayableCharacter.CHARACTER_SIZE / 2);
        root.getChildren().add(character.getImageView());

        // Bot
        test = new NonPlayableCharacter("chaewon icon.jpeg", 700, 500);
        root.getChildren().add(test.getImageView());
        nonPlayableCharacters.add(test);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.setOnKeyPressed(this::handleKeyPressed);
        scene.setOnKeyReleased(this::handleKeyReleased);

        primaryStage.setTitle("SsamYen");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Request focus on the root pane to receive key events
        root.requestFocus();

        // Initialize AudioPlayer
      //  audioPlayer = new AudioPlayer();
        //String musicFilePath = "src/main/resources/music/pvz.wav";
        //audioPlayer.playMusic(musicFilePath);
        //audioPlayer.setMusicVolume(-10.0f);

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
       // System.out.println("DT: " + dt);
        // Calculate the desired target position for the NPC
        double targetX = character.getImageView().getX();
        double targetY = character.getImageView().getY();
       // System.out.println("Position of Test X: " + test.getImageView().getX() + " Position of Test Y: " + test.getImageView().getY());

        // Calculate the direction vector from NPC to character
        double directionX = targetX - test.getImageView().getX();
        double directionY = targetY - test.getImageView().getY();
        double length = Math.sqrt(directionX * directionX + directionY * directionY);
       // System.out.println("Direction X: " + directionX + " Direction Y: " + directionY);

        // Normalize the direction vector (optional, but ensures smoother movement)
        if (length != 0) {
            directionX /= length;
            directionY /= length;
        }

        // Calculate the desired movement amount based on NPC speed and elapsed time
        double dx = directionX * npcSpeed * dt;
        double dy = directionY * npcSpeed * dt;

        //System.out.println("dx: " + dx + " dy: " + dy);

        // Determine the direction of movement and change the image accordingly
        if (dx > 0) {
            test.setImage(test.npcRight);
        } else if (dx < 0) {
            test.setImage(test.npcLeft);
        }

        // Update the NPC's position with boundary checks
        double newX = test.getImageView().getX() + dx;
        double newY = test.getImageView().getY() + dy;
//        System.out.println("Test X: " + test.getImageView().getX() + " Test Y: " + test.getImageView().getY());
//        System.out.println("newX: " + newX + " newY: " + newY);
        newX = Math.max(0, Math.min(newX, WINDOW_WIDTH - NonPlayableCharacter.CHARACTER_SIZE));
        newY = Math.max(0, Math.min(newY, WINDOW_HEIGHT - NonPlayableCharacter.CHARACTER_SIZE));

        // Move the NPC
        test.moveTo(newX, newY);
        //System.out.println("Target X: " + targetX + " Target Y: " + targetY + " Direction X: " + directionX + " Direction Y: " + directionY + " Test X: " + test.getImageView().getX() + " Test Y: " + test.getImageView().getY());
    }

    private void shootingTest(long now) {
        if (now - lastShotTime < SHOOTING_COOLDOWN) {
            return; // Enforce cooldown
        }

        boolean shotFired = false;

        if (keysPressed.contains("UP") && !shotFired) {
            System.out.println("Up Pressed");
            createProjectile("UP", "/projectile/projectileUp.png");
            lastShotTime = now;
            shotFired = true;
        }
        if (keysPressed.contains("DOWN") && !shotFired) {
            System.out.println("Down Pressed");
            createProjectile("DOWN", "/projectile/projectileDown.png");
            lastShotTime = now;
            shotFired = true;
        }
        if (keysPressed.contains("LEFT") && !shotFired) {
            System.out.println("Left Pressed");
            createProjectile("LEFT", "/projectile/projectileLeft.png");
            lastShotTime = now;
            shotFired = true;
        }
        if (keysPressed.contains("RIGHT") && !shotFired) {
            System.out.println("Right Pressed");
            createProjectile("RIGHT", "/projectile/projectileRight.png");
            lastShotTime = now;
            shotFired = true;
        }
    }

    private void createProjectile(String direction, String projectileDirection) {
        Projectile projectile = new Projectile(projectileDirection, character.getImageView().getX(), character.getImageView().getY(), direction);
        projectiles.add(projectile);
        root.getChildren().add(projectile.getProjectileImageView());
    }

    private void updateProjectiles() {
        List<Projectile> projectilesToRemove = new ArrayList<>();
        for (Projectile projectile : projectiles) {
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
        List<Projectile> projectilesToRemove = new ArrayList<>();
        List<NonPlayableCharacter> charactersToRemove = new ArrayList<>();

        for (Projectile projectile : projectiles) {
            for (NonPlayableCharacter nonPlayableCharacter : nonPlayableCharacters) {
                if (collisionCheck(projectile, nonPlayableCharacter)) {
                    projectilesToRemove.add(projectile);
                    root.getChildren().remove(projectile.getProjectileImageView());
                    handleCharacterHit(nonPlayableCharacter); // Pass the hit character to handleCharacterHit
                    charactersToRemove.add(nonPlayableCharacter); // Add character to remove from characters list
                }
            }
        }

        projectiles.removeAll(projectilesToRemove);
        nonPlayableCharacters.removeAll(charactersToRemove); // Remove all hit characters from characters list
    }

    private boolean collisionCheck(Projectile projectile, NonPlayableCharacter nonPlayableCharacter) {
        return projectile.getProjectileImageView().getBoundsInParent().intersects(nonPlayableCharacter.getImageView().getBoundsInParent());
    }

    private boolean collisionInDirection(double characterX, double characterY, NonPlayableCharacter other, String direction) {
        double testX = other.getImageView().getX();
        double testY = other.getImageView().getY();

        // Calculate the edges of the characters' images
        double characterLeft = characterX;
        double characterRight = characterX + NonPlayableCharacter.CHARACTER_SIZE;
        double characterTop = characterY;
        double characterBottom = characterY + NonPlayableCharacter.CHARACTER_SIZE;

        double testLeft = testX;
        double testRight = testX + NonPlayableCharacter.CHARACTER_SIZE;
        double testTop = testY;
        double testBottom = testY + NonPlayableCharacter.CHARACTER_SIZE;

        // Check for partial intersection between the edges of the characters' images
        switch (direction) {
            case "W": // Moving up
                return characterRight > testLeft && characterLeft < testRight &&
                        characterTop - NonPlayableCharacter.MOVE_DISTANCE < testBottom && characterTop > testTop;
            case "A": // Moving left
                return characterBottom > testTop && characterTop < testBottom &&
                        characterLeft - NonPlayableCharacter.MOVE_DISTANCE < testRight && characterLeft > testLeft;
            case "S": // Moving down
                return characterRight > testLeft && characterLeft < testRight &&
                        characterBottom + NonPlayableCharacter.MOVE_DISTANCE > testTop && characterBottom < testBottom;
            case "D": // Moving right
                return characterBottom > testTop && characterTop < testBottom &&
                        characterRight + NonPlayableCharacter.MOVE_DISTANCE > testLeft && characterRight < testRight;
            default:
                return false;
        }
    }

    private void handleCharacterHit(NonPlayableCharacter nonPlayableCharacter) {
        // Implement your logic here to handle the hit character
        // For example, remove character from the scene or perform other actions
        root.getChildren().remove(nonPlayableCharacter.getImageView()); // Remove character from scene graph
        // Optionally, perform other actions like reducing health, updating score, etc.
    }
}
