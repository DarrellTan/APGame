package com.example.gametest;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Projectile {
    private int x, y;
    private int speed;
    private int direction;
    private int damage;
    private Image image;

    public Projectile(int x, int y, int direction, int speed, int damage) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.speed = speed;
        this.damage = damage;
        try {
            this.image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("projectile.png"));
            if (this.image == null) {
                throw new IOException("Resource not found: projectile.png");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void move() {
        switch (direction) {
            case 0:
                x += speed;
                break;
            case 180:
                x -= speed;
                break;
            // Add more directions if necessary
        }
    }

    public void draw(Graphics g) {
        g.drawImage(image, x, y, null);
    }

    public boolean checkCollision(CharacterTest target) {
        // Simplified collision detection
        return target.getX() == x && target.getY() == y;
    }
}