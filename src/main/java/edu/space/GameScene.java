package edu.space;

import edu.engine.SceneController;

public class GameScene {
    //размеры и базовые настройки
    private static  final double W  = SceneController.WIDTH;
    private static  final double H  = SceneController.HEIGHT;

    //корабль
    private double px = W/2.0, py = H -140;
    private double ps = 400;//скорость px/py (px/sec)
    private int lives =3;
    private int hp = 3;
    private long lastShotNanos =0;
    private long fireDelayNanos = 140_000_000L;//7 выстрелов/cек
    //пули игрока
    private  static class Bullet{
        
    }

}
