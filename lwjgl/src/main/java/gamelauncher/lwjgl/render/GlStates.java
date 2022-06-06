package gamelauncher.lwjgl.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GlStates {

	private static final Map<Integer, Integer> bindTexture = new ConcurrentHashMap<>();
	private static final Map<Integer, Integer> bindBuffer = new ConcurrentHashMap<>();
	private static final Map<Integer, Integer> bindFramebuffer = new ConcurrentHashMap<>();
	private static final Map<Integer, Integer> bindRenderbuffer = new ConcurrentHashMap<>();
	private static final Collection<Integer> activeTexture = ConcurrentHashMap.newKeySet();
	private static final AtomicInteger bindVertexArray = new AtomicInteger();
	private static final AtomicInteger useProgram = new AtomicInteger();

	public static void bindFramebuffer(int target, int framebuffer) {
		if (bindFramebuffer.put(target, framebuffer) != Integer.valueOf(framebuffer)) {
			glBindFramebuffer(target, framebuffer);
		}
	}
	
	public static void deleteTextures(int texture) {
		
	}

	public static void bindRenderbuffer(int target, int renderbuffer) {
		if (bindRenderbuffer.put(target, renderbuffer) != Integer.valueOf(renderbuffer)) {
			glBindRenderbuffer(target, renderbuffer);
		}
	}

	public static void bindVertexArray(int vao) {
		if (bindVertexArray.getAndSet(vao) != vao) {
			glBindVertexArray(vao);
		}
	}

	public static void bindTexture(int target, int texture) {
		if (bindTexture.put(target, texture) != Integer.valueOf(texture)) {
			glBindTexture(target, texture);
		}
	}

	public static void activeTexture(int texture) {
		if (activeTexture.add(texture)) {
			glActiveTexture(texture);
		}
	}

	public static void bindBuffer(int target, int buffer) {
		if (bindBuffer.put(target, buffer) != Integer.valueOf(buffer)) {
			glBindBuffer(target, buffer);
		}
	}

	public static void useProgram(int program) {
		if (useProgram.getAndSet(program) != program) {
			glUseProgram(program);
		}
	}

}
