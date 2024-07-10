package com.example.gametest;

import javax.sound.sampled.*;
import java.io.File;

public class AudioPlayer {

    private Clip clip;
    private FloatControl volumeControl;

    public void playMusic(String filePath, float initialVolume) {
        try {
            // Load audio file as resource using class loader
            File musicPath = new File(filePath);

            if (musicPath.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicPath);
                clip = AudioSystem.getClip();
                clip.open(audioStream);

                volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                setMusicVolume(initialVolume); // Set initial volume

                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
                System.out.println("Audio played");
            } else {
                System.out.println("Can't find file");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMusicVolume(float volume) {
        if (volumeControl != null) {
            // Map the volume (0-100) to decibels (-80 to 6)
            float dB = (volume / 100.0f) * 86.0f - 80.0f;
            volumeControl.setValue(dB);
        }
    }

    public void stopMusic() {
        if (clip != null) {
            clip.stop();
            clip.close();
        }
    }
}
