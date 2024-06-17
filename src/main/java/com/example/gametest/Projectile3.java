package com.example.gametest;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Projectile3 {
    public static final int PROJECTILE_SIZE = 40;
    public static final int PROJECTILE_SPEED = 5;
    public ImageView projectileImageView;

    public Projectile3(String imagePath, double characterX, double characterY) {
        Image characterImage = new Image(imagePath);
        projectileImageView = new ImageView(characterImage);
        projectileImageView.setFitWidth(PROJECTILE_SIZE);
        projectileImageView.setFitHeight(PROJECTILE_SIZE);
        projectileImageView.setX(characterX);
        projectileImageView.setY(characterY);
    }

    public ImageView getprojectImageView() {
        return projectileImageView;
    }
}
