package edu;

import edu.engine.SceneController;
import edu.ui.MainMenuScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage){
        stage.setTitle("Космо шутер");
        stage.setResizable(false);

        // === УСТАНАВЛИВАЕМ ИКОНКУ ===
        Image icon = new Image(getClass().getResource("/icon/app_icon.png").toExternalForm());
        stage.getIcons().add(icon);
        // ============================

        SceneController.init(stage, 520, 980);     // размер игрового поля
//        SceneController.init(stage, 520, 580);     // размер игрового поля
        Scene menu = new MainMenuScene().create();
        SceneController.set(menu);

        stage.show();
    }

    public static void main(String[] args) {launch(args);}
}