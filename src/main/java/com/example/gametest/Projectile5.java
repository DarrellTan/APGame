package com.example.gametest;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Projectile5 {

    private static final double PROJECTILE_SIZE = 40;
    private static final double PROJECTILE_SPEED = 10;

    private ImageView projectileImageView;
    private String direction;

    public Projectile5(String imagePath, double characterX, double characterY, String direction) {
        Image characterImage = new Image(imagePath);
        projectileImageView = new ImageView(characterImage);
        projectileImageView.setFitWidth(PROJECTILE_SIZE);
        projectileImageView.setFitHeight(PROJECTILE_SIZE);
        projectileImageView.setX(characterX);
        projectileImageView.setY(characterY);
        this.direction = direction;
    }

    public ImageView getProjectileImageView() {
        return projectileImageView;
    }

    public void move() {
        switch (direction) {
            case "UP":
                projectileImageView.setY(projectileImageView.getY() - PROJECTILE_SPEED);
                break;
            case "DOWN":
                projectileImageView.setY(projectileImageView.getY() + PROJECTILE_SPEED);
                break;
            case "LEFT":
                projectileImageView.setX(projectileImageView.getX() - PROJECTILE_SPEED);
                break;
            case "RIGHT":
                projectileImageView.setX(projectileImageView.getX() + PROJECTILE_SPEED);
                break;
        }
    }

    public boolean collidesWith(Character2 character) {
        return projectileImageView.getBoundsInParent().intersects(character.getImageView().getBoundsInParent());
    }


}
