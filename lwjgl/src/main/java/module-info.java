open module gamelauncher.lwjgl {
    requires transitive gamelauncher.gles;
    requires transitive gamelauncher.netty;
    requires org.apache.commons.imaging;
    requires org.fusesource.jansi;
    requires org.lwjgl;
    requires org.lwjgl.opengl;
    requires org.lwjgl.opengles;
    requires org.lwjgl.glfw;
    requires org.lwjgl.stb;
    requires org.lwjgl.natives;
    requires org.lwjgl.opengl.natives;
    requires org.lwjgl.opengles.natives;
    requires org.lwjgl.glfw.natives;
    requires org.lwjgl.stb.natives;
    requires de.dasbabypixel.property;
    requires java.desktop;
    exports gamelauncher.lwjgl;
    exports gamelauncher.lwjgl.gui;
    exports gamelauncher.lwjgl.settings;
    exports gamelauncher.lwjgl.input;
    exports gamelauncher.lwjgl.event;
    exports gamelauncher.lwjgl.util;
    exports gamelauncher.lwjgl.util.image;
    exports gamelauncher.lwjgl.util.keybind;
    exports gamelauncher.lwjgl.render;
    exports gamelauncher.lwjgl.render.glfw;
}
