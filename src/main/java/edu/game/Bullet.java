package edu.game;

public class Bullet {
    public double x;
    public double y;
    public double vy; // скорость по Y (отрицательное значение - вверх)

    public Bullet(double x, double y, double vy) {
        this.x = x;
        this.y = y;
        this.vy = vy;
    }

    public void update(double dt) {y += vy * dt;}

    public boolean isOffscreen () {return y < -20;}
}
