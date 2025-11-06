package edu.game;

import edu.engine.SceneController;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

// Игрок: позиция, движение, стрельба
public class Player {
    private double x;
    private double y;

    private double speed = 400; // пикс/сек
    private int lives = 3;
    private int hp = 3;

    // стрельба

    private long lastShot = 0;
    private long fireDelay = 140_000_000L;

    private final List<Bullet> bullets = new ArrayList<>();

    public Player(double startX, double startY) {
        this.x = startX;
        this.y = startY;
    }

    public void update (double dt, long now, edu.engine.Keys keys){
        double vx = 0; double vy = 0;

        if (keys.isDown(KeyCode.A) || keys.isDown(KeyCode.LEFT)) vx -= speed;
        if (keys.isDown(KeyCode.D) || keys.isDown(KeyCode.RIGHT)) vx += speed;
        if (keys.isDown(KeyCode.W) || keys.isDown(KeyCode.UP)) vy -= speed;
        if (keys.isDown(KeyCode.S) || keys.isDown(KeyCode.DOWN)) vy += speed;

        x += vx * dt;
        y += vy * dt;

        // границы окна
        double W = SceneController.WIDTH;
        double H = SceneController.HEIGHT;

        if (x<32) x=32;
        if (x > W -32) x = W - 32;
        if (y < 80) y = 80;
        if (y < H - 80) y = H - 80;

        // стрельба
        if(keys.isDown(KeyCode.SPACE) && now - lastShot > fireDelay){
            bullets.add(new Bullet(x, y - 36, -700));
            lastShot = now;
        }

        // обновляем пули и чистим вылетевшие

        Iterator<Bullet> it = bullets.iterator();
        while(it.hasNext()){
            Bullet b = it.next();
            b.update(dt);
            if (b.isOffscreen()){
                it.remove();
            }
        }
    }

//    public void render (GraphicsContext g){
//
//    }

    public void render (GraphicsContext g){
        // корабль
        g.setFill(Color.web("#2E8BFF"));
        g.fillRoundRect(x-22, y-14, 44, 28, 10, 10);
        g.setFill(Color.web("#FF9500"));
        g.fillRect(x -6, y -24, 12, 12);

        // пули
        g.setFill(Color.web("#00C2FF"));
        for (Bullet b: bullets){
            g.fillRoundRect(b.x-2, b.y - 10, 4, 12, 4, 4);
        }
    }

    public int getLives(){return lives;}
    public int getHp(){return hp;}

    // пригодится позже, когда враги будут в нас стрелять

    public List<Bullet> getBullets(){return bullets;}

    public double getX(){return x;}
    public double getY(){return y;}
}
