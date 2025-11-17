package edu.ui;

import edu.GameScene;
import edu.engine.Assets;
import edu.engine.Music;
import edu.engine.SceneController;
import edu.engine.Sound;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;


public class MainMenuScene {
    public Scene create() {
        Label title = new Label("КОСМИЧЕСКИЙ ШУТЕР");
        title.getStyleClass().add("title-xl"); // NEW

        // === Корабль игрока в центре экрана ===
        Image shipImg = Assets.image("title_img"); // файл /textures/title_img.png
        ImageView shipView = new ImageView(shipImg);
        shipView.setPreserveRatio(true);
        shipView.setFitWidth(460); // размер на экране, можешь менять

       // лёгкий "3D"-эффект: тень под кораблём
        DropShadow ds = new DropShadow();
        ds.setRadius(25);
        ds.setOffsetY(10);
        ds.setColor(Color.color(0, 0, 0, 0.6));
        shipView.setEffect(ds);

        // Чтобы корабль оказался реально в центре, добавим "пустые" растягиваемые зоны
        Region spacerTop = new Region();
        Region spacerBottom = new Region();
        VBox.setVgrow(spacerTop, javafx.scene.layout.Priority.ALWAYS);
        VBox.setVgrow(spacerBottom, javafx.scene.layout.Priority.ALWAYS);

        Button start = new Button("СТАРТ");
        Button score = new Button("РЕКОРДЫ");
        Button author = new Button("АВТОР");
        Button exit = new Button("ВЫХОД");

        // NEW: стили кнопок
        start.getStyleClass().addAll("btn", "btn-primary");
        score.getStyleClass().addAll("btn", "btn-ghost");
        author.getStyleClass().addAll("btn", "btn-ghost");
        exit.getStyleClass().addAll("btn", "btn-danger");

        VBox box = new VBox(16, title, start, score, author, exit, spacerTop, shipView, spacerBottom);
        box.setPadding(new Insets(24));
        box.setAlignment(Pos.TOP_CENTER);
        box.setSpacing(16);

        // NEW: фон меню
        box.getStyleClass().add("screen-menu");

        Scene scene = new Scene (box, SceneController.WIDTH, SceneController.HEIGHT);

        // === Лёгкая вибрация корабля (готовим таймлайн) ===
        Timeline shake = new Timeline(
                new KeyFrame(Duration.millis(0),
                        new KeyValue(shipView.translateXProperty(), -4)),
                new KeyFrame(Duration.millis(70),
                        new KeyValue(shipView.translateXProperty(), 4))
        );
        shake.setAutoReverse(true);
        shake.setCycleCount(Timeline.INDEFINITE);

        // Особый звук для СТАРТА
        start.setOnAction(e -> {
            Sound.play("menu_start_button");   // ← особый звук на кнопку старт

            shipView.setTranslateX(0);  // сброс
            shake.playFromStart();      // запускаем вибрацию

            PauseTransition delay = new PauseTransition(Duration.seconds(3));

            delay.setOnFinished(ev -> SceneController.set(new GameScene().create()));

            Music.stop();
            delay.play();

        });


        // 2) Обычный клик для других кнопок
        score.setOnAction(e -> {
            Sound.play("menu_btn_click");
            SceneController.set(new HighScoreScene().create());
        });

        author.setOnAction(e -> {
            Sound.play("menu_btn_click");
            SceneController.set(new AuthorScene().create());
        });

        exit.setOnAction(e -> {
            Sound.play("menu_btn_click");
            System.exit(0);
        });

        Music.play("mainMune_Music_Theme", 0.40);
        return scene;

    }
}
