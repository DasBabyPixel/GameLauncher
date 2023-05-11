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
import gamelauncher.engine.gui.guis.ButtonGui;
import gamelauncher.engine.gui.guis.MainScreenGui;
import gamelauncher.engine.gui.guis.ScrollGui;
import gamelauncher.engine.gui.guis.TextGui;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.function.GameFunction;
import gamelauncher.engine.util.function.GameSupplier;
import gamelauncher.engine.util.keybind.KeybindEvent;
import gamelauncher.engine.util.logging.Logger;
import java8.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.Nullable;

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
    private final Map<Framebuffer, Gui> guis = new ConcurrentHashMap<>();
    private final Map<Class<? extends Gui>, Map<GuiDistribution, GameSupplier<? extends Gui>>> registeredGuis = new HashMap<>();
    private final Map<Class<? extends Gui>, Set<GameFunction<? extends Gui, ? extends Gui>>> converters = new HashMap<>();
    private final Map<Class<? extends Gui>, GuiDistribution> preferredDistribution = new HashMap<>();

    public SimpleGuiManager(GameLauncher launcher) {
        this.launcher = launcher;
        this.launcher.eventManager().registerListener(this);
        registerGuiCreator(null, MainScreenGui.class);
        registerGuiCreator(null, ScrollGui.class);
        registerGuiCreator(null, ButtonGui.class);
        registerGuiCreator(null, TextGui.class);
    }

    @Override public void cleanup0() throws GameException {
        this.launcher.eventManager().unregisterListener(this);
        CompletableFuture<?>[] futures = new CompletableFuture[this.guis.size()];
        int i = 0;
        for (Map.Entry<Framebuffer, Gui> entry : this.guis.entrySet()) {
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
        Gui gui = this.guis.remove(framebuffer);
        if (gui != null) {
            return framebuffer.renderThread().submit(() -> {
                gui.cleanup(framebuffer);
            }).thenCombine(launcher.gameThread().runLater(() -> {
                gui.unfocus();
                gui.onClose();
            }), (unused, unused2) -> null);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override public void openGui(Framebuffer framebuffer, Gui gui) throws GameException {
        if (gui == null) throw new NullPointerException("Gui");
        Gui currentGui = this.guis.put(framebuffer, gui);
        if (currentGui != null) {
            currentGui.unfocus();
            currentGui.onClose();
        }
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
            framebuffer.scheduleRedraw();
        }
        gui.widthProperty().bind(framebuffer.width());
        gui.heightProperty().bind(framebuffer.height());
        gui.onOpen();
        gui.focus();
        Gui finalGui = gui;
        framebuffer.renderThread().submit(() -> finalGui.init(framebuffer));
        framebuffer.scheduleRedraw();
    }

    @Api @Override public @Nullable Gui currentGui(Framebuffer framebuffer) throws GameException {
        return this.guis.get(framebuffer);
    }

    @Override public void cleanup(Framebuffer framebuffer) throws GameException {
        Threads.waitFor(this.cleanupLater(framebuffer));
    }

    @Override public GameLauncher launcher() {
        return this.launcher;
    }

    @Override public void updateGuis() throws GameException {
        for (Gui gui : this.guis.values()) {
            gui.update();
        }
    }

    @Override public GuiDistribution preferredDistribution(Class<? extends Gui> clazz) {
        return preferredDistribution.computeIfAbsent(clazz, aClass -> GuiDistribution.DEFAULT);
    }

    @Override public void preferredDistribution(Class<? extends Gui> clazz, GuiDistribution distribution) {
        preferredDistribution.put(clazz, distribution);
    }

    @SuppressWarnings("unchecked") @Override public <T extends Gui> T createGui(GuiDistribution distribution, Class<T> clazz) throws GameException {
        if (distribution == null) distribution = preferredDistribution(clazz);
        GameSupplier<? extends Gui> sup = registered(clazz).get(distribution);
        if (sup == null) sup = registered(clazz).get(GuiDistribution.DEFAULT);
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

    @Override public <T extends Gui> void registerGuiCreator(GuiDistribution distribution, Class<T> clazz, GameSupplier<T> sup) {
        if (distribution == null) distribution = preferredDistribution(clazz);
        registered(clazz).put(distribution, sup);
    }

    private Map<GuiDistribution, GameSupplier<? extends Gui>> registered(Class<? extends Gui> clazz) {
        return registeredGuis.computeIfAbsent(clazz, aClass -> new HashMap<>());
    }

    @Override public <T extends Gui> void registerGuiCreator(GuiDistribution distribution, Class<T> clazz) {
        registerGuiCreator0(distribution, clazz, clazz, null);
    }

    @Override public <T extends Gui> void registerGuiCreator(GuiDistribution distribution, Class<T> clazz, GuiConstructorTemplate constructorTemplate) {
        if (constructorTemplate == null) throw new IllegalArgumentException("GuiConstructorTemplate is null", new NullPointerException());
        registerGuiCreator0(distribution, clazz, clazz, constructorTemplate);
    }

    @Override public <T extends Gui> void registerGuiCreator(GuiDistribution distribution, Class<T> guiClass, Class<? extends T> implementationClass, GuiConstructorTemplate constructorTemplate) {
        if (constructorTemplate == null) throw new IllegalArgumentException("GuiConstructorTemplate is null", new NullPointerException());
        registerGuiCreator0(distribution, guiClass, implementationClass, constructorTemplate);
    }

    @Override public <T extends Gui> void registerGuiCreator(GuiDistribution distribution, Class<T> clazz, Class<? extends T> implementationClass) {
        registerGuiCreator0(distribution, clazz, implementationClass, null);
    }

    private <T extends Gui> void registerGuiCreator0(GuiDistribution distribution, Class<T> clazz, Class<? extends T> implementationClass, GuiConstructorTemplate constructorTemplate) {
        if (distribution == null) distribution = preferredDistribution(clazz);
        Class<?> found = null;
        for (Class<?> c : implementationClass.getDeclaredClasses()) {
            if (c.getSimpleName().equals(launcher.operatingSystem().osName()) && !c.isInterface() && !Modifier.isAbstract(c.getModifiers())) {
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
                            if (args.length != parameters.length) continue;
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
            if (constructorTemplate == null) {
                logger.error("Gui is not correctly set up! TEMPLATE missing: " + defaultCls.getName());
                return;
            }
            @SuppressWarnings("unchecked") Constructor<? extends Gui> c = (Constructor<? extends Gui>) defaultCls.getConstructor(constructorTemplate.argumentTypes());
            GuiConstructor<?> constructor = new GuiConstructor<>(c, constructorTemplate);
            registered(clazz).put(distribution, constructor);
            if (defaultCls != clazz) registered(defaultCls.asSubclass(Gui.class)).put(distribution, constructor);
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    @Override public void redrawAll() {
        for (Map.Entry<Framebuffer, Gui> e : guis.entrySet()) {
            e.getKey().scheduleRedraw();
        }
    }

    @EventHandler protected void handle(KeybindEntryEvent event) {
        if (!shouldHandle(event)) return;
        KeybindEvent entry = event.entry();
        // TODO: Gui Selection - not relevant with only one frame visible in this guimanager
        for (Gui gui : guis.values()) {
            try {
                gui.handle(entry);
            } catch (GameException ex) {
                logger.error(ex);
            }
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted") protected boolean shouldHandle(KeybindEntryEvent event) {
        return true;
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
