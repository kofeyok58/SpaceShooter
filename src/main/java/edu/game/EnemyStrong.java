package edu.game;

import edu.engine.Assets;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.List;

public class EnemyStrong extends Enemy {

    private int hp = 3; // ← три попадания

    private static final double TARGET_W2 = 90; // крупнее обычного
    private static final double TARGET_H2 = 80;
    private final Image sprite2 = Assets.image("strong_enemy");

    public EnemyStrong(double x, double y) {
        super(x, y);
        // можешь добавить свою скорость, если хочешь
    }

    @Override
    public void render(GraphicsContext g) {
        double cx = getX() + TARGET_W2 / 2;
        double cy = getY() + TARGET_H2 / 2;

        g.save();
        g.translate(cx, cy);
        g.rotate(180);
        g.drawImage(sprite2, -TARGET_W2 / 2, -TARGET_H2 / 2, TARGET_W2, TARGET_H2);
        g.restore();

        // 🔴 РИСУЕМ ПУЛИ (как в Enemy.render)
        g.setFill(Color.web("#FF4B4B"));
        List<Bullet> bullets = getBullets();  // наследуемый метод из Enemy
        for (Bullet b : bullets) {
            g.fillRoundRect(b.x - 2, b.y - 2, 4, 12, 4, 4);
        }
    }


    @Override
    public double getW() { return TARGET_W2; }

    @Override
    public double getH() { return TARGET_H2; }

    /** Получает урон (уменьшение HP) */
    public boolean hit() {
        hp--;
        return hp <= 0;
    }
}