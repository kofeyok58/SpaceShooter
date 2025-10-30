package edu.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// простейший враг, в виде прямоугольника, который двигается по одной плоскости
public class Enemy {
    private double x;
    private double y;
    private double w = 42;
    private double h = 26;

    // скорость по X - что бы можно было сделать "шатание"
    private double vx = 45; // пикс/сек вправо
    private double vy = 0; // пикс/сек вниз


    public Enemy(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void update(double dt, double worldW){
        // простое "елозонье" по горизонтали

        x+=vx * dt;

        // отражаемся от краёв
        if(x<20){
            x = 20;
            vx = -vx;
        }else if(x + w > worldW - 20){
            x = worldW - 20 - w;
            vx = -vx;
        }
        // можно добавить медленное спускание
        // y += vy * dt;
    }

    public void render (GraphicsContext g){
        g.setFill(Color.web("#ff5C5C"));
        g.fillRoundRect(x, y, w, h, 6, 6);
        g.setFill(Color.web("#990000"));
        g.fillRect(x+10, y+6, 5, 5); // глазик
        g.fillRect(x+26, y+6, 5, 5); // глазик
    }

    // пригодится для коллизий
    public double getX() {return x;}
    public double getY() {return y;}
    public double getW() {return w;}
    public double getH() {return h;}
}
