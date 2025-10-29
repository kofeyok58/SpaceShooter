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
    public static void set (Scene scene){
        primary.setScene(scene);
        primary.centerOnScreen();
    }

}

