/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.util.keybind;

import android.view.KeyEvent;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.event.events.util.keybind.KeybindEntryEvent;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.Keybind;
import gamelauncher.engine.util.keybind.KeybindEvent;
import gamelauncher.engine.util.keybind.KeybindManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AndroidKeybindManager extends AbstractGameResource implements KeybindManager {
    public static final int BITS_UNKNOWN = 0b00000000_00000000_00000000_00000000;
    public static final int BITS_TOUCH = 0b10000000_00000000_00000000_00000000;
    public static final int BITS_KEY = 0b01000000_00000000_00000000_00000000;
    public static final int BITS_CHARACTER = 0b00100000_00000000_00000000_00000000;
    private final GameLauncher launcher;
    private final Map<Integer, Keybind> keybinds = new ConcurrentHashMap<>();
    private final Map<Integer, String> names = new ConcurrentHashMap<>();
    public KeyEvent lastKeyEvent = null;

    public AndroidKeybindManager(GameLauncher launcher) {
        this.launcher = launcher;
    }

    @Override public void post(KeybindEvent event) {
        launcher.eventManager().post(new KeybindEntryEvent(event));
        event.keybind().handle(event);
    }

    @Override public Keybind keybind(int keybind) {
        return keybinds.computeIfAbsent(keybind, k -> {
            String display = "?";
            if ((k & BITS_KEY) == BITS_KEY) {
                if (lastKeyEvent != null) {
                    display = Character.toString(lastKeyEvent.getKeyCharacterMap().getDisplayLabel(k - BITS_KEY));
                }
            } else if ((k & BITS_TOUCH) == BITS_TOUCH) {
                display = "Pointer " + (k - BITS_TOUCH);
            } else if ((k & BITS_CHARACTER) == BITS_CHARACTER) {
                display = Character.toString((char) (k - BITS_CHARACTER));
            }
            return new AndroidKeybind(display, k, this);
        });
    }

    @Override protected void cleanup0() throws GameException {
        for (Keybind keybind : keybinds.values()) {
            keybind.cleanup();
        }
        keybinds.clear();
    }
}
