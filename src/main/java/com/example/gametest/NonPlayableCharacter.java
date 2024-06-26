package com.example.gametest;

import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class NonPlayableCharacter {
    public static final int CHARACTER_SIZE = 40;
    public static final int MOVE_DISTANCE = 5;
    public int health;

    public ImageView imageView;

    public NonPlayableCharacter(String imagePath, double X, double Y) {
        Image characterImage = new Image(imagePath);
        imageView = new ImageView(characterImage);
        imageView.setFitWidth(CHARACTER_SIZE);
        imageView.setFitHeight(CHARACTER_SIZE);
        imageView.setX(X);
        imageView.setY(Y);
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

    public boolean collidesWith(NonPlayableCharacter other) {
        return getBounds().intersects(other.getBounds());
    }

    public Bounds getBoundingBox() {
        return imageView.getBoundsInParent();
    }

    // New Movement Generation

    public void moveTo(double x, double y) {
        this.getImageView().setX(x);
        this.getImageView().setY(y);
    }

    public double getX() {
        return this.getImageView().getX();
    }

    public double getY() {
        return this.getImageView().getY();
    }

    public void move(double dx, double dy) {
        this.getImageView().setX(this.getX() + dx);
        this.getImageView().setY(this.getY() + dy);
    }
}
