package edu;

import edu.engine.Keys;
import edu.engine.SceneController;
import edu.game.Enemy;
import edu.game.Player;
import edu.game.Bullet;     // Нужен для доступа к пулям игрока
import edu.ui.MainMenuScene;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;


public class GameScene {

    private static final double W = SceneController.WIDTH;
    private static final double H = SceneController.HEIGHT;

    private final Keys keys = new Keys();
    private AnimationTimer loop;
    private boolean paused = false;

    private final Player player = new Player(W/2.0, H - 140);

    // 👇 вот тут появились враги
    private final List<Enemy> enemies = new ArrayList<>();

    public Scene create (){
        Canvas canvas = new Canvas(W, H);
        GraphicsContext g = canvas.getGraphicsContext2D();

        // 🔽 вот это должно быть сглаживание
        g.setImageSmoothing(true);

        // оверлей паузы
        Button resume = new Button("Продолжить");
        Button toMenu = new Button("Выйти в меню");
        VBox overlay = new VBox(12, resume, toMenu);
        overlay.setAlignment(Pos.CENTER);
        overlay.setPadding(new Insets(10));
        overlay.setVisible(false);
        overlay.setMouseTransparent(true);

        StackPane root = new StackPane(canvas, overlay);
        Scene scene = new Scene(root, W, H, Color.WHITE);

        keys.attach(scene);

        scene.addEventHandler(KeyEvent.KEY_PRESSED, e-> {
            if (e.getCode() == KeyCode.ESCAPE) {
                paused = !paused;
                overlay.setVisible(paused);
                overlay.setMouseTransparent(!paused);
            }
        });
        toMenu.setOnAction(e-> SceneController.set(new MainMenuScene().create()));

        // 👇 создаём врагов в начале сцены

        spawnEnemies();

        loop = new AnimationTimer() {
            long prev = 0;
            @Override
            public void handle(long now) {
                if (prev == 0){prev = now; return;}
                double dt = Math.min((now - prev)/1_000_000_000.0, 0.05);
                prev = now;

                if (!paused){
                    // игрок
                    player.update(dt, now, keys);
                    // враги
                    for (Enemy enemy : enemies){
                        enemy.update(dt, W);
                    }
                    /* NEW
                    столкновение пули игрока с врагом
                    * */

                    checkBulletEnemyCollision();

                    /* NEW
                    отчистка ушедших за экран врагов
                    * */

                    enemies.removeIf(e -> e.getY()> H+40);
                }
                render(g);
            }

        };

        loop.start();

        return scene;
    }

    private void render (GraphicsContext g){
        g.setFill(Color.WHITE);
        g.fillRect(0, 0, W, H);

        // HUD
        for (Enemy enemy : enemies){
            enemy.render(g);
        }

        player.render(g);

        }

    private void spawnEnemies() {
        enemies.clear();
        int row = 4;
        int cols = 5;
        double startX = 80;
        double startY = 120;
        double gapX = 90;
        double gapY = 60;

        for(int r = 0; r < row; r++) {
            for(int c=0; c < cols; c++) {
                double x = startX + c * gapX;
                double y = startY + r * gapY;
                enemies.add(new Enemy(x, y));
            }
        }
    }
    /* NEW
    Простейшая проверка пересечения прямоугольника
    * */

    private void checkBulletEnemyCollision(){
        List<Bullet> bullets = player.getBullets();

        /* NEW
        Идём с конца, что бы безопасно удалять элементы списков
        * */

        for (int i = enemies.size() - 1; i >= 0; i--){
            Enemy e = enemies.get(i);
            double ex = e.getX(), ey = e.getY(), ew = e.getW(), eh = e.getH();

            for (int j = bullets.size() - 1; j >= 0; j--){
                Bullet b = bullets.get(j);

                /* NEW
                Размер пули как в Player.render (4x12) и её позиция от центра
                * */
                double bx = b.x - 2;
                double by = b.y - 10;
                double bw = 4;
                double bh = 12;

                boolean hit = bx < ex + ew && bx + bw > ex &&
                        by < ey + eh && by + bh > ey;

                if(hit){
                    // удаление врага и пули
                    bullets.remove(j);
                    enemies.remove(i);
                }
            }
        }
    }
}
