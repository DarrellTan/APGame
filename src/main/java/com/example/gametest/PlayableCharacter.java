package com.example.gametest;

import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class PlayableCharacter implements MouseListener {

    private int x, y;
    private List<Projectile> projectiles;

    // Import from previous Character
    public static final int CHARACTER_SIZE = 60;
    public static final int MOVE_DISTANCE = 5;
    public ImageView imageView;
    public Image pcLeft;
    public Image pcRight;

    private boolean facingRight = true; // Track facing direction

    public PlayableCharacter() {
        // Load images from resources
        pcLeft = new Image(getClass().getResource("/playableCharacter/pcLeft.png").toExternalForm());
        pcRight = new Image(getClass().getResource("/playableCharacter/pcRight.png").toExternalForm());

        imageView = new ImageView(pcRight); // Start facing right
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

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void moveLeft() {
        if (imageView.getX() - MOVE_DISTANCE >= 0) {
            if (facingRight) {
                imageView.setImage(pcLeft);
                facingRight = false;
            }
            imageView.setX(imageView.getX() - MOVE_DISTANCE);
        }
    }

    public void moveRight(double windowWidth) {
        if (imageView.getX() + MOVE_DISTANCE + CHARACTER_SIZE <= windowWidth) {
            if (!facingRight) {
                imageView.setImage(pcRight);
                facingRight = true;
            }
            imageView.setX(imageView.getX() + MOVE_DISTANCE);
        }
    }

    public void moveUp() {
        // Ensure character does not move above 116 pixels from the top
        if (imageView.getY() - MOVE_DISTANCE >= 116) {
            imageView.setY(imageView.getY() - MOVE_DISTANCE);
        }
    }

    public void moveDown(double windowHeight) {
        if (imageView.getY() + MOVE_DISTANCE + CHARACTER_SIZE <= windowHeight) {
            imageView.setY(imageView.getY() + MOVE_DISTANCE);
        }
    }


    public Bounds getBounds() {
        return imageView.getBoundsInParent();
    }

    public boolean collidesWith(NonPlayableCharacter other) {
        return getBounds().intersects(other.getBounds());
    }
}
