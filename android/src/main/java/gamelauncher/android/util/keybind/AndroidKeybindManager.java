/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.util.keybind;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameFunction;
import gamelauncher.engine.util.keybind.Keybind;
import gamelauncher.engine.util.keybind.KeybindEntry;
import gamelauncher.engine.util.keybind.KeybindManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AndroidKeybindManager extends AbstractGameResource implements KeybindManager {
    private final GameLauncher launcher;
    private final Map<Integer, Keybind> keybinds = new ConcurrentHashMap<>();
    private final Map<Integer, String> names = new ConcurrentHashMap<>();

    public AndroidKeybindManager(GameLauncher launcher) {
        this.launcher = launcher;
    }

    @Override
    public void post(GameFunction<Keybind, KeybindEntry> entry) throws GameException {

    }

    @Override
    public Keybind getKeybind(int keybind) {

        return keybinds.get(keybind);
    }

    @Override
    protected void cleanup0() throws GameException {

    }
}
