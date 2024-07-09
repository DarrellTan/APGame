package com.example.gametest;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameApp extends Application {

    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;
    private static final long SHOOTING_COOLDOWN = 1_000_000_000; // 1 second in nanoseconds
    private static final long FRAME_DURATION = 1_000_000_000 / 60; // 60 FPS in nanoseconds
    private Pane root;

    public PlayableCharacter character;
    private NonPlayableCharacter test;
    private List<NonPlayableCharacter> nonPlayableCharacters = new ArrayList<>();
    private Set<String> keysPressed = new HashSet<>();
    private List<Projectile> projectiles = new ArrayList<>();
    private long lastUpdateTime = 0;
    private long lastShotTime = 0;
    private AudioPlayer audioPlayer;
    public WaveManager wave;

    public Image startNpcLeft = new Image(getClass().getResource("/nonPlayableCharacter/npcLeft.png").toExternalForm());
    public Image startNpcRight = new Image(getClass().getResource("/nonPlayableCharacter/npcRight.png").toExternalForm());

    public double npcSpeed = 30; // Pixels per second

    // Score Keeping in the UI
    public int score;
    public HUD hud;

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
//        test = new NonPlayableCharacter(startNpcRight, 700, 500);
//        root.getChildren().add(test.getImageView());
//        nonPlayableCharacters.add(test);

        // Wave Spawner
        WaveManager wave = new WaveManager(root, 800, 600);
        this.wave = wave;
        wave.setCharacter(character);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.setOnKeyPressed(this::handleKeyPressed);
        scene.setOnKeyReleased(this::handleKeyReleased);

        primaryStage.setTitle("Fearless");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Request focus on the root pane to receive key events
        root.requestFocus();

        // UI Related Initialization
        HUD hud = new HUD();

        this.hud = hud;
        // Add Health Icons and Labels to the root node
        root.getChildren().addAll(hud.healthIcon1, hud.healthIcon2, hud.healthIcon3, hud.scoreLabel, hud.highscoreLabel, hud.timeLabel);

        // Initialize AudioPlayer
       audioPlayer = new AudioPlayer();
       String musicFilePath = "src/main/resources/music/pvz.wav";
       audioPlayer.playMusic(musicFilePath);
       audioPlayer.setMusicVolume(-10.0f);

        // Start waves
        wave.startWaves();

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

        if (keysPressed.contains("W") && !collisionInDirection(characterX, characterY, wave.npcs, "W")) {
            character.moveUp();
            System.out.println("Moving Up Pressed");
        }
        if (keysPressed.contains("A") && !collisionInDirection(characterX, characterY, wave.npcs, "A")) {
            character.moveLeft();
            System.out.println("Moving Left Pressed");
        }
        if (keysPressed.contains("S") && !collisionInDirection(characterX, characterY, wave.npcs, "S")) {
            character.moveDown(WINDOW_HEIGHT);
            System.out.println("Moving Down Pressed");
        }
        if (keysPressed.contains("D") && !collisionInDirection(characterX, characterY, wave.npcs, "D")) {
            character.moveRight(WINDOW_WIDTH);
            System.out.println("Moving Right Pressed");
        }
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

        // Assuming 'wave' is properly initialized and accessible here
        for (Projectile projectile : projectiles) {
            for (NonPlayableCharacter nonPlayableCharacter : wave.npcs) { // Iterate over wave.npcs
                if (collisionCheck(projectile, nonPlayableCharacter)) {
                    projectilesToRemove.add(projectile);
                    root.getChildren().remove(projectile.getProjectileImageView());
                    handleCharacterHit(nonPlayableCharacter); // Handle hit character
                    charactersToRemove.add(nonPlayableCharacter); // Mark character for removal
                }
            }
        }

        // Remove projectiles and characters that need removal
        projectiles.removeAll(projectilesToRemove);
        wave.npcs.removeAll(charactersToRemove);
    }


    private boolean collisionCheck(Projectile projectile, NonPlayableCharacter nonPlayableCharacter) {
        return projectile.getProjectileImageView().getBoundsInParent().intersects(nonPlayableCharacter.getImageView().getBoundsInParent());
    }

    private boolean collisionInDirection(double characterX, double characterY, List<NonPlayableCharacter> npcs, String direction) {
        for (NonPlayableCharacter other : npcs) {
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
                    if (characterRight > testLeft && characterLeft < testRight &&
                            characterTop - NonPlayableCharacter.MOVE_DISTANCE < testBottom && characterTop > testTop) {
                        return true;
                    }
                    break;
                case "A": // Moving left
                    if (characterBottom > testTop && characterTop < testBottom &&
                            characterLeft - NonPlayableCharacter.MOVE_DISTANCE < testRight && characterLeft > testLeft) {
                        return true;
                    }
                    break;
                case "S": // Moving down
                    if (characterRight > testLeft && characterLeft < testRight &&
                            characterBottom + NonPlayableCharacter.MOVE_DISTANCE > testTop && characterBottom < testBottom) {
                        return true;
                    }
                    break;
                case "D": // Moving right
                    if (characterBottom > testTop && characterTop < testBottom &&
                            characterRight + NonPlayableCharacter.MOVE_DISTANCE > testLeft && characterRight < testRight) {
                        return true;
                    }
                    break;
                default:
                    break;
            }
        }
        return false;
    }


    private void handleCharacterHit(NonPlayableCharacter nonPlayableCharacter) {
        // Implement your logic here to handle the hit character
        // For example, remove character from the scene or perform other actions
        root.getChildren().remove(nonPlayableCharacter.getImageView()); // Remove character from scene graph
        // Optionally, perform other actions like reducing health, updating score, etc.
    }
}
