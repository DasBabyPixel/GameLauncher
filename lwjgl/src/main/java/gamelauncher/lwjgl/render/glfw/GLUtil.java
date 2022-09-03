package gamelauncher.lwjgl.render.glfw;

import static org.lwjgl.opengles.GLES20.*;
import static org.lwjgl.opengles.GLES32.*;
import static org.lwjgl.system.APIUtil.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.io.PrintStream;

import javax.annotation.Nullable;

import org.lwjgl.opengles.GLDebugMessageCallback;
import org.lwjgl.opengles.GLES;
import org.lwjgl.opengles.GLESCapabilities;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.Callback;

import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.states.ContextLocal;

/** OpenGL utilities. */
@SuppressWarnings("javadoc")
public final class GLUtil {

	private static final int GL_CONTEXT_FLAGS = 0x821E;

	static volatile ContextLocal<Boolean> skip = null;

	private GLUtil() {
	}

	public static void clinit(LWJGLGameLauncher launcher) {
		skip = ContextLocal.empty(launcher);
	}

	/**
	 * Detects the best debug output functionality to use and creates a callback
	 * that prints information to {@link APIUtil#DEBUG_STREAM}. The callback
	 * function is returned as a {@link Callback}, that should be
	 * {@link Callback#free freed} when no longer needed.
	 */
	@Nullable
	public static Callback setupDebugMessageCallback() {
		return setupDebugMessageCallback(APIUtil.DEBUG_STREAM);
	}

	/**
	 * Detects the best debug output functionality to use and creates a callback
	 * that prints information to the specified {@link PrintStream}. The callback
	 * function is returned as a {@link Callback}, that should be
	 * {@link Callback#free freed} when no longer needed.
	 *
	 * @param stream the output {@link PrintStream}
	 */
	@Nullable
	public static Callback setupDebugMessageCallback(PrintStream stream) {
		GLESCapabilities caps = GLES.getCapabilities();

		if (caps.GLES32) {
			apiLog("[GL] Using OpenGL 4.3 for error logging.");
			GLDebugMessageCallback proc = GLDebugMessageCallback
					.create((source, type, id, severity, length, message, userParam) -> {
						if (skip.has()) {
							return;
						}
						stream.println("[LWJGL] OpenGL debug message");
						printDetail(stream, "ID", String.format("0x%X", id));
						printDetail(stream, "Source", getDebugSource(source));
						printDetail(stream, "Type", getDebugType(type));
						printDetail(stream, "Severity", getDebugSeverity(severity));
						printDetail(stream, "Message", GLDebugMessageCallback.getMessage(length, message));
					});
			glDebugMessageCallback(proc, NULL);
			if ((glGetInteger(GL_CONTEXT_FLAGS) & GL_CONTEXT_FLAG_DEBUG_BIT) == 0) {
				apiLog("[GL] Warning: A non-debug context may not produce any debug output.");
			}
			return proc;
		}
		apiLog("[GL] No debug output implementation is available.");
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

}