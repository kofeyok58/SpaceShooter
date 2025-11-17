package edu.ui;

import edu.engine.HighScores;
import edu.engine.SceneController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.List;

public class HighScoreScene {
    public Scene create() {
        Label title = new Label("Таблица рекордов (ТОП-10)");
        title.getStyleClass().add("title-xl"); // крупный красивый заголовок

        // Заголовки колонок
        HBox header = new HBox(
                mkCell("#", 50, true),
                mkCell("Имя", 240, true),
                mkCell("Очки", 120, true),
                mkCell("Дата", 200, true)
        );
        header.setPadding(new Insets(8, 0, 8, 0));

        VBox rows = new VBox(4);
        rows.setFillWidth(true);

        List<HighScores.Entry> top = HighScores.top();
        int rank = 1;
        for (HighScores.Entry e : top) {
            HBox row = new HBox(
                    mkCell(String.valueOf(rank), 50, false),
                    mkCell(e.name, 240, false),
                    mkCell(String.valueOf(e.score), 120, false),
                    mkCell(e.date.replace('T', ' '), 200, false)
            );

            // стиль строки задаём через CSS-класс, а не через setStyle
            row.getStyleClass().add("score-row");
            rows.getChildren().add(row);
            rank++;
        }

        // сама «плашка» с таблицей (фон + скругление)
        VBox tableBox = new VBox(8, header, rows);
        tableBox.getStyleClass().add("score-panel");

        Button back = new Button("Назад");
        back.getStyleClass().addAll("btn", "btn-ghost"); // NEW
        back.setOnAction(e -> SceneController.set(new MainMenuScene().create()));

        VBox root = new VBox(16, title, header, rows, back);
        root.setPadding(new Insets(24));
        root.setAlignment(Pos.TOP_CENTER);
        // NEW: фон
        root.getStyleClass().add("screen-dark");

        Scene scene = new Scene(root, SceneController.WIDTH, SceneController.HEIGHT);
        return scene;
    }

    // маленький помощник для ячеек
    private Pane mkCell(String text, double width, boolean header) {
        Label l = new Label(text);
        l.setFont(Font.font(header ? 16 : 14));
        l.setStyle(header
                ? "-fx-font-weight: bold;"
                : "-fx-opacity: 0.95;");
        l.setMaxWidth(Double.MAX_VALUE);

        StackPane box = new StackPane(l);
        box.setPrefWidth(width);
        StackPane.setAlignment(l, Pos.CENTER_LEFT);
        return box;
    }
}

