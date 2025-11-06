package edu.game;

import edu.engine.Assets;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

// простейший враг, в виде прямоугольника, который двигается по одной плоскости
public class Enemy {
    private double x;
    private double y;
    private double w = 42;
    private double h = 26;

    // скорость по X - что бы можно было сделать "шатание"
    private double vx = 45; // пикс/сек вправо
    private double vy = 10; // пикс/сек вниз

    /**
     Доюавим спрайт и целевой размер модели врага
     **/

    private static  final double TARGET_W = 56; // ширина врага на экране
    private static  final double TARGET_H = 48; // высота врага на экране
    private final Image sprite = Assets.image("enemy_01");  // 🔷 добавили

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
        }else if(x + TARGET_W > worldW - 20){
            x = worldW - 20 - TARGET_W;
            vx = -vx;
        }
        // можно добавить медленное спускание
         y += vy * dt;
    }

    public void render (GraphicsContext g){
        double cx = x + TARGET_W / 2.0;
        double cy = y + TARGET_H / 2.0;

        g.save();
        g.translate(cx, cy);
        g.rotate(180); // поворачиваем спрайт на 180
        g.drawImage(sprite, -TARGET_W/2.0, -TARGET_H/2.0, TARGET_W, TARGET_H);
        g.restore();

    }

//    public void render (GraphicsContext g){
//        g.setFill(Color.web("#ff5C5C"));
//        g.fillRoundRect(x, y, w, h, 6, 6);
//        g.setFill(Color.web("#990000"));
//        g.fillRect(x+10, y+6, 5, 5); // глазик
//        g.fillRect(x+26, y+6, 5, 5); // глазик
//    }


    // пригодится для коллизий
    public double getX() {return x;}
    public double getY() {return y;}
    public double getW() {return w;}
    public double getH() {return h;}
}
