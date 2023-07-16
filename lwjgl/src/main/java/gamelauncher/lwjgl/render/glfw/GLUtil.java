/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.lwjgl.render.glfw;

import de.dasbabypixel.annotations.Api;
import gamelauncher.gles.states.ContextLocal;
import gamelauncher.gles.states.StateRegistry;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.LWJGLGL;
import gamelauncher.lwjgl.render.LWJGLGLES;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengles.*;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryUtil;

import java.io.PrintStream;

import static org.lwjgl.opengles.GLES32.*;

/**
 * OpenGL utilities.
 */
public final class GLUtil {

    private GLUtil() {
    }

    public static void setNullCapabilities() {
        if (LWJGLGameLauncher.USE_GLES.value()) LWJGLGLES.setCapabilities(null);
        else LWJGLGL.setCapabilities(null);
    }

    public static void createCapabilities() {
        if (LWJGLGameLauncher.USE_GLES.value()) LWJGLGLES.createCapabilities();
        else LWJGLGL.createCapabilities();
    }

    public static gamelauncher.gles.gl.GLES32 getGL() {
        return LWJGLGameLauncher.USE_GLES.value() ? LWJGLGLES.instance : LWJGLGL.instance;
    }

    private static void printDetail(PrintStream stream, String type, String message) {
        stream.printf("\t%s: %s\n", type, message);
    }

    private static String getDebugSource(int source) {
        switch (source) {
            case GL_DEBUG_SOURCE_API:
                return "API";
            case GL_DEBUG_SOURCE_WINDOW_SYSTEM:
                return "WINDOW SYSTEM";
            case GL_DEBUG_SOURCE_SHADER_COMPILER:
                return "SHADER COMPILER";
            case GL_DEBUG_SOURCE_THIRD_PARTY:
                return "THIRD PARTY";
            case GL_DEBUG_SOURCE_APPLICATION:
                return "APPLICATION";
            case GL_DEBUG_SOURCE_OTHER:
                return "OTHER";
            default:
                return APIUtil.apiUnknownToken(source);
        }
    }

    private static String getDebugType(int type) {
        switch (type) {
            case GL_DEBUG_TYPE_ERROR:
                return "ERROR";
            case GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR:
                return "DEPRECATED BEHAVIOR";
            case GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR:
                return "UNDEFINED BEHAVIOR";
            case GL_DEBUG_TYPE_PORTABILITY:
                return "PORTABILITY";
            case GL_DEBUG_TYPE_PERFORMANCE:
                return "PERFORMANCE";
            case GL_DEBUG_TYPE_OTHER:
                return "OTHER";
            case GL_DEBUG_TYPE_MARKER:
                return "MARKER";
            default:
                return APIUtil.apiUnknownToken(type);
        }
    }

    private static String getDebugSeverity(int severity) {
        switch (severity) {
            case GL_DEBUG_SEVERITY_HIGH:
                return "HIGH";
            case GL_DEBUG_SEVERITY_MEDIUM:
                return "MEDIUM";
            case GL_DEBUG_SEVERITY_LOW:
                return "LOW";
            case GL_DEBUG_SEVERITY_NOTIFICATION:
                return "NOTIFICATION";
            default:
                return APIUtil.apiUnknownToken(severity);
        }
    }
}
