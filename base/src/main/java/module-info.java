module gamelauncher.base {
	exports gamelauncher.engine.event.events.util.keybind;
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
	exports gamelauncher.engine.util.text.serializer;
	exports gamelauncher.engine.util.text;
	exports gamelauncher.engine.util;
	exports gamelauncher.engine;

	provides java.nio.file.spi.FileSystemProvider with gamelauncher.engine.io.embed.EmbedFileSystemProvider;

	requires com.google.gson;
	requires de.dasbabypixel.property;
	requires java.base;
	requires org.fusesource.jansi;
	requires org.jetbrains.annotations;
	requires org.joml;
}
