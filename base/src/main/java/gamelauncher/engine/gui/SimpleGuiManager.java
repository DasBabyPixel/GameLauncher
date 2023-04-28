/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.gui;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.event.EventHandler;
import gamelauncher.engine.event.events.gui.GuiOpenEvent;
import gamelauncher.engine.event.events.util.keybind.KeybindEntryEvent;
import gamelauncher.engine.gui.guis.MainScreenGui;
import gamelauncher.engine.gui.guis.ScrollGui;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.function.GameFunction;
import gamelauncher.engine.util.function.GameSupplier;
import gamelauncher.engine.util.keybind.KeybindEvent;
import gamelauncher.engine.util.logging.Logger;
import java8.util.concurrent.CompletableFuture;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class SimpleGuiManager extends AbstractGameResource implements GuiManager {
    private static final Logger logger = Logger.logger();
    private final GameLauncher launcher;
    private final Map<Framebuffer, GuiStack> guis = new ConcurrentHashMap<>();
    private final Map<Class<? extends Gui>, GameSupplier<? extends Gui>> registeredGuis = new HashMap<>();
    private final Map<Class<? extends Gui>, Set<GameFunction<? extends Gui, ? extends Gui>>> converters = new HashMap<>();

    public SimpleGuiManager(GameLauncher launcher) {
        this.launcher = launcher;
        this.launcher.eventManager().registerListener(this);
        registerGuiCreator(MainScreenGui.class);
        registerGuiCreator(ScrollGui.class);
    }

    @Override public void cleanup0() throws GameException {
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

    @Override public void openGui(Framebuffer framebuffer, Gui gui) throws GameException {
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
                framebuffer.renderThread().submit(() -> currentGui.cleanup(framebuffer));
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
            Gui oldGui = gui;
            gui = launcher.eventManager().post(new GuiOpenEvent(gui)).gui();
            if (oldGui != gui) {
                oldGui.onOpen();
                oldGui.focus();
                oldGui.unfocus();
                oldGui.onClose();
                framebuffer.renderThread().submit(() -> {
                    oldGui.init(framebuffer);
                    oldGui.cleanup(framebuffer);
                });
            }
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

    @Api @Override public Gui currentGui(Framebuffer framebuffer) throws GameException {
        if (!this.guis.containsKey(framebuffer)) {
            return null;
        }
        GuiStack.StackEntry e = this.guis.get(framebuffer).peekGui();
        return e == null ? null : e.gui;
    }

    @Override public void cleanup(Framebuffer framebuffer) throws GameException {
        Threads.waitFor(this.cleanupLater(framebuffer));
    }

    @Override public GameLauncher launcher() {
        return this.launcher;
    }

    @Override public void updateGuis() throws GameException {
        for (GuiStack stack : this.guis.values()) {
            GuiStack.StackEntry e = stack.peekGui();
            if (e != null) {
                e.gui.update();
            }
        }
    }

    @SuppressWarnings("unchecked") @Override public <T extends Gui> T createGui(Class<T> clazz) throws GameException {
        GameSupplier<? extends Gui> sup = this.registeredGuis.get(clazz);
        if (sup == null) {
            throw new GameException("Gui " + clazz.getName() + " not registered! Outdated launcher?");
        }
        T t = clazz.cast(sup.get());
        if (this.converters.containsKey(clazz)) {
            Set<GameFunction<? extends Gui, ? extends Gui>> c = this.converters.get(clazz);
            //noinspection rawtypes
            for (GameFunction func : c) {
                t = clazz.cast(func.apply(t));
            }
        }
        return t;
    }

    @Override public <T extends Gui> void registerGuiConverter(Class<T> clazz, GameFunction<T, T> converter) {
        Set<GameFunction<? extends Gui, ? extends Gui>> c = this.converters.computeIfAbsent(clazz, k -> new CopyOnWriteArraySet<>());
        c.add(converter);
    }

    @Override public <T extends Gui> void registerGuiCreator(Class<T> clazz, GameSupplier<T> sup) {
        this.registeredGuis.put(clazz, sup);
    }

    @Override public <T extends Gui> void registerGuiCreator(Class<T> clazz) {
        registerGuiCreator0(clazz, clazz, null);
    }

    @Override
    public <T extends Gui> void registerGuiCreator(Class<T> clazz, GuiConstructorTemplate constructorTemplate) {
        if (constructorTemplate == null)
            throw new IllegalArgumentException("GuiConstructorTemplate is null", new NullPointerException());
        registerGuiCreator0(clazz, clazz, constructorTemplate);
    }

    @Override
    public <T extends Gui> void registerGuiCreator(Class<T> guiClass, Class<? extends T> implementationClass, GuiConstructorTemplate constructorTemplate) {
        if (constructorTemplate == null)
            throw new IllegalArgumentException("GuiConstructorTemplate is null", new NullPointerException());
        registerGuiCreator0(guiClass, implementationClass, constructorTemplate);
    }

    @Override public <T extends Gui> void registerGuiCreator(Class<T> clazz, Class<? extends T> implementationClass) {
        registerGuiCreator0(clazz, implementationClass, null);
    }

    private <T extends Gui> void registerGuiCreator0(Class<T> clazz, Class<? extends T> implementationClass, GuiConstructorTemplate constructorTemplate) {
        Class<?> found = null;
        for (Class<?> c : implementationClass.getDeclaredClasses()) {
            if (c.getSimpleName().equals(launcher.operatingSystem().name()) && !c.isInterface() && !Modifier.isAbstract(c.getModifiers())) {
                found = c;
                break;
            }
        }
        Class<?> defaultCls = implementationClass;
        if (found != null) defaultCls = found;
        if (defaultCls.isInterface() || Modifier.isAbstract(defaultCls.getModifiers())) {
            for (Class<?> cls : defaultCls.getDeclaredClasses()) {
                if (!clazz.isAssignableFrom(cls)) {
                    continue;
                }
                if (cls.getSimpleName().equals("Default") || cls.getSimpleName().equals("Simple")) {
                    defaultCls = cls;
                    break;
                }
            }
            if (defaultCls == implementationClass) {
                logger.error("Unable to find default class for GUI " + clazz.getName());
                return;
            }
        }
        try {
            if (constructorTemplate == null) {
                try {
                    Field field = defaultCls.getField("TEMPLATE");
                    if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                        constructorTemplate = (GuiConstructorTemplate) field.get(null);
                    } else {
                        throw new IllegalArgumentException("TEMPLATE is not public static final");
                    }
                } catch (NoSuchFieldException ignored) {
                    for (Constructor<?> constructor : defaultCls.getConstructors()) {
                        Class<?>[] parameters = constructor.getParameterTypes();

                        templates:
                        for (GuiConstructorTemplate template : GuiConstructorTemplates.defaults()) {
                            Class<?>[] args = template.argumentTypes();
                            for (int i = 0; i < args.length; i++) {
                                if (!args[i].equals(parameters[i])) {
                                    continue templates;
                                }
                            }
                            constructorTemplate = template;
                            break;
                        }
                    }
                }
            }
            if (constructorTemplate == null)
                throw new IllegalArgumentException("Gui is not correctly set up! TEMPLATE missing: " + defaultCls.getName());
            @SuppressWarnings("unchecked") Constructor<? extends Gui> c = (Constructor<? extends Gui>) defaultCls.getConstructor(constructorTemplate.argumentTypes());
            GuiConstructor<?> constructor = new GuiConstructor<>(c, constructorTemplate);
            registeredGuis.put(clazz, constructor);
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    @Override public void redrawAll() {
        for (Map.Entry<Framebuffer, GuiStack> e : guis.entrySet()) {
            e.getKey().scheduleRedraw();
        }
    }

    @EventHandler private void handle(KeybindEntryEvent event) {
        KeybindEvent entry = event.entry();
        // TODO: Gui Selection - not relevant with only one frame visible in this guimanager
        for (GuiStack stack : guis.values()) {
            GuiStack.StackEntry e = stack.peekGui();
            if (e != null) {
                try {
                    e.gui.handle(entry);
                } catch (GameException ex) {
                    logger.error(ex);
                }
            }
        }
    }

    protected static class GuiConstructor<T extends Gui> implements GameSupplier<T> {
        private final Constructor<? extends T> constructor;
        private final GuiConstructorTemplate template;

        public GuiConstructor(Constructor<? extends T> constructor, GuiConstructorTemplate template) {
            this.constructor = constructor;
            this.template = template;
        }

        public T instantiate() throws GameException {
            try {
                return constructor.newInstance(template.arguments());
            } catch (Exception e) {
                throw new GameException(e);
            }
        }

        @Override public T get() throws GameException {
            return instantiate();
        }
    }
}
