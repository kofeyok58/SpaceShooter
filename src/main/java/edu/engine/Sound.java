package edu.engine;

import javafx.scene.media.AudioClip;

import java.util.HashMap;

public class Sound {
    private static final HashMap<String, AudioClip> cache = new HashMap<>();

    public static AudioClip load(String name) {
        return cache.computeIfAbsent(name, n -> {
            String path = "/sounds/" + n + ".wav"; // или .mp3
            try {
                AudioClip clip = new AudioClip(Sound.class.getResource(path).toString());
                return clip;
            } catch (Exception e) {
                System.err.println("Не удалось загрузить звук: " + path);
                throw e;
            }
        });
    }

    public static void play(String name) {
        load(name).play();
    }
}