package edu.engine;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/** Простая загрузка картинок из /textures */
public final class Assets {
    private static final Map<String, Image> CACHE = new HashMap<>();

    private Assets() {}

    public static Image image(String name) {
        return CACHE.computeIfAbsent(name, key -> {
            String path = "/textures/" + key + ".png";
            InputStream is = Assets.class.getResourceAsStream(path);
            if (is == null) {
                throw new RuntimeException("Не найден ресурс: " + path);
            }
            return new Image(is);
        });
    }
}
