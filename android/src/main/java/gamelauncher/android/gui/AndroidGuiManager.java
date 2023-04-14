/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.gui;

import gamelauncher.android.AndroidGameLauncher;
import gamelauncher.android.gui.launcher.*;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.event.EventHandler;
import gamelauncher.engine.event.events.gui.GuiOpenEvent;
import gamelauncher.engine.event.events.util.keybind.KeybindEntryEvent;
import gamelauncher.engine.gui.Gui;
import gamelauncher.engine.gui.GuiManager;
import gamelauncher.engine.gui.GuiStack;
import gamelauncher.engine.gui.LauncherBasedGui;
import gamelauncher.engine.gui.launcher.*;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.function.GameFunction;
import gamelauncher.engine.util.function.GameSupplier;
import gamelauncher.engine.util.keybind.KeybindEvent;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.gles.GLES;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class AndroidGuiManager extends AbstractGameResource implements GuiManager {
    private static final Logger logger = Logger.logger();
    private final AndroidGameLauncher launcher;
    private final Map<Framebuffer, GuiStack> guis = new ConcurrentHashMap<>();
    private final Map<Class<? extends LauncherBasedGui>, GameSupplier<? extends LauncherBasedGui>> registeredGuis = new HashMap<>();

    private final Map<Class<? extends LauncherBasedGui>, Set<GameFunction<? extends LauncherBasedGui, ? extends LauncherBasedGui>>> converters = new HashMap<>();

    public AndroidGuiManager(AndroidGameLauncher launcher, GLES gles) {
        this.launcher = launcher;
        this.launcher.eventManager().registerListener(this);
        this.registerGuiCreator(MainScreenGui.class, () -> new AndroidMainScreenGui(launcher));
        this.registerGuiCreator(TextureGui.class, () -> new AndroidTextureGui(launcher, gles));
        this.registerGuiCreator(ColorGui.class, () -> new AndroidColorGui(launcher, gles));
        this.registerGuiCreator(ScrollGui.class, () -> new AndroidScrollGui(launcher));
        this.registerGuiCreator(LineGui.class, () -> new AndroidLineGui(launcher, gles));
    }

    @Override
    public void cleanup0() throws GameException {
        this.launcher.eventManager().unregisterListener(this);
        CompletableFuture<?>[] futures = new CompletableFuture[this.guis.size()];
        int i = 0;
        for (Map.Entry<Framebuffer, GuiStack> entry : this.guis.entrySet()) {
            futures[i++] = this.cleanupLater(entry.getKey());
        }
        for (CompletableFuture<?> fut : futures) {
            Threads.waitFor(fut);
        }
    }

    /**
     * Cleanes up a framebuffer and its guis
     *
     * @param framebuffer the framebuffer
     * @return a future for the task
     */
    private CompletableFuture<Void> cleanupLater(Framebuffer framebuffer) {
        GuiStack stack = this.guis.remove(framebuffer);
        if (stack != null) {
            return framebuffer.renderThread().submit(() -> {
                GuiStack.StackEntry se;
                while ((se = stack.popGui()) != null) {
                    se.gui.cleanup(framebuffer);
                }
            });
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void openGui(Framebuffer framebuffer, Gui gui) throws GameException {
        GuiStack stack = this.guis.get(framebuffer);
        if (stack == null) {
            stack = new GuiStack();
            this.guis.put(framebuffer, stack);
        }
        final boolean exit = gui == null;
        GuiStack.StackEntry stackEntryCurrentGui = exit ? stack.popGui() : stack.peekGui();
        Gui currentGui = stackEntryCurrentGui == null ? null : stackEntryCurrentGui.gui;
        if (currentGui != null) {
            currentGui.unfocus();
            currentGui.onClose();
            if (exit) {
                framebuffer.renderThread().submit(() -> {
                    currentGui.cleanup(framebuffer);
                });
                if (stack.peekGui() == null) {
                    gui = this.createGui(MainScreenGui.class);
                }
            }
        } else {
            if (exit) {
                gui = this.createGui(MainScreenGui.class);
            }
        }
        if (gui != null) {
            gui = launcher.eventManager().post(new GuiOpenEvent(gui)).gui();
            gui.widthProperty().bind(framebuffer.width());
            gui.heightProperty().bind(framebuffer.height());
            stack.pushGui(gui);
            gui.onOpen();
            gui.focus();
        } else {
            logger.error("Tried to push \"null\" GUI");
        }
        framebuffer.scheduleRedraw();
    }

    @Override
    public Gui currentGui(Framebuffer framebuffer) throws GameException {
        if (!this.guis.containsKey(framebuffer)) {
            return null;
        }
        GuiStack.StackEntry e = this.guis.get(framebuffer).peekGui();
        return e == null ? null : e.gui;
    }

    @Override
    public void cleanup(Framebuffer framebuffer) throws GameException {
        Threads.waitFor(this.cleanupLater(framebuffer));
    }

    @Override
    public GameLauncher launcher() {
        return this.launcher;
    }

    @Override
    public void updateGuis() throws GameException {
        for (GuiStack stack : this.guis.values()) {
            GuiStack.StackEntry e = stack.peekGui();
            if (e != null) {
                e.gui.update();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends LauncherBasedGui> T createGui(Class<T> clazz) throws GameException {
        GameSupplier<? extends LauncherBasedGui> sup = this.registeredGuis.get(clazz);
        if (sup == null) {
            throw new GameException("Gui " + clazz.getName() + " not registered! Outdated launcher?");
        }
        T t = clazz.cast(sup.get());
        if (this.converters.containsKey(clazz)) {
            Set<GameFunction<? extends LauncherBasedGui, ? extends LauncherBasedGui>> c = this.converters.get(clazz);
            //noinspection rawtypes
            for (GameFunction func : c) {
                t = clazz.cast(func.apply(t));
            }
        }
        return t;
    }

    @Override
    public <T extends LauncherBasedGui> void registerGuiConverter(Class<T> clazz, GameFunction<T, T> converter) {
        Set<GameFunction<? extends LauncherBasedGui, ? extends LauncherBasedGui>> c = this.converters.computeIfAbsent(clazz, k -> new CopyOnWriteArraySet<>());
        c.add(converter);
    }

    @Override
    public <T extends LauncherBasedGui> void registerGuiCreator(Class<T> clazz, GameSupplier<T> sup) {
        this.registeredGuis.put(clazz, sup);
    }

    public void redrawAll() {
        for (Map.Entry<Framebuffer, GuiStack> e : guis.entrySet()) {
            e.getKey().scheduleRedraw();
        }
    }

    @EventHandler
    private void handle(KeybindEntryEvent event) {
        KeybindEvent entry = event.entry();
        // TODO: Gui Selection - not relevant with only one gui being able to be opened
        // in this guimanager
        this.guis.values().forEach(stack -> {
            GuiStack.StackEntry e = stack.peekGui();
            if (e != null) {
                try {
                    e.gui.handle(entry);
                } catch (GameException ex) {
                    logger.error(ex);
                }
            }
        });
    }
}
