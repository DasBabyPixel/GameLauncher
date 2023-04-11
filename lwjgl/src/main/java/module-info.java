open module gamelauncher.lwjgl {
	requires transitive com.google.gson;
	requires transitive de.dasbabypixel.property;
	requires transitive gamelauncher.base;
	requires transitive org.jetbrains.annotations;
	requires transitive org.joml;
	requires transitive org.lwjgl.glfw.natives;
	requires transitive org.lwjgl.glfw;
	requires transitive org.lwjgl.natives;
	requires transitive org.lwjgl.opengl.natives;
	requires transitive org.lwjgl.opengl;
	requires transitive org.lwjgl.opengles.natives;
	requires transitive org.lwjgl.opengles;
	requires transitive org.lwjgl.stb.natives;
	requires transitive org.lwjgl.stb;
	requires io.netty.codec;
	requires io.netty.buffer;
	requires io.netty.common;
	requires io.netty.handler;
	requires io.netty.transport;
    requires gamelauncher.gles;

    exports gamelauncher.lwjgl;
	exports gamelauncher.lwjgl.gui;
	exports gamelauncher.lwjgl.gui.launcher;
	exports gamelauncher.lwjgl.input;
	exports gamelauncher.lwjgl.render;
	exports gamelauncher.lwjgl.render.font.bitmap;
	exports gamelauncher.lwjgl.render.glfw;
	exports gamelauncher.lwjgl.settings.controls;
	exports gamelauncher.lwjgl.util;
	exports gamelauncher.lwjgl.util.keybind;
	exports gamelauncher.lwjgl.util.profiler;
}
