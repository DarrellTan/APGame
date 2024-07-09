package com.example.gametest;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class HUD {
    public Image healthPng;
    public ImageView healthIcon1;
    public ImageView healthIcon2;
    public ImageView healthIcon3;

    public Label scoreLabel;
    public Label highscoreLabel;
    public Label timeLabel;

    public HUD(){
        // Setting Health Images
        healthPng = new Image(getClass().getResource("/ui/health.png").toExternalForm());
        healthIcon1 = new ImageView(healthPng);
        healthIcon2 = new ImageView(healthPng);
        healthIcon3 = new ImageView(healthPng);

        // Set size for Health Icons
        double healthIconSize = 40;
        healthIcon1.setFitWidth(healthIconSize);
        healthIcon1.setFitHeight(healthIconSize);

        healthIcon2.setFitWidth(healthIconSize);
        healthIcon2.setFitHeight(healthIconSize);

        healthIcon3.setFitWidth(healthIconSize);
        healthIcon3.setFitHeight(healthIconSize);

        // Position of Health Icon
        healthIcon1.setLayoutX(10);
        healthIcon1.setLayoutY(10);

        healthIcon2.setLayoutX(50);
        healthIcon2.setLayoutY(10);

        healthIcon3.setLayoutX(90);
        healthIcon3.setLayoutY(10);

        // Initialize Labels
        scoreLabel = new Label("Score: 0");
        highscoreLabel = new Label("Highscore: 0");
        timeLabel = new Label("Time: 0");

        // Setting Initial UI text
        // scoreLabel
        scoreLabel.setLayoutX(650);
        scoreLabel.setLayoutY(10);

        // highscoreLabel
        highscoreLabel.setLayoutX(650);
        highscoreLabel.setLayoutY(40);

        // timeDisplayLabel
        timeLabel.setLayoutX(650);
        timeLabel.setLayoutY(70);

        // Set font size and color for labels
        String labelStyle = "-fx-font-size: 16px; -fx-text-fill: #f0f0f0;"; // Soft white color
        scoreLabel.setStyle(labelStyle);
        highscoreLabel.setStyle(labelStyle);
        timeLabel.setStyle(labelStyle);
    }

    public void setScoreLabel(int score){
        scoreLabel.setText("Score: " + score);
    }

}
