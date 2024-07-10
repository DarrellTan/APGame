package com.example.gametest;

import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.example.gametest.GameApp.*;

public class WaveManager {
    private Pane root;
    private double windowWidth;
    private double windowHeight;
    public List<NonPlayableCharacter> npcs;
    private Random random;
    private int waveNumber;
    public double npcSpeed = 30; // Pixels per second
    private long lastWaveTime = 0;

    public Image startNpcLeft = new Image(getClass().getResource("/nonPlayableCharacter/npcLeft.png").toExternalForm());
    public Image startNpcRight = new Image(getClass().getResource("/nonPlayableCharacter/npcRight.png").toExternalForm());
    private PlayableCharacter character;

    public WaveManager (Pane root, double windowWidth, double windowHeight) {
        this.root = root;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.npcs = new ArrayList<>();
        this.random = new Random();
        this.waveNumber = 1;
    }

    public void update(long now) {
        if (now - lastWaveTime > 10_000_000_000L) { // 5 seconds per wave
            spawnWave();
            lastWaveTime = now;
        }
        updateNpcPosition(now, character);
    }

    private void spawnWave() {
        int maxNPCs = 10; // Maximum NPCs allowed at a time
        int currentNPCs = npcs.size(); // Current number of active NPCs

        if (currentNPCs >= maxNPCs) {
            return; // Exit if already at maximum NPCs
        }

        int numberOfEnemies = waveNumber + 5; // Increase enemies per wave
        int NPCsToAdd = Math.min(maxNPCs - currentNPCs, numberOfEnemies); // Calculate NPCs to add

        double characterX = character.getImageView().getX() + NonPlayableCharacter.CHARACTER_SIZE / 2;
        double characterY = character.getImageView().getY() + NonPlayableCharacter.CHARACTER_SIZE / 2;
        double safeRadius = 300;

        for (int i = 0; i < NPCsToAdd; i++) {
            double x, y;
            do {
                x = random.nextDouble() * (windowWidth - NonPlayableCharacter.CHARACTER_SIZE);
                y = random.nextDouble() * (windowHeight - NonPlayableCharacter.CHARACTER_SIZE);
            } while (distance(x, y, characterX, characterY) < safeRadius);

            NonPlayableCharacter npc = new NonPlayableCharacter(startNpcRight, x, y);
            npcs.add(npc);
            root.getChildren().add(npc.getImageView());
        }

        // Increment wave number
        waveNumber++;

        // Check if it's a multiple of 5 to increase npcSpeed
        if (waveNumber % 5 == 0) {
            npcSpeed += 10;
            System.out.println("NPC Speed increased to: " + npcSpeed);
        }

        // Debugging statement to check the size of npcs list
        System.out.println("NPCs in wave.npcs after spawning wave: " + npcs.size());
    }

    // Helper method to calculate the distance between two points
    private double distance(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }



    public void setCharacter(PlayableCharacter character) {
        this.character = character;
    }

    private void updateNpcPosition(long now, PlayableCharacter character) {
        for (NonPlayableCharacter npc : npcs) {
            double dt = 1.0 / 60.0;

            // Calculate the desired target position for the NPC
            double targetX = character.getImageView().getX();
            double targetY = character.getImageView().getY();

            // Calculate the direction vector from NPC to character
            double directionX = targetX - npc.getImageView().getX();
            double directionY = targetY - npc.getImageView().getY();
            double length = Math.sqrt(directionX * directionX + directionY * directionY);

            // Normalize the direction vector (optional, but ensures smoother movement)
            if (length != 0) {
                directionX /= length;
                directionY /= length;
            }

            // Calculate the desired movement amount based on NPC speed and elapsed time
            double dx = directionX * npcSpeed * dt;
            double dy = directionY * npcSpeed * dt;

            // Determine the direction of movement and change the image accordingly
            if (dx > 0) {
                npc.setImage(npc.npcRight);
            } else if (dx < 0) {
                npc.setImage(npc.npcLeft);
            }

            // Update the NPC's position with boundary checks
            double newX = npc.getImageView().getX() + dx;
            double newY = npc.getImageView().getY() + dy;

            // Ensure NPCs stay within window boundaries and above 116 pixels from the top
            newX = Math.max(0, Math.min(newX, WINDOW_WIDTH - NonPlayableCharacter.CHARACTER_SIZE));
            newY = Math.max(116, Math.min(newY, WINDOW_HEIGHT - NonPlayableCharacter.CHARACTER_SIZE));

            // Move the NPC
            npc.moveTo(newX, newY);
        }
    }

}