package com.example.gametest;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameApp extends Application {

    // JavaFX Variables
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;
    private static final long SHOOTING_COOLDOWN = 1_000_000_000; // 1 second in nanoseconds
    private static final long FRAME_DURATION = 1_000_000_000 / 60; // 60 FPS in nanoseconds
    private Pane root;

    public PlayableCharacter character;

    // Previous Developmental Test Code (Keeping for memories)
    //private NonPlayableCharacter test;

    // Arrays for Inputs, Projectiles and NPC characters
    private List<NonPlayableCharacter> nonPlayableCharacters = new ArrayList<>();
    private Set<String> keysPressed = new HashSet<>();
    private List<Projectile> projectiles = new ArrayList<>();

    // Player Balance Variables
    private long lastUpdateTime = 0;
    private long lastShotTime = 0;
    private long lastDamageTime;


    // Image Variables
    public Image startNpcLeft = new Image(getClass().getResource("/nonPlayableCharacter/npcLeft.png").toExternalForm());
    public Image startNpcRight = new Image(getClass().getResource("/nonPlayableCharacter/npcRight.png").toExternalForm());

    public double npcSpeed = 30; // Pixels per second

    // Player Score and Information Related Variables
    private int health = 3;

    // Score Keeping in the UI
    public int score;
    public int highscore;
    public int initialHighscore;

    // External Classes Variables
    private AudioPlayer audioPlayer;
    public WaveManager wave;
    public HUD hud;
    private HighScoreReadAndWriter highScoreReadAndWriter;

    // Pausing System Variables
    private boolean isPaused = false;
    private AnimationTimer gameTimer;

    public VBox pauseBox;

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

        // Bot (Previous Developmental Test)
//        test = new NonPlayableCharacter(startNpcRight, 700, 500);
//        root.getChildren().add(test.getImageView());
//        nonPlayableCharacters.add(test);

        // Wave Spawner
        WaveManager wave = new WaveManager(root, 800, 600);
        this.wave = wave;
        wave.setCharacter(character);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.setOnKeyPressed(this::handleKeyPressed);
        scene.setOnKeyPressed(this::handleKeyPause);
        scene.setOnKeyReleased(this::handleKeyReleased);

        primaryStage.setTitle("Fearless");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Request focus on the root pane to receive key events
        root.requestFocus();

        // Initialize Highscore
        HighScoreReadAndWriter highScoreReadAndWriter = new HighScoreReadAndWriter();
        this.highScoreReadAndWriter = highScoreReadAndWriter;
        this.highscore = highScoreReadAndWriter.readHighscore();
        System.out.println(highscore);

        // UI Related Initialization
        HUD hud = new HUD();

        this.hud = hud;

        // Initialize AudioPlayer
        audioPlayer = new AudioPlayer();
        String musicFilePath = "src/main/resources/music/pvz.wav";
        audioPlayer.playMusic(musicFilePath);
        audioPlayer.setMusicVolume(-10.0f);

        // Show Main Menu
        showMainMenu();
    }

    // Initialize Game Functions
    private void initializeGame() {
        // Clear existing game elements if any
        root.getChildren().clear();
        wave.npcs.clear();
        projectiles.clear();
        health = 3;
        score = 0;

        // Initialize the playable character
        character = new PlayableCharacter();
        character.getImageView().setX(WINDOW_WIDTH / 2 - NonPlayableCharacter.CHARACTER_SIZE / 2);
        character.getImageView().setY(WINDOW_HEIGHT / 2 - NonPlayableCharacter.CHARACTER_SIZE / 2);
        root.getChildren().add(character.getImageView());

        // Initialize HUD
        HUD hud = new HUD();
        this.hud = hud;
        hud.setHighscoreLabel(highscore);
        root.getChildren().addAll(hud.healthIcon1, hud.healthIcon2, hud.healthIcon3, hud.scoreLabel, hud.highscoreLabel, hud.timeLabel);

        // Initialize Wave Manager
        WaveManager wave = new WaveManager(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        this.wave = wave;
        wave.setCharacter(character);
    }

    private void startGame() {
        gameTimer = new AnimationTimer() {
            private long lastUpdateTime = 0;

            @Override
            public void handle(long now) {
                if (!isPaused && now - lastUpdateTime >= FRAME_DURATION) {
                    update(now);
                    lastUpdateTime = now;
                }
            }
        };
        gameTimer.start();
    }

    // Functions for Pausing System

    private void handlePauseButton() {
        if (isPaused) {
            resumeGame();
        } else {
            pauseGame();
        }
    }

    private void pauseGame() {
        isPaused = true;
        gameTimer.stop();
        showPauseMenu();
    }

    private void resumeGame() {
        isPaused = false;
        gameTimer.start();
        hidePauseMenu();
    }

    private void hidePauseMenu() {
        root.getChildren().removeIf(node -> node instanceof VBox); // Remove any VBox (pause menu)
    }

    private void showPauseMenu() {
        writingHighscore();
        VBox pauseMenu = new VBox();
        pauseMenu.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);"); // Semi-transparent black background
        pauseMenu.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        pauseMenu.setAlignment(Pos.CENTER); // Center aligns its content
        pauseMenu.setSpacing(20); // Add spacing between buttons

        Button resumeButton = new Button("Resume Game");
        resumeButton.setOnAction(event -> resumeGame());

        Button restartButton = new Button("Restart Game");
        restartButton.setOnAction(event -> {
            // Add logic to restart the game
            // initializeGame(); This for later
            isPaused = false;
            hidePauseMenu();
            initializeGame();
            startGame();
        });

        Button settingsButton = new Button("Settings");
        settingsButton.setOnAction(event -> {
            // Add logic to open settings
            System.out.println("Open settings");
        });

        Button exitButton = new Button("Exit to Main Menu");
        exitButton.setOnAction(event -> {
            // Add logic to exit to main menu
            System.out.println("Exit to main menu");
            hidePauseMenu();
            showMainMenu();
        });

        pauseMenu.getChildren().addAll(resumeButton, restartButton, settingsButton, exitButton);
        root.getChildren().add(pauseMenu);
    }

    private void showMainMenu() {
        VBox gameMenu = new VBox();
        gameMenu.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);"); // Semi-transparent black background
        gameMenu.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        gameMenu.setAlignment(Pos.CENTER); // Center aligns its content
        gameMenu.setSpacing(20); // Add spacing between buttons


        Button startButton = new Button("Start Game");
        startButton.setOnAction(event -> {
            // Add logic to restart the game
            // initializeGame(); This for later
            hidePauseMenu();
            initializeGame();
            startGame();
        });

        Button settingsButton = new Button("Settings");
        settingsButton.setOnAction(event -> {
            // Add logic to open settings
            System.out.println("Open settings");
        });

        Button exitButton = new Button("Exit Game");
        exitButton.setOnAction(event -> {
            // Add logic to exit to main menu
            System.out.println("Exit Game");
            Platform.exit();

        });

        gameMenu.getChildren().addAll(startButton, settingsButton, exitButton);
        root.getChildren().add(gameMenu);
    }

    public void deathScreen() {
        if (health <= 0) {
            writingHighscore();

            gameTimer.stop();
            VBox deathMenu = new VBox();
            deathMenu.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);"); // Semi-transparent black background
            deathMenu.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            deathMenu.setAlignment(Pos.CENTER); // Center aligns its content
            deathMenu.setSpacing(20); // Add spacing between buttons

            Label gameOver = new Label("Game Over");
            gameOver.setStyle("-fx-font-size: 48px; -fx-text-fill: white;");
            Label scoreText = new Label("Score: " + score);
            scoreText.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");

            Button restartButton = new Button("Restart Game");
            restartButton.setOnAction(event -> {
                // Add logic to restart the game
                // initializeGame(); This for later
                isPaused = false;
                hidePauseMenu();
                initializeGame();
                startGame();
            });

            Button settingsButton = new Button("Settings");
            settingsButton.setOnAction(event -> {
                // Add logic to open settings
                System.out.println("Open settings");
            });

            Button exitButton = new Button("Exit to Main Menu");
            exitButton.setOnAction(event -> {
                // Add logic to exit to main menu
                System.out.println("Exit to main menu");
                hidePauseMenu();
                showMainMenu();
            });

            deathMenu.getChildren().addAll(gameOver, scoreText, restartButton, settingsButton, exitButton);
            root.getChildren().add(deathMenu);
        }
    }

    public void endGame() {
        gameTimer.stop();
    }

    // Writing Highscore
   public void setHighscore(int score) {
        if (score > highscore) {
            highscore = score;
            hud.setHighscoreLabel(highscore);
       }
   }

   public void writingHighscore() {
       if (score > initialHighscore) {
           highScoreReadAndWriter.writeHighscore(score);
           System.out.println("Writing Highscore Function Ran");
       }
   }


    private void handleKeyPause(KeyEvent event) {
        String code = event.getCode().toString();
        keysPressed.add(code);

        // Check if the Esc key is pressed to toggle the pause state
        if (event.getCode() == KeyCode.ESCAPE) {
            handlePauseButton();
        }
    }

    private void handleKeyPressed(KeyEvent event) {
        keysPressed.add(event.getCode().toString());
    }

    private void handleKeyReleased(KeyEvent event) {
        keysPressed.remove(event.getCode().toString());
    }

    private void update(long now) {
        setHighscore(score);
        updateCharacterPosition();
        shootingTest(now);
        updateProjectiles();
        checkProjectileCollisions();
        playableCharacterHit(now);
        // Spawn waves
        wave.update(now);
        deathScreen();
    }

    // Scrapped Function
    private void render() {
        // Render code if needed
    }


    private void updateCharacterPosition() {
        // Gets where character is located
        double characterX = character.getImageView().getX();
        double characterY = character.getImageView().getY();

        if (keysPressed.contains("W") && !collisionInDirection(characterX, characterY, wave.npcs, "W")) {
            character.moveUp();
            //System.out.println("Moving Up Pressed");
        }
        if (keysPressed.contains("A") && !collisionInDirection(characterX, characterY, wave.npcs, "A")) {
            character.moveLeft();
            //System.out.println("Moving Left Pressed");
        }
        if (keysPressed.contains("S") && !collisionInDirection(characterX, characterY, wave.npcs, "S")) {
            character.moveDown(WINDOW_HEIGHT);
            //System.out.println("Moving Down Pressed");
        }
        if (keysPressed.contains("D") && !collisionInDirection(characterX, characterY, wave.npcs, "D")) {
            character.moveRight(WINDOW_WIDTH);
            //System.out.println("Moving Right Pressed");
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
        // Score Keeping (And potentially keeping track of number of projectiles shot)
        score += charactersToRemove.size();
        hud.setScoreLabel(score);

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

    private boolean playableCharacterCollision(double characterX, double characterY, List<NonPlayableCharacter> npcs) {
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

            // Check for collision in any direction
            if (characterRight > testLeft && characterLeft < testRight &&
                    characterBottom > testTop && characterTop < testBottom) {
                return true;
            }
        }
        return false;
    }

    private void playableCharacterHit(long now) {
        if (playableCharacterCollision(character.getImageView().getX(), character.getImageView().getY(), wave.npcs)){
            if ((now - lastDamageTime) >= 1_000_000_000) { // 1 second in nanoseconds
                health -= 1;
                lastDamageTime = now;
                hud.healthUpdater(health, root);
                System.out.println("Got hit, -1 health");
            }
        }
    }


    private void handleCharacterHit(NonPlayableCharacter nonPlayableCharacter) {
        // Implement your logic here to handle the hit character
        // For example, remove character from the scene or perform other actions
        root.getChildren().remove(nonPlayableCharacter.getImageView()); // Remove character from scene graph
        // Optionally, perform other actions like reducing health, updating score, etc.
    }
}
