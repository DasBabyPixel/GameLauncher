package gamelauncher.lwjgl.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("javadoc")
public class GlStates {

	private static final Map<Integer, Integer> bindTexture = new ConcurrentHashMap<>();
	private static final Map<Integer, Integer> bindBuffer = new ConcurrentHashMap<>();
	private static final Map<Integer, Integer> bindFramebuffer = new ConcurrentHashMap<>();
	private static final Map<Integer, Integer> bindRenderbuffer = new ConcurrentHashMap<>();
	private static final Collection<Integer> activeTexture = ConcurrentHashMap.newKeySet();
	private static final BlendState blend = new BlendState();
	private static final DepthState depth = new DepthState();
	private static final AtomicInteger bindVertexArray = new AtomicInteger();
	private static final AtomicInteger useProgram = new AtomicInteger();

	public static BlendState getBlendState() {
		return blend;
	}
	
	public static DepthState getDepthState() {
		return depth;
	}

	public static void bindFramebuffer(int target, int framebuffer) {
		if (bindFramebuffer.put(target, framebuffer) != Integer.valueOf(framebuffer)) {
			glBindFramebuffer(target, framebuffer);
		}
	}

	public static void deleteTextures(int texture) {
		glDeleteTextures(texture);
		for (Map.Entry<Integer, Integer> e : bindTexture.entrySet()) {
			if (e.getValue() == texture) {
				bindTexture.remove(e.getKey());
			}
		}
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

	public static class DepthState {
		
		public int func;
		public final BooleanState enabled = new BooleanState(GL_DEPTH_TEST);

		public void setFunc(int func) {
			if (this.func != func) {
				this.func = func;
				glDepthFunc(func);
			}
		}
	}

	public static class BlendState {

		public int srcrgb;
		public int dstrgb;
		public int srcalpha;
		public int dstalpha;
		public final BooleanState enabled = new BooleanState(GL_BLEND);

		public void set(int srcrgb, int dstrgb, int srcalpha, int dstalpha) {
			if (this.srcrgb != srcrgb || this.dstrgb != dstrgb || this.srcalpha != srcalpha
					|| this.dstalpha != dstalpha) {
				this.srcrgb = srcrgb;
				this.dstrgb = dstrgb;
				this.srcalpha = srcalpha;
				this.dstalpha = dstalpha;
				glBlendFuncSeparate(srcrgb, dstrgb, srcalpha, dstalpha);
			}
		}
	}

	public static class BooleanState {
		public final int state;
		public boolean enabled = false;

		public BooleanState(int state) {
			this.state = state;
		}

		public BooleanState(int state, boolean enabled) {
			this.state = state;
			this.enabled = enabled;
		}

		public void enable() {
			setEnabled(true);
		}

		public void disable() {
			setEnabled(false);
		}

		public boolean isEnabled() {
			return enabled;
		}

		public int getState() {
			return state;
		}

		public void setEnabled(boolean value) {
			if (value != enabled) {
				enabled = value;
				if (enabled) {
					glEnable(state);
				} else {
					glDisable(state);
				}
			}
		}
	}
}
