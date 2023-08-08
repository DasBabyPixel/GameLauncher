/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.internal.util.keybind;

import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.keybind.Keybind;
import gamelauncher.engine.util.keybind.KeybindEvent;
import gamelauncher.engine.util.keybind.KeybindHandler;
import gamelauncher.engine.util.keybind.KeybindManager;
import java8.util.concurrent.CompletableFuture;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public class AndroidKeybind extends AbstractGameResource implements Keybind {
    private final String display;
    private final int id;
    private final AndroidKeybindManager manager;
    private final Collection<KeybindHandler> handlers = new CopyOnWriteArrayList<>();

    public AndroidKeybind(String display, int id, AndroidKeybindManager manager) {
        this.display = display;
        this.id = id;
        this.manager = manager;
    }

    @Override protected CompletableFuture<Void> cleanup0() {
        handlers.clear();
        return null;
    }

    @Override public String name() {
        return display;
    }

    @Override public int uniqueId() {
        return id;
    }

    @Override public void handle(KeybindEvent entry) {
        for (KeybindHandler handler : handlers) {
            handler.handle(entry);
        }
    }

    @Override public void addHandler(KeybindHandler handler) {
        handlers.add(handler);
    }

    @Override public void removeHandler(KeybindHandler handler) {
        handlers.remove(handler);
    }

    @Override public KeybindManager manager() {
        return manager;
    }
}
