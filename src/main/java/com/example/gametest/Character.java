package com.example.gametest;

import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Character {
    public static final int CHARACTER_SIZE = 40;
    public static final int MOVE_DISTANCE = 5;
    public int health;

    public ImageView imageView;

    public Character(String imagePath) {
        Image characterImage = new Image(imagePath);
        imageView = new ImageView(characterImage);
        imageView.setFitWidth(CHARACTER_SIZE);
        imageView.setFitHeight(CHARACTER_SIZE);
        imageView.setX(0);
        imageView.setY(0);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void moveUp() {
        if (imageView.getY() - MOVE_DISTANCE >= 0) {
            imageView.setY(imageView.getY() - MOVE_DISTANCE);
        }
    }

    public void moveDown(double windowHeight) {
        if (imageView.getY() + MOVE_DISTANCE + CHARACTER_SIZE <= windowHeight) {
            imageView.setY(imageView.getY() + MOVE_DISTANCE);
        }
    }

    public void moveLeft() {
        if (imageView.getX() - MOVE_DISTANCE >= 0) {
            imageView.setX(imageView.getX() - MOVE_DISTANCE);
        }
    }

    public void moveRight(double windowWidth) {
        if (imageView.getX() + MOVE_DISTANCE + CHARACTER_SIZE <= windowWidth) {
            imageView.setX(imageView.getX() + MOVE_DISTANCE);
        }
    }

    public Bounds getBounds() {
        return imageView.getBoundsInParent();
    }

    public boolean collidesWith(Character other) {
        return getBounds().intersects(other.getBounds());
    }

    public Bounds getBoundingBox() {
        return imageView.getBoundsInParent();
    }
}
