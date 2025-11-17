package edu.ui;

import edu.engine.Assets;
import edu.engine.SceneController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class AuthorScene {
    public Scene create() {
        Label title = new Label("Автор: Купневич Злата");
        title.getStyleClass().add("title-lg"); // NEW

        // NEW === ЗАГРУЖАЕМ КАРТИНКУ ===
        Image authorImg = Assets.image("author_img2"); // файл textures/author_img.png
        ImageView authorView = new ImageView(authorImg);

        authorView.setPreserveRatio(true);
        authorView.setFitWidth(500); // Можешь менять размер

        // Лёгкая тень для эффекта
        DropShadow ds = new DropShadow();
        ds.setRadius(20);
        ds.setOffsetY(5);
        ds.setColor(Color.color(0, 0, 0, 0.6));
        authorView.setEffect(ds);

        Button back = new Button("Назад");
        back.getStyleClass().addAll("btn", "btn-ghost"); // NEW

        VBox root = new VBox(16, title,authorView, back );
        root.setPadding(new Insets(24));
        root.setAlignment(Pos.CENTER);

        // NEW: тёмный фон + карточка в центре
        root.getStyleClass().addAll("screen-dark", "card");

        Scene scene = new Scene(root, SceneController.WIDTH, SceneController.HEIGHT);
        back.setOnAction(e -> SceneController.set(new MainMenuScene().create()));
        return scene;
    }
}
