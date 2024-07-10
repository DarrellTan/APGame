package com.example.gametest;

import java.io.*;

public class VolumeReadAndWriter {

    private String fileName;
    public float volume;

    public VolumeReadAndWriter() {
        this.fileName = "volume.bin";
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

    public void writeVolume(float volume) {
        try {
            // Write the high score to the binary file
            try (FileOutputStream fos = new FileOutputStream(fileName);
                 DataOutputStream dos = new DataOutputStream(fos)) {
                dos.writeFloat(volume);
                System.out.println("Volume " + volume + " has been written to " + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public float readVolume() {
        try (FileInputStream fis = new FileInputStream(fileName);
             DataInputStream dis = new DataInputStream(fis)) {
            // Read the high score from the binary file
            volume = dis.readFloat();
            System.out.println("Volume read from file: " + volume);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not read volume");
            return -10;
        }
        return volume;
    }
}
