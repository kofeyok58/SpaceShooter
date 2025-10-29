package edu.engine;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

import java.util.HashSet;
import java.util.Set;

/** Отслеживание нажатых клавиш */
public final class Keys {
    private final Set<KeyCode> down = new HashSet<>();

    public void attach(Scene scene){
        scene.setOnKeyPressed(e->down.add(e.getCode()));

        scene.setOnKeyReleased(e-> down.remove(e.getCode()));

    }

    public boolean isDown(KeyCode code){return down.contains(code);}

}
