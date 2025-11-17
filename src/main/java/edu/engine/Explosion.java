package edu.engine;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Random;

public final class Explosion {

    private static final class Particle {
        double x, y;      // позиция
        double vx, vy;    // скорость
        double life;      // оставшееся время (сек)
        double maxLife;   // изначальная длительность (сек)
        double size;      // базовый размер
    }

    private final Particle[] ps;
    private final Random rnd = new Random();
    private boolean finished = false;

    /**
     * @param x центр взрыва (px)
     * @param y центр взрыва (px)
     * @param count число частиц (например, 16–24)
     * @param maxLifeSec длительность (например, 0.5–0.8 c)
     * @param speed px/s (например, 220–320)
     */
    public Explosion(double x, double y, int count, double maxLifeSec, double speed) {
        ps = new Particle[count];
        for (int i = 0; i < count; i++) {
            ps[i] = new Particle();
            ps[i].x = x;
            ps[i].y = y;

            double angle = rnd.nextDouble() * Math.PI * 2.0;
            double sp = speed * (0.6 + rnd.nextDouble() * 0.8); // разброс скорости
            ps[i].vx = Math.cos(angle) * sp;
            ps[i].vy = Math.sin(angle) * sp;

            ps[i].maxLife = maxLifeSec * (0.7 + rnd.nextDouble() * 0.6); // разброс жизни
            ps[i].life = ps[i].maxLife;
            ps[i].size = 2.0 + rnd.nextDouble() * 3.0; // 2..5 px
        }
    }

    public void update(double dt) {
        if (finished) return;

        boolean anyAlive = false;
        for (Particle p : ps) {
            if (p.life <= 0) continue;

            p.life -= dt;
            if (p.life <= 0) {
                p.life = 0;
                continue;
            }

            // простая физика
            p.x += p.vx * dt;
            p.y += p.vy * dt;
            p.vy += 220 * dt; // чуть «гравитации», чтобы разлет был натуральнее

            anyAlive = true;
        }
        finished = !anyAlive;
    }

    public void render(GraphicsContext g) {
        // палитра «взрыва»: желтый → оранжевый → красный → дым (тёмный)
        for (Particle p : ps) {
            if (p.life <= 0) continue;

            double t = 1.0 - (p.life / p.maxLife); // 0..1
            // цвет по времени: от ярко-жёлтого к красно-оранжевому и затем к тёмному
            Color c;
            if (t < 0.3) {
                c = Color.color(1.0, 0.95, 0.3); // жёлтый
            } else if (t < 0.7) {
                c = Color.color(1.0, 0.55, 0.0); // оранжевый
            } else {
                c = Color.color(0.6, 0.2, 0.1);  // тёмно-красный/дым
            }
            double alpha = Math.max(0.0, 1.0 - t); // плавное затухание

            g.setGlobalAlpha(alpha);
            g.setFill(c);

            // лёгкое расширение с временем
            double s = p.size * (1.0 + t * 1.2);
            g.fillOval(p.x - s * 0.5, p.y - s * 0.5, s, s);
        }
        g.setGlobalAlpha(1.0);
    }

    public boolean isFinished() { return finished; }
}
