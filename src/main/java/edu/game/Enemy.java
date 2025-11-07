package edu.game;

import edu.engine.Assets;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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

    private static  final double TARGET_W = 68; // ширина врага на экране
    private static  final double TARGET_H = 60; // высота врага на экране
    private final Image sprite = Assets.image("enemy_01");  // 🔷 добавили

    // Выстрел Enemy
    private final List<Bullet> bullets = new ArrayList<>();
    private static final double BULLET_SPEED = 300; // пикс/сек вниз

    /*
    Индивидуальная стрельба: у каждого самолёта свой период стрельбы
    * */

    private final long basePeriod;
    private final long jitter;

    private long nextShot = 0;


    public Enemy(double x, double y) {
        this.x = x;
        this.y = y;

        double baseSec = ThreadLocalRandom.current().nextDouble(4.5, 6);
        this.basePeriod = (long)(baseSec * 1_000_000_000L);

        double jitSec = ThreadLocalRandom.current().nextDouble(0, 1.5);
        this.jitter = (long)(jitSec * 1_000_000_000L);
    }

    public void update(double dt, double worldW, long now){
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

        if(nextShot == 0){
            long phase = ThreadLocalRandom.current().nextLong(0, basePeriod + 1);
            nextShot = now + phase;
        }
        if (now >= nextShot){
            bullets.add(new Bullet(x + TARGET_W / 2, y + TARGET_H / 2, BULLET_SPEED));
            long extra = ThreadLocalRandom.current().nextLong(0, jitter + 1);
            nextShot = now + basePeriod + extra;
        }

        // обновление и удаление пуль
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()){
            Bullet b = it.next();
            b.update(dt);
            if (b.y > 1000){
                it.remove();
            }
        }
    }

    public void render (GraphicsContext g){
        double cx = x + TARGET_W / 2.0;
        double cy = y + TARGET_H / 2.0;

        g.save();
        g.translate(cx, cy);
        g.rotate(180); // поворачиваем спрайт на 180
        g.drawImage(sprite, -TARGET_W/2.0, -TARGET_H/2.0, TARGET_W, TARGET_H);
        g.restore();

        //NEW пули врагов
        g.setFill(javafx.scene.paint.Color.web("FF4B4B"));
        for(Bullet b : bullets){
            g.fillRoundRect(b.x - 2, b.y - 2, 4, 12, 4, 4);
        }

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
