package com.example.gametest;

import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Character2 {
    public static final double CHARACTER_SIZE = 50;
    public static final double MOVE_DISTANCE = 5;
    private ImageView imageView;

    public Character2(String imagePath) {
        Image characterImage = new Image(imagePath);
        imageView = new ImageView(characterImage);
        imageView.setFitWidth(CHARACTER_SIZE);
        imageView.setFitHeight(CHARACTER_SIZE);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void moveUp() {
        imageView.setY(imageView.getY() - MOVE_DISTANCE);
    }

    public void moveDown(double windowHeight) {
        imageView.setY(imageView.getY() + MOVE_DISTANCE);
    }

    public void moveLeft() {
        imageView.setX(imageView.getX() - MOVE_DISTANCE);
    }

    public void moveRight(double windowWidth) {
        imageView.setX(imageView.getX() + MOVE_DISTANCE);
    }

    public boolean collidesWith(Character other) {
        return imageView.getBoundsInParent().intersects(other.getImageView().getBoundsInParent());
    }

    public boolean collidesWith(Projectile4 projectile) {
        return imageView.getBoundsInParent().intersects(projectile.getprojectImageView().getBoundsInParent());
    }

    public Bounds getBoundingBox() {
        return imageView.getBoundsInParent();
    }
}
