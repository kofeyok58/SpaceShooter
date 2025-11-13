package edu.game;

import edu.engine.Assets;
import edu.engine.SceneController;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Игрок: позиция, движение, стрельба
public class Player {
    private double x;
    private double y;

    /*
     * Параметры здоровья и жизни корабля
    * */
    private double speed = 400; // пикс/сек
    public static final int MAX_HP = 3;
    public static final int MAX_LIVES = 3;

    private int lives = MAX_LIVES;
    private int hp = MAX_HP;
    /*
     * Небольшая неуязвимость после попадания
    * */
    private static final long INVULN = 900_000_000L; // ~0.9 секунд
    private long lastHitAt = 1;


    // стрельба

    private long lastShot = 0;
    private long fireDelay = 140_000_000L;

    private final List<Bullet> bullets = new ArrayList<>();

    private static final double TARGET_H = 70;
    private static final double TARGET_W = 70;
    private final Image sprite = Assets.image("player_ship");

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

        // границы с учетом размера спрайта
        double halfW = TARGET_W/ 2.0;
        double halfH = TARGET_H/ 2.0;
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

    /**
     * Попадание по игроку (с учетом неуязвимости)
     */
    public void hit (long now){
        if (isInvulnerable(now)) return;

        hp -= 1;
        lastHitAt = now;

        if (hp <= 0){
            lives -= 1;
            hp = (lives > 0) ? MAX_HP : 0;
        }
    }

    public boolean isAlive () {return lives > 0 && hp > 0;}
    public boolean isInvulnerable(long now){return  lastHitAt > 0 && (now - lastHitAt) < INVULN;}


    public void render (GraphicsContext g, long now){

//        // корабль
//        g.setFill(Color.web("#2E8BFF"));
//        g.fillRoundRect(x-22, y-14, 44, 28, 10, 10);
//        g.setFill(Color.web("#FF9500"));
//        g.fillRect(x -6, y -24, 12, 12);

        // легкое мигание при неуязвимости
        boolean inv = isInvulnerable(now);
        if (inv) {
            // миганим 6 раз в секунду
            long t = (now / 100_000_000L) % 2;  // ~ каждые 0.1 секунду
            if (t == 0) g.setGlobalAlpha(0.5);
        }
        // спрайт корабля
        g.drawImage(sprite, x - TARGET_W / 2.0, y - TARGET_H / 2.0, TARGET_W, TARGET_H);
        if(inv) g.setGlobalAlpha(1);

        // пули
        g.setFill(Color.web("#00C2FF"));
        for (Bullet b: bullets){
            g.fillRoundRect(b.x-2, b.y - 10, 4, 12, 4, 4);
        }
    }

    /*
    * Прямоугольник игрока для коллизии
    * */
    public double getLeft(){return x - TARGET_W / 2.0;}
    public double getTop(){return y - TARGET_H / 2.0;}
    public double getWidth(){return TARGET_W;}
    public double getHeight(){return TARGET_H;}

    public int getLives(){return lives;}
    public int getHp(){return hp;}

    public List<Bullet> getBullets(){return bullets;}

    public double getX(){return x;}
    public double getY(){return y;}
}
