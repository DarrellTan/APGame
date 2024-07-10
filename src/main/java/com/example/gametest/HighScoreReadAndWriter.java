package com.example.gametest;

import java.io.*;

public class HighScoreReadAndWriter {

    private String fileName;

    public HighScoreReadAndWriter() {
        this.fileName = "highscore.bin";
        ensureFileExists(); // Ensure file creation when the class is initialized
    }

    private void ensureFileExists() {
        File file = new File(fileName);
        try {
            // Check if the file exists; create if it doesn't
            if (!file.exists()) {
                file.createNewFile();
                System.out.println("File " + fileName + " created.");
            } else {
                System.out.println("File " + fileName + " already exists.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeHighscore(int highScore) {
        try {
            // Write the high score to the binary file
            try (FileOutputStream fos = new FileOutputStream(fileName);
                 DataOutputStream dos = new DataOutputStream(fos)) {
                dos.writeInt(highScore);
                System.out.println("High score has been written to " + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int readHighscore() {
        int highScore = 0;
        try (FileInputStream fis = new FileInputStream(fileName);
             DataInputStream dis = new DataInputStream(fis)) {
            // Read the high score from the binary file
            highScore = dis.readInt();
            System.out.println("High score read from file: " + highScore);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return highScore;
    }
}
