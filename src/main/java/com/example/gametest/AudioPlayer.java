package com.example.gametest;
import javax.sound.sampled.*;
import java.net.URL;
import java.io.File;


public class AudioPlayer {
    private Clip clip;
    private FloatControl volumeControl;

    public void playMusic(String filePath) {
        try {
            // Load audio file as resource using class loader
            File musicPath = new File(filePath);

            // Get the absolute path of the file
            String absolutePath = musicPath.getAbsolutePath();

            // Print the absolute path
            System.out.println("The file is being looked for at: " + absolutePath);

            // Check if the file exists
            if (musicPath.exists()) {
                System.out.println("File exists at the specified location.");
            } else {
                System.out.println("File does not exist at the specified location.");
            }

            if (musicPath.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicPath);
                clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
                System.out.println("Audio played");
            }
            else {
                System.out.println("Can't find file");
            }



            volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMusicVolume(float volume) {
        if (volumeControl != null) {
            volumeControl.setValue(volume);
        }
    }

    public void stopMusic() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }
}

