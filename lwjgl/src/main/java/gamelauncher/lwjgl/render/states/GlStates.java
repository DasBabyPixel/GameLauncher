package gamelauncher.lwjgl.render.states;

import static org.lwjgl.opengles.GLES20.*;
import static org.lwjgl.opengles.GLES30.*;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("javadoc")
public class GlStates {

	private final Map<Integer, Integer> bindTexture = new ConcurrentHashMap<>();
	private final Map<Integer, Integer> bindBuffer = new ConcurrentHashMap<>();
	private final Map<Integer, Integer> bindFramebuffer = new ConcurrentHashMap<>();
	private final Map<Integer, Integer> bindRenderbuffer = new ConcurrentHashMap<>();
	private final Collection<Integer> activeTexture = ConcurrentHashMap.newKeySet();
	private final BlendState blend = new BlendState();
	private final DepthState depth = new DepthState();
	private final AtomicInteger bindVertexArray = new AtomicInteger();
	private final AtomicInteger useProgram = new AtomicInteger();

	public static GlStates current() {
		return StateRegistry.currentContext().states;
	}
	
	public BlendState getBlendState() {
		return blend;
	}

	public DepthState getDepthState() {
		return depth;
	}

	public void bindFramebuffer(int target, int framebuffer) {
		if (!bindFramebuffer.containsKey(target) || bindFramebuffer.put(target, framebuffer) != framebuffer) {
			glBindFramebuffer(target, framebuffer);
		}
	}

	public void deleteTextures(int texture) {
		glDeleteTextures(texture);
		for (Map.Entry<Integer, Integer> e : bindTexture.entrySet()) {
			if (e.getValue() == texture) {
				bindTexture.remove(e.getKey());
			}
		}
	}

	public void bindRenderbuffer(int target, int renderbuffer) {
		if (!bindRenderbuffer.containsKey(target) || bindRenderbuffer.put(target, renderbuffer) != renderbuffer) {
			glBindRenderbuffer(target, renderbuffer);
		}
	}

	public void bindVertexArray(int vao) {
		if (bindVertexArray.getAndSet(vao) != vao) {
			glBindVertexArray(vao);
		}
	}

	public void bindTexture(int target, int texture) {
		if (!bindTexture.containsKey(target) || bindTexture.put(target, texture) != texture) {
			glBindTexture(target, texture);
		}
	}

	public void activeTexture(int texture) {
		if (activeTexture.add(texture)) {
			glActiveTexture(texture);
		}
	}

	public void bindBuffer(int target, int buffer) {
		if (!bindBuffer.containsKey(target) || bindBuffer.put(target, buffer) != buffer) {
			glBindBuffer(target, buffer);
		}
	}

	public void useProgram(int program) {
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
