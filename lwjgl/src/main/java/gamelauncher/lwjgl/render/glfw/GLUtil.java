package gamelauncher.lwjgl.render.glfw;

import de.dasbabypixel.annotations.Api;
import gamelauncher.gles.states.ContextLocal;
import gamelauncher.gles.states.StateRegistry;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import org.jetbrains.annotations.Nullable;
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

    private static final int GL_CONTEXT_FLAGS = 0x821E;

    static volatile ContextLocal<Boolean> skip = null;

    private GLUtil() {
    }

    public static void clinit(LWJGLGameLauncher launcher) {
        GLUtil.skip = ContextLocal.empty();
    }

    /**
     * Detects the best debug output functionality to use and creates a callback that prints
     * information to {@link APIUtil#DEBUG_STREAM}. The callback function is returned as a
     * {@link Callback}, that should be {@link Callback#free freed} when no longer needed.
     *
     * @return the generated callback function
     */
    @Api @Nullable public static Callback setupDebugMessageCallback() {
        return GLUtil.setupDebugMessageCallback(APIUtil.DEBUG_STREAM);
    }

    /**
     * Detects the best debug output functionality to use and creates a callback that prints
     * information to the specified {@link PrintStream}. The callback function is returned as a
     * {@link Callback}, that should be {@link Callback#free freed} when no longer needed.
     *
     * @param stream the output {@link PrintStream}
     * @return the generated callback function
     */
    @Api @Nullable public static Callback setupDebugMessageCallback(PrintStream stream) {
        GLESCapabilities caps = GLES.getCapabilities();

        if (caps.GLES32) {
            APIUtil.apiLog("[GL] Using OpenGL 4.3 for error logging.");
            GLDebugMessageCallback proc = GLDebugMessageCallback.create((source, type, id, severity, length, message, userParam) -> {
                if (id == 0x20071) {
                    // Notification about buffer details
                    return;
                }
                if (StateRegistry.currentContext() != null) {
                    if (GLUtil.skip.has()) {
                        return;
                    }
                } else {
                    stream.println("OpenGL Error on Thread without OpenGL context: " + Thread.currentThread().getName());
                }
                stream.println("[LWJGL] OpenGL debug message");
                GLUtil.printDetail(stream, "ID", String.format("0x%X", id));
                GLUtil.printDetail(stream, "Source", GLUtil.getDebugSource(source));
                GLUtil.printDetail(stream, "Type", GLUtil.getDebugType(type));
                GLUtil.printDetail(stream, "Severity", GLUtil.getDebugSeverity(severity));
                GLUtil.printDetail(stream, "Message", GLDebugMessageCallback.getMessage(length, message));
                Thread.dumpStack();
            });
            GLES32.glDebugMessageCallback(proc, MemoryUtil.NULL);
            if ((GLES20.glGetInteger(GLUtil.GL_CONTEXT_FLAGS) & GLES32.GL_CONTEXT_FLAG_DEBUG_BIT) == 0) {
                APIUtil.apiLog("[GL] Warning: A non-debug context may not produce any debug output.");
            }
            return proc;
        }
        APIUtil.apiLog("[GL] No debug output implementation is available.");
        return null;
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
