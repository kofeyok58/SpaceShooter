package edu.engine;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneController {

    public static Stage primary;
    public static int WIDTH;
    public static int HEIGHT;

    private SceneController(){}
        public static void init(Stage stage, int width, int height){
                primary = stage;
                WIDTH = width;
                HEIGHT = height;
        }
        public static void set (Scene scene) {

            if (primary == null) return;

            // NEW: подключаем наш CSS, если ещё не подключён
            var url = SceneController.class.getResource("/styles/app.css");
            if (url != null) {
                String css = url.toExternalForm();
                if (!scene.getStylesheets().contains(css)) {
                    scene.getStylesheets().add(css);
                }
            }
            primary.setScene(scene);
            primary.centerOnScreen();

        }
}
