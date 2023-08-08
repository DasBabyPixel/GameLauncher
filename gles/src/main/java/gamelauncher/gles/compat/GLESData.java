/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.gles.compat;

import gamelauncher.engine.util.logging.Logger;
import gamelauncher.gles.gl.GLES20;
import gamelauncher.gles.gl.GLES30;

public class GLESData {
    public static String VERSION = null;
    public static String SHADING_VERSION = null;
    public static int VERSION_MAJOR = -1;
    public static int VERSION_MINOR = -1;
    public static int MAX_TEXTURE_SIZE = -1;
    public static int MAX_TEXTURE_IMAGE_UNITS = -1;
    public static int MAX_DRAW_BUFFERS = -1;

    public static void load(GLES20 gl) {
        VERSION = gl.glGetString(GLES20.GL_VERSION);
        SHADING_VERSION = gl.glGetString(GLES20.GL_SHADING_LANGUAGE_VERSION);
        VERSION_MAJOR = gl.glGetInteger(GLES30.GL_MAJOR_VERSION);
        VERSION_MINOR = gl.glGetInteger(GLES30.GL_MINOR_VERSION);
        MAX_TEXTURE_SIZE = gl.glGetInteger(GLES20.GL_MAX_TEXTURE_SIZE);
        MAX_TEXTURE_IMAGE_UNITS = gl.glGetInteger(GLES20.GL_MAX_TEXTURE_IMAGE_UNITS);
        if (VERSION_MAJOR >= 3) MAX_DRAW_BUFFERS = gl.glGetInteger(GLES30.GL_MAX_DRAW_BUFFERS);
    }

    public static void printDebugInfos(Logger logger) {
        logger.debugf("OpenGL: %s", VERSION);
        logger.debugf("GLSL: %s", SHADING_VERSION);
        logger.debugf("OpenGL Version: %s.%s", VERSION_MAJOR, VERSION_MINOR);
        logger.debugf("MAX_TEXTURE_SIZE: %s", MAX_TEXTURE_SIZE);
        if (MAX_DRAW_BUFFERS != -1) logger.debugf("MAX_DRAW_BUFFERS: %s", MAX_DRAW_BUFFERS);
    }
}
