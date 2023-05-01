/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.gles;

import gamelauncher.engine.util.logging.Logger;

public class GLESCompat {
    public static String VERSION = null;
    public static String SHADING_VERSION = null;
    public static int VERSION_MAJOR = -1;
    public static int VERSION_MINOR = -1;
    public static int MAX_TEXTURE_SIZE = -1;
    public static int MAX_TEXTURE_IMAGE_UNITS = -1;
    public static int MAX_DRAW_BUFFERS = -1;

    public static void printDebugInfos(Logger logger) {
        logger.debugf("OpenGL: %s%nGLSL: %s%nOpenGL Version: %s.%s", VERSION, SHADING_VERSION, VERSION_MAJOR, VERSION_MINOR);
        logger.debugf("MAX_TEXTURE_SIZE: %s", MAX_TEXTURE_SIZE);
    }
}
