package edu.engine;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Random;

public final class Starfield {
    private static final class Star {
        double x, y, speed, size, alpha;
    }

    private final Star[] stars;
    private final double width, height;
    private final Random rnd = new Random();

    public Starfield(int count, double width, double height) {
        this.width = width;
        this.height = height;
        this.stars = new Star[count];
        for (int i = 0; i < count; i++) {
            stars[i] = makeStar(rnd.nextDouble() * width, rnd.nextDouble() * height, i);
        }
    }

    private Star makeStar(double x, double y, int i) {
        Star s = new Star();
        boolean fast = (i % 3 == 0);     // ~1/3 — быстрые звезды (задний план «движется» глубже)
        s.x = x;
        s.y = y;
        s.size  = fast ? 2 : 1;
        s.speed = fast ? 120 : 60;       // px/s
        s.alpha = fast ? 0.9 : 0.6;
        return s;
    }

    public void update(double dt) {
        for (Star s : stars) {
            s.y += s.speed * dt;
            if (s.y > height) {
                s.y = -5;                          // появляемся сверху
                s.x = rnd.nextDouble() * width;
            }
        }
    }

    public void render(GraphicsContext g) {
        g.setFill(Color.WHITE);
        for (Star s : stars) {
            g.setGlobalAlpha(s.alpha);
            g.fillRect(s.x, s.y, s.size, s.size);
        }
        g.setGlobalAlpha(1.0);
    }
}