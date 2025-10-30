package edu.ui;

import edu.engine.SceneController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class HighScoreScene {
    public Scene create(){
        Label title = new Label("Таблица рекордов");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold");
        Button back = new Button("Назад");

        VBox root = new VBox(16,title,back);
        root.setPadding(new Insets(24));
        root.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(root, SceneController.WIDTH, SceneController.HEIGHT);
        back.setOnAction(e -> SceneController.set(new MainMenuScene().create()));
        return scene;
    }
}
