package edu.game;

/*
* Простейший враг
* В виде прямоугольника, который двигается по одно плоскости.
* */

import edu.engine.Assets;
import edu.engine.Sound;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Enemy {
    private double x;
    private double y;
//    private double w = 42;
//    private double h = 26;

    // скорость по Х - что бы можно было сделать "шатание"

    private double vx = 45; // пикс/сек вправо
    private double vy = 10; // пикс/сек вниз

    // === добавлено: спрайт и целевой размер ===
    private static final double TARGET_W = 68; // ширина врага на экране
    private static final double TARGET_H = 60; // высота врага на экране
    private final Image sprite = Assets.image("enemy_01"); // 🔹 добавили

    // 🔴 выстрел врага

    private final List<Bullet> bullets = new ArrayList<>();
    private static final double BULLET_SPEED = 300; // пикс/сек вниз
    /*  Индивидуальная стрельба: свой период и джиттер на экземпляр
    Базовый период в наносекундах (случайно в пределах 4.5–6.0 сек)
    */
    private final long basePeriodNs;
    // Джиттер (0–1.5 сек) для разнообразия интервалов между выстрелами
    private final long jitterNs;

    // Момент времени следующего выстрела (наносекунды таймера)
    private long nextShotAtNs = 0; // 0 = не инициализировано (назначим при первом update)



    public Enemy(double x, double y) {
        this.x = x;
        this.y = y;

        /* Индивидуальные параметры “темпа” для каждого врага:
        basePeriod: 4.5..6.0 сек
        */
        double baseSec = ThreadLocalRandom.current().nextDouble(4.5, 6.0);
        this.basePeriodNs = (long)(baseSec * 1_000_000_000L);

        // NEW - jitter: 0..1.5 сек
        double jitSec = ThreadLocalRandom.current().nextDouble(0.0, 1.5);
        this.jitterNs = (long)(jitSec * 1_000_000_000L);

    }

    /**
     * @param dt   дельта времени в секундах
     * @param worldW ширина мира (для отражения от краёв)
     * @param now  текущее время из AnimationTimer (наносекунды)
     */

    public void update (double dt, double worldW, long now){
        // простое "елозание" по горизонтали

        x += vx * dt;

        //отражаемся от краёв
        if (x<20) {
            x=20;
            vx = -vx;
        } else if (x + TARGET_W > worldW - 20) {
            x = worldW - 20 - TARGET_W;
            vx = -vx;
        }
        //можно добавить медленное спускание
            y += vy * dt;


        // NEW - Первая инициализация фазой: первый выстрел через случайное время 0..basePeriod
        if (nextShotAtNs == 0) {
            long phase = ThreadLocalRandom.current().nextLong(0, basePeriodNs + 1);
            nextShotAtNs = now + phase;
        }

        // NEW - Стрельба: если пришло время — выпускаем пулю и назначаем новый дедлайн
        if (now >= nextShotAtNs) {
            bullets.add(new Bullet(x + TARGET_W / 2, y + TARGET_H, BULLET_SPEED));
            long extra = ThreadLocalRandom.current().nextLong(0, jitterNs + 1); // 0..jitter
            nextShotAtNs = now + basePeriodNs + extra;

            Sound.play("enemy_laser");
        }

        // NEW - обновление пуль врага
        Iterator<Bullet> it = bullets.iterator();
        while (it.hasNext()) {
            Bullet b = it.next();
            b.update(dt);
            if (b.y > 1000) { // если вылетела за экран вниз
                it.remove();
            }
        }
    }

    public void render(GraphicsContext g) {
        double cx = x + TARGET_W / 2.0;
        double cy = y + TARGET_H / 2.0;

        g.save();
        g.translate(cx, cy);
        g.rotate(180); // поворачиваем спрайт, чтобы "нос" смотрел вниз
        g.drawImage(sprite, -TARGET_W / 2.0, -TARGET_H / 2.0, TARGET_W, TARGET_H);
        g.restore();

        //  NEW - рисуем пули врагов (красные)
        g.setFill(javafx.scene.paint.Color.web("#FF4B4B"));
        for (Bullet b : bullets) {
            g.fillRoundRect(b.x - 2, b.y - 2, 4, 12, 4, 4);
        }
    }

    //пригодится для коллизий

    public double getX() {return x;}
    public double getY() {return y;}
    public double getW() {return TARGET_W;}
    public double getH() {return TARGET_H;}

    public List<Bullet> getBullets() { return bullets; }
}
