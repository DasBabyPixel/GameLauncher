/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util;

import de.dasbabypixel.annotations.Api;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Api
public class Config<T> {
    public static final Config<String> NAME = createString("name", "GameLauncher");
    public static final Config<Boolean> DEBUG = createBoolean("debug", false);
    public static final Config<Boolean> TRACK_RESOURCES = createBoolean("track_resources", DEBUG);
    public static final Config<Boolean> CALCULATE_THREAD_STACKS = createBoolean("calculate_thread_stacks", DEBUG);
    public static final Config<String> GAME_DIRECTORY = createString("game_directory", "folder");
    private final String name;
    private final T defaultValue;
    private volatile T value;

    private Config(String name, T defaultValue) {
        this(name, defaultValue, defaultValue);
    }

    private Config(String name, T defaultValue, T value) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = value;
        if (Holder.config.putIfAbsent(name, this) != null) {
            throw new GameException.RuntimeGameException("Cannot create multiple configs of the same name: " + name);
        }
    }

    @Api public static <T> Config<T> named(String name) throws UnknownConfigException {
        @SuppressWarnings("unchecked") Config<T> c = (Config<T>) Holder.config.get(name);
        if (c == null) throw new UnknownConfigException(name);
        return c;
    }

    @Api public static <T> Config<T> create(String name, T defaultValue) {
        return new Config<>(name, defaultValue);
    }

    @Api public static Config<String> createString(String name, String defaultValue) {
        String prop = systemProperty(name);
        return prop == null ? new Config<>(name, defaultValue) : new Config<>(name, defaultValue, prop);
    }

    @Api public static Config<Boolean> createBoolean(String name, Config<Boolean> defaultValue) {
        return createBoolean(name, defaultValue.value());
    }

    @Api public static Config<Boolean> createBoolean(String name, boolean defaultValue) {
        String prop = systemProperty(name);
        boolean value = prop == null ? defaultValue : getBoolean(prop, defaultValue);
        return new Config<>(name, defaultValue, value);
    }

    @Api public static Config<Integer> createInt(String name, int defaultValue) {
        String prop = systemProperty(name);
        int value = prop == null ? defaultValue : getInt(prop, defaultValue);
        return new Config<>(name, defaultValue, value);
    }

    private static int getInt(String prop, int d) {
        try {
            return Integer.parseInt(prop);
        } catch (NumberFormatException e) {
            return d;
        }
    }

    private static boolean getBoolean(String prop, boolean d) {
        if ("true".equalsIgnoreCase(prop) || "1".equals(prop)) return true;
        if ("false".equalsIgnoreCase(prop) || "0".equals(prop)) return false;
        return d;
    }

    private static @Nullable String systemProperty(String name) {
        return System.getProperty("gamelauncher." + name);
    }

    @Api public T defaultValue() {
        return defaultValue;
    }

    @Api public T value() {
        return value;
    }

    @Api public void value(T value) {
        this.value = value;
    }

    @Override public String toString() {
        return name + "[" + value + "]";
    }

    @Api
    public static class UnknownConfigException extends GameException {
        @Api public UnknownConfigException() {
        }

        @Api public UnknownConfigException(String message, Throwable cause) {
            super(message, cause);
        }

        @Api public UnknownConfigException(String message) {
            super(message);
        }

        @Api public UnknownConfigException(Throwable cause) {
            super(cause);
        }
    }

    private static class Holder {
        private static final Map<String, Config<?>> config = new ConcurrentHashMap<>();
    }
}
