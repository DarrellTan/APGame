package com.example.gametest;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class Projectiles2 {
    int x;
    int y;
    ImageView bullet;
    int XSpeed;
    int YSpeed;

    public Projectiles2(int px, int py, int PXSpeed, int PYSpeed) {
        x = px;
        y = py;
        XSpeed = PXSpeed;
        YSpeed = PYSpeed;
        Image image = new Image("projectile.png"); // Ensure the path is correct
        bullet = new ImageView(image);
        bullet.setX(x);
        bullet.setY(y);
    }


    public void display(Pane pane) {
        if (!pane.getChildren().contains(bullet)) {
            pane.getChildren().add(bullet);
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    void Pmove() {
        x = x + XSpeed;
        y = y + YSpeed;
    }




    boolean erase() { // if bullet moves off screen to remove it
        if (y - YSpeed < 0 || x + XSpeed > 9999 || x - XSpeed < 0 || y + YSpeed > 9999) {
            return true;
        } else {
            return false;
        }
    }


}