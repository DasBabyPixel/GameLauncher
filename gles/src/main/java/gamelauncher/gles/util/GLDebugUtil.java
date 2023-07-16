/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.gles.util;

import gamelauncher.engine.resource.DummyGameResource;
import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.util.Debug;
import gamelauncher.gles.gl.GLES32;
import gamelauncher.gles.states.ContextLocal;
import gamelauncher.gles.states.StateRegistry;

import java.io.PrintStream;

import static gamelauncher.gles.gl.GLES32.*;

public class GLDebugUtil {

    public static final ContextLocal<Boolean> skip = ContextLocal.empty();

    public static GameResource setupDebugMessageCallback(PrintStream stream) {
        if (!Debug.debug) {
            return new DummyGameResource();
        }

        GLES32 gl = StateRegistry.currentGl();
        if ((gl.glGetInteger(GL_CONTEXT_FLAGS) & GLES32.GL_CONTEXT_FLAG_DEBUG_BIT) == 0) {
            stream.println("[GLES] Warning: A non-debug context may not produce any debug output.");
//            return new DummyGameResource();
        }
        GameResource r;
        try {
            r = gl.glDebugMessageCallback((source, type, id, severity, message) -> {
                if (StateRegistry.currentContext() != null) {
                    if (skip.has()) {
                        return;
                    }
                } else {
                    stream.println("OpenGL Error on Thread without OpenGL context: " + Thread.currentThread().getName());
                }
                stream.println("[LWJGL] OpenGL debug message");
                printDetail(stream, "ID", String.format("0x%X", id));
                printDetail(stream, "Source", getDebugSource(source));
                printDetail(stream, "Type", getDebugType(type));
                printDetail(stream, "Severity", getDebugSeverity(severity));
                printDetail(stream, "Message", message);
                new Exception().printStackTrace(stream);
            });
            stream.println("[GLES] Initialized debug logging");
        } catch (Throwable t) {
            gl.glGetError();
            stream.println("[GLES] Failed to initialize debug logging: " + t.getMessage());
            r = new DummyGameResource();
        }
        return r;
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
                return apiUnknownToken(source);
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
                return apiUnknownToken(type);
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
                return apiUnknownToken(severity);
        }
    }

    public static String apiUnknownToken(int token) {
        return apiUnknownToken("Unknown", token);
    }

    public static String apiUnknownToken(String description, int token) {
        return String.format("%s [0x%X]", description, token);
    }

    private static void printDetail(PrintStream stream, String type, String message) {
        stream.printf("\t%s: %s\n", type, message);
    }

    public interface DebugMessageCallback {
        void free();
    }
}
