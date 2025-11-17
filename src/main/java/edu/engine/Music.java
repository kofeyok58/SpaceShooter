package edu.engine;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Music {

    private static MediaPlayer player;

    public static void play(String name, double volume) {
        stop();
        String path = "/sounds/" + name + ".wav";
        try {
            Media media = new Media(Music.class.getResource(path).toExternalForm());
            player = new MediaPlayer(media);
            player.setCycleCount(MediaPlayer.INDEFINITE); // повтор
            player.setVolume(volume); // громкость меню
            player.play();
        } catch (Exception e) {
            System.out.println("Ошибка загрузки музыки: " + path);
            e.printStackTrace();
        }
    }

    public static void stop() {
        if (player != null) {
            player.stop();
            player.dispose();
            player = null;
        }
    }
}