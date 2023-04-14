module gamelauncher.base {
    exports gamelauncher.engine.event.events.util.keybind;
    exports gamelauncher.engine.event.events.gui;
    exports gamelauncher.engine.event.events;
    exports gamelauncher.engine.event;
    exports gamelauncher.engine.game;
    exports gamelauncher.engine.gui.guis;
    exports gamelauncher.engine.gui.launcher;
    exports gamelauncher.engine.gui;
    exports gamelauncher.engine.input;
    exports gamelauncher.engine.io.embed.url;
    exports gamelauncher.engine.io.embed;
    exports gamelauncher.engine.io;
    exports gamelauncher.engine.network.packet;
    exports gamelauncher.engine.network;
    exports gamelauncher.engine.plugin;
    exports gamelauncher.engine.render.font;
    exports gamelauncher.engine.render.model;
    exports gamelauncher.engine.render.shader;
    exports gamelauncher.engine.render.texture;
    exports gamelauncher.engine.render;
    exports gamelauncher.engine.resource;
    exports gamelauncher.engine.settings.controls;
    exports gamelauncher.engine.settings;
    exports gamelauncher.engine.util.concurrent;
    exports gamelauncher.engine.util.function;
    exports gamelauncher.engine.util.keybind;
    exports gamelauncher.engine.util.logging;
    exports gamelauncher.engine.util.math;
    exports gamelauncher.engine.util.profiler;
    exports gamelauncher.engine.util.property;
    exports gamelauncher.engine.util.text.flattener;
    exports gamelauncher.engine.util.text.format;
    exports gamelauncher.engine.util.text.serializer;
    exports gamelauncher.engine.util.text;
    exports gamelauncher.engine.util;
    exports gamelauncher.engine;
    exports gamelauncher.engine.render.light;

    requires transitive com.google.gson;
    requires transitive de.dasbabypixel.property;
    requires java.base;
    //noinspection requires-transitive-automatic
    requires transitive de.dasbabypixel.annotations;
    requires transitive org.jetbrains.annotations;
    requires transitive org.joml;
}
