package com.example.gametest;


import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.Graphics;

public class CharacterTest implements MouseListener {

    private int x, y;
    private List<Projectile> projectiles;


    // Import from previous Character
    public static final int CHARACTER_SIZE = 40;
    public static final int MOVE_DISTANCE = 5;
    public ImageView imageView;


    public CharacterTest() {

        // Implementing Character
        Image characterImage = new Image("yena icon.jpeg");
        imageView = new ImageView(characterImage);
        imageView.setFitWidth(CHARACTER_SIZE);
        imageView.setFitHeight(CHARACTER_SIZE);
        imageView.setX(0);
        imageView.setY(0);
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) { // Mouse 1 is clicked
            System.out.println("Mouse Button Pressed");
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}



    public void draw(Graphics g) {
        for (Projectile p : projectiles) {
            p.draw(g);
        }
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
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

}

