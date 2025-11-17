package edu;

import edu.engine.*;
import edu.game.Bullet;
import edu.game.Enemy;
import edu.game.EnemyStrong;
import edu.game.Player;
import edu.ui.MainMenuScene;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GameScene {

    private static final double W = SceneController.WIDTH;
    private static final double H = SceneController.HEIGHT;

    private final Keys keys = new Keys();
    private AnimationTimer loop;
    private boolean paused = false;
    private boolean gameOver = false; // статус «проигрыш»

    /**
     * NEW: поле счёта
    * */

    private final Score score = new Score();
    private final Starfield starfield = new Starfield(120, W, H); // NEW Звездное небо

    // NEW: флаг — игрок в процессе «умирания» (взрыв воспроизводится)
    private boolean playerDying = false; // NEW

    // NEW: список активных взрывов
    private final List<Explosion> explosions = new ArrayList<>(); // NEW

    private final Player player = new Player(W/2.0, H -140);

    // 👇 вот тут появились враги
    private final List<Enemy> enemies = new ArrayList<>();

    private boolean nameAsked = false;

    // NEW: простая волновая система
    private int  wave = 0;                           // номер текущей волны
    private boolean waitingNextWave = false;         // ждём старт следующей волны
    private long nextWaveAtNs = 0;                   // время старта следующей волны
    private static final long WAVE_DELAY_NS = 2_000_000_000L; // 2 сек. пауза между волнами

    public Scene create() {
        Canvas canvas = new Canvas(W, H);
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setImageSmoothing(true);

        // --- Оверлей паузы ---
        Button resume = new Button("Продолжить");
        Button toMenu = new Button("Выйти в меню");

        resume.getStyleClass().addAll("btn", "btn-primary"); // NEW
        toMenu.getStyleClass().addAll("btn", "btn-ghost");   // NEW

        VBox pauseOverlay = new VBox(12, resume, toMenu);
        pauseOverlay.setAlignment(Pos.CENTER);
        pauseOverlay.setPadding(new Insets(10));
        pauseOverlay.setVisible(false);
        pauseOverlay.setMouseTransparent(true);

        // NEW:
        pauseOverlay.getStyleClass().add("card");

        // --- Оверлей Game Over ---
        Label lostLbl = new Label("Вы проиграли");
        lostLbl.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        Button restart = new Button("Начать заново?");
        restart.getStyleClass().addAll("btn", "btn-primary"); // NEW
        VBox gameOverOverlay = new VBox(16, lostLbl, restart);
        gameOverOverlay.setAlignment(Pos.CENTER);
        gameOverOverlay.setPadding(new Insets(10));
        gameOverOverlay.setVisible(false);
        gameOverOverlay.setMouseTransparent(true);
        gameOverOverlay.getStyleClass().add("card"); // NEW

        StackPane root = new StackPane(canvas, pauseOverlay, gameOverOverlay);
        Scene scene = new Scene(root, W, H, Color.WHITE);

        Music.play("battle_Theme1", 0.30);

        keys.attach(scene);

        scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (gameOver) return; // при экране проигрыша не реагируем на ESC
            if (e.getCode() == KeyCode.ESCAPE) {
                paused = !paused;
                pauseOverlay.setVisible(paused);
                pauseOverlay.setMouseTransparent(!paused);
            }
        });

        resume.setOnAction(e -> {
            paused = false;
            pauseOverlay.setVisible(false);
            pauseOverlay.setMouseTransparent(true);
        });

        toMenu.setOnAction(e -> {
            if (loop != null) loop.stop();
            SceneController.set(new MainMenuScene().create());
        });

        restart.setOnAction(e -> {
            if (loop != null) loop.stop();
            SceneController.set(new GameScene().create()); // перезапускаем игру
        });

        /**
         * NEW: стартуем с 1-й волны
        * */
        spawnWave(1);
        wave = 1;
        waitingNextWave = false;

        loop = new AnimationTimer() {
            long prev = 0;

            @Override
            public void handle(long now) {
                if (prev == 0) { prev = now; return; }
                double dt = Math.min((now - prev) / 1_000_000_000.0, 0.05);
                prev = now;

                if (!paused && !gameOver) {

                    starfield.update(dt); // NEW — до обновления врагов/игрока или после — неважно

                    // NEW Если игрок ещё не в фазе «умирания», обновляем управление игроком
                    if (!playerDying) {
                        player.update(dt, now, keys);
                    }

                    // Враги
                    for (Enemy enemy : enemies) {
                        enemy.update(dt, W, now);
                    }

                    // Коллизии: пули игрока → враги
                    checkBulletEnemyCollisions();

                    // NEW Коллизии: пули врагов → игрок
                    if (!playerDying) {
                        checkEnemyBulletsVsPlayer(now);
                    }

                    /**
                     * NEW: апдейт взрывов и чистка завершившихся
                    * */

                    for (int i = explosions.size() - 1; i >= 0; i--) { // NEW
                        Explosion ex = explosions.get(i);
                        ex.update(dt);
                        if (ex.isFinished()) explosions.remove(i);
                    }

                    /**
                     * NEW: если игрок не жив и ещё не установлен флаг dying — запускаем взрыв игрока
                     **/

                    if (!player.isAlive() && !playerDying) {
                        playerDying = true; // отметили, что игрок в фазе гибели
                        // центр игрока
                        double cx = player.getX();
                        double cy = player.getY();
                        // добавляем крупный взрыв
                        explosions.add(new Explosion(cx, cy, 32, 1.0, 300));
                        // (не выставляем gameOver=true пока взрыв не завершится)
                    }

                    // NEW: когда игрок в dying и все explosions для игрока закончились -> Game Over
                    if (playerDying && explosions.isEmpty()) {
                        gameOver = true;
                        paused = true;

                        if (!nameAsked) {
                            nameAsked = true; // чтобы не заспавнить несколько диалогов

                            Platform.runLater(() -> {
                                // диалог уже НЕ в фазе анимации
                                javafx.scene.control.TextInputDialog dlg = new javafx.scene.control.TextInputDialog("Player");
                                dlg.setTitle("Новый результат");
                                dlg.setHeaderText("Ваш счёт: " + score.get());
                                dlg.setContentText("Введите имя для таблицы рекордов:");
                                java.util.Optional<String> res = dlg.showAndWait();
                                String name = res.orElse("Player").trim();
                                if (name.isEmpty()) name = "Player";

                                HighScores.add(name, score.get());

                                // теперь спокойно показываем оверлей Game Over
                                lostLbl.setText("Вы проиграли\nСчёт: " + score.get());
                                lostLbl.setStyle("-fx-font-size: 48px; -fx-text-fill: white;");
                                gameOverOverlay.setVisible(true);
                                gameOverOverlay.setMouseTransparent(false);
                            });
                        }
                    }

                    // Очистка врагов, ушедших за экран (по желанию)
                    enemies.removeIf(e -> e.getY() > H + 40);

                    /**
                     * NEW: если врагов нет — планируем следующую волну через паузу
                    * */

                    if (!playerDying && !gameOver) {
                        if (enemies.isEmpty() && !waitingNextWave) {
                            waitingNextWave = true;
                            nextWaveAtNs = now + WAVE_DELAY_NS;
                        }
                        if (waitingNextWave && now >= nextWaveAtNs) {
                            wave++;
                            spawnWave(wave);
                            waitingNextWave = false;
                        }
                    }
                }
                render(g, now);
            }
        };

        loop.start();
        return scene;
    }

    private void render (GraphicsContext g, long now){
        /**
         * NEW Поменять цвет фона с белого на тёмный космос.
        **/
        g.setFill(Color.web("#0B0F1A"));
        g.fillRect(0, 0, W, H);
        starfield.render(g);
        // Враги

        for (Enemy enemy : enemies){
            enemy.render(g);
        }
        // игрок и его пули
        // NEW: если игрок в процессе взрыва — не рисуем его (рисуем только взрывы)
        if (!playerDying) { // NEW
            player.render(g, now); // обычный рендер игрока
        } // NEW

        /**
         * NEW: рендер взрывов поверх
        * */
        for (Explosion ex : explosions) { // NEW
            ex.render(g);
        }

        /**
        * HUD: Жизни и HP
        **/
        g.setFill(Color.WHITE);
        String hearts = "❤".repeat(Math.max(0, player.getLives()));
        g.fillText("Жизни: " + hearts, 12, 24);
        g.fillText("HP: " + player.getHp() + "/" + Player.MAX_HP, 12, 44);

        /**
         * NEW - Выводим счёт и номер волны
        * */

        g.fillText("Очки: " + score.get(), W - 80, 24);
        g.fillText("Волна: " + wave, W - 140, 44);            // NEW: номер волны
    }

    /**
     * Волновой спавн с авто-центрированием по ширине
     * + растущее количество EnemyStrong (до 40% от всех врагов),
     * сильные враги появляются в СЛУЧАЙНЫХ клетках сетки.
     */
    private void spawnWave(int waveNum) {
        enemies.clear();

        // рост сложности: от 2 до 6 рядов, от 4 до 8 колонок
        int rows = Math.min(2 + (waveNum / 2), 6);
        int cols = Math.min(4 + ((waveNum - 1) % 4), 8);

        double startY = 100;
        double gapY   = 70;
        double gapX   = 100;
        double enemyW = 68; // визуальная ширина врага
        double gridW  = (cols - 1) * gapX + enemyW;
        double startX = Math.max(20, (W - gridW) / 2.0);

        int total = rows * cols;

        // максимум 40% сильных
        int maxStrong = (int) Math.floor(total * 0.5);

        // сколько хотим сильных на этой волне (постепенно растёт)
        int targetStrong = Math.min(1 + waveNum / 2, maxStrong);

        int remainingStrong = targetStrong;
        int remainingCells  = total;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double x = startX + c * gapX;
                double y = startY + r * gapY;

                boolean makeStrong = false;

                if (remainingStrong > 0) {
                    // Вероятность сделать текущую клетку сильной
                    double p = (double) remainingStrong / (double) remainingCells;
                    if (ThreadLocalRandom.current().nextDouble() < p) {
                        makeStrong = true;
                        remainingStrong--;
                    }
                }

                remainingCells--;

                if (makeStrong) {
                    enemies.add(new EnemyStrong(x, y)); // сильный враг (3 попадания, 300 очков)
                } else {
                    enemies.add(new Enemy(x, y));       // обычный враг (1 попадание, 100 очков)
                }
            }
        }
    }

        private void spawnEnemies() {
            enemies.clear();
            int row = 4;
            int cols = 5;
            double startX = 80;
            double startY = 120;
            double gapX = 90;
            double gapY = 60;

            for (int r=0; r<row; r++){
                for (int c=0; c<cols; c++){
                    double x = startX + c * gapX;
                    double y = startY + r * gapY;
                    enemies.add(new Enemy(x, y));
                }
            }
        }
        /*
        Простейшая проверка пересечения прямоугольника
        * */

    /** Пули игрока → враги (AABB) + счёт + ВЗРЫВ + поддержка EnemyStrong */
    private void checkBulletEnemyCollisions() {
        List<Bullet> bullets = player.getBullets();

        // идём по врагам с конца, чтобы безопасно удалять
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy e = enemies.get(i);
            double ex = e.getX(), ey = e.getY(), ew = e.getW(), eh = e.getH();

            // идём по пулям тоже с конца
            for (int j = bullets.size() - 1; j >= 0; j--) {
                Bullet b = bullets.get(j);

                double bx = b.x - 2;
                double by = b.y - 10;
                double bw = 4;
                double bh = 12;

                boolean hit = bx < ex + ew && bx + bw > ex &&
                        by < ey + eh && by + bh > ey;

                if (hit) {
                    // сначала убираем пулю
                    bullets.remove(j);

                    // центр врага — для взрыва
                    double cx = ex + ew / 2.0;
                    double cy = ey + eh / 2.0;

                    // по умолчанию считаем, что враг умрёт с одного попадания
                    boolean dead = true;
                    int points = 100;
                    boolean strong = false;

                    // если это сильный враг — уменьшаем ему HP,
                    // и он умрёт только когда hp <= 0
                    if (e instanceof EnemyStrong strongEnemy) {
                        strong = true;
                        dead = strongEnemy.hit(); // вернёт true, только когда HP кончатся
                        points = 300;             // за сильного врага 300 очков
                    }

                    // взрыв — можно сделать чуть мощнее для сильного
                    explosions.add(new Explosion(
                            cx,
                            cy,
                            strong ? 22 : 18,   // размер
                            strong ? 0.75 : 0.65,
                            strong ? 320 : 280  // скорость разлёта
                    ));

                    // удаляем врага только если он реально "умер"
                    if (dead) {
                        enemies.remove(i);
                        score.add(points);
                    }

                    // выходим из цикла по пулям — этот враг уже обработан
                    break;
                }
            }
        }
    }

    /**
     * Проверка столкновений пуль врагов с игроком.
     * Простая AABB-коллизия (прямоугольники по осям).
     */
    private void checkEnemyBulletsVsPlayer(long now) {
        // Границы игрока
        double px = player.getLeft();
        double py = player.getTop();
        double pw = player.getWidth();
        double ph = player.getHeight();

        // Проходим по всем пулям всех врагов
        for (Enemy e : enemies) {
            List<Bullet> bullets = e.getBullets();
            for (int j = bullets.size() - 1; j >= 0; j--) {
                Bullet b = bullets.get(j);

                /*
                Размер пули, как в Player.render (4x12) и ее позиция от центра
                * */

                double bx = b.x - 2;
                double by = b.y - 10;
                double bw = 4;
                double bh = 12;

                boolean hit = bx < px + pw && bx + bw > px &&
                        by < py + ph && by + bh > py;

                if (hit) {
                    // Удаление и продолжаем со следующим врагом
                    bullets.remove(j);


                    player.hit(now);     // игрок получает урон/неуязвимость учитывается внутри
                    // здесь врага НЕ удаляем

                }
            }
        }
    }
}
