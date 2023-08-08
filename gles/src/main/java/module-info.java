open module gamelauncher.gles {
    requires transitive gamelauncher.api;
    requires transitive gamelauncher.gles.gl;
    requires java.base;
    requires de.dasbabypixel.property;

    exports gamelauncher.gles.texture;
    exports gamelauncher.gles.util;
    exports gamelauncher.gles.font.bitmap;
    exports gamelauncher.gles.render;
    exports gamelauncher.gles.context;
    exports gamelauncher.gles.framebuffer;
    exports gamelauncher.gles.gui;
    exports gamelauncher.gles.annotation;
    exports gamelauncher.gles.mesh;
    exports gamelauncher.gles.model;
    exports gamelauncher.gles.modelloader;
    exports gamelauncher.gles.shader;
    exports gamelauncher.gles.shader.struct;
    exports gamelauncher.gles.states;
    exports gamelauncher.gles;
    exports gamelauncher.gles.compat;
}
