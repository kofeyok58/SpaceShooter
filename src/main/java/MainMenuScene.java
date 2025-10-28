import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MainMenuScene {
    public Scene create(){
        Label title = new Label("КОСМО ШУТЕР"); // название в главном меню
        title.setStyle("-fx-font-size: 36px; -fx-font-weight: bold");
        Button start = new Button("СТАРТ");
        Button score = new Button("РЕКОРДЫ");
        Button author = new Button("АВТОР");
        Button exit = new Button("ВЫХОД");

        start.setMaxWidth(Double.MAX_VALUE);
        score.setMaxWidth(Double.MAX_VALUE);
        author.setMaxWidth(Double.MAX_VALUE);
        exit.setMaxWidth(Double.MAX_VALUE);

        VBox box = new VBox(16, title, start, score, author, exit);
        box.setPadding(new Insets(24));
        box.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene (box, SceneController.WIDTH, SceneController.HEIGHT);
        return scene;

    }

}
