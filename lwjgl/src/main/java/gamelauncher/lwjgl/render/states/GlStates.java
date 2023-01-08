package gamelauncher.lwjgl.render.states;

import org.lwjgl.opengles.GLES20;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.opengles.GLES20.*;
import static org.lwjgl.opengles.GLES30.*;
import static org.lwjgl.opengles.GLES32.glCopyImageSubData;

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

	public void texParameteri(int target, int parameter, int value) {
		glTexParameteri(target, parameter, value);
	}

	/**
	 * @param target
	 * @param level
	 * @param internalFormat
	 * @param width
	 * @param height
	 * @param border
	 * @param dataFormat
	 * @param type
	 * @param data
	 *
	 * @see GLES20#glTexImage2D(int, int, int, int, int, int, int, int, ByteBuffer)
	 */
	public void texImage2D(int target, int level, int internalFormat, int width, int height,
			int border, int dataFormat, int type, ByteBuffer data) {
		glTexImage2D(target, level, internalFormat, width, height, border, dataFormat, type, data);
	}

	public void bindFramebuffer(int target, int framebuffer) {
		if (!bindFramebuffer.containsKey(target)
				|| bindFramebuffer.put(target, framebuffer) != framebuffer) {
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

	public void copyImageSubData(int srcTexture, int srcTarget, int srcLevel, int srcX, int srcY,
			int srcZ, int dstTexture, int dstTarget, int dstLevel, int dstX, int dstY, int dstZ,
			int copyw, int copyh, int copyd) {
		glCopyImageSubData(srcTexture, srcTarget, srcLevel, srcX, srcY, srcZ, dstTexture, dstTarget,
				dstLevel, dstX, dstY, dstZ, copyw, copyh, copyd);
	}

	public int genTextures() {
		return glGenTextures();
	}

	public void pixelStorei(int pname, int param) {
		glPixelStorei(pname, param);
	}

	public int genFramebuffers() {
		return glGenFramebuffers();
	}

	public void cullFace(int mode) {
		glCullFace(mode);
	}

	public void readPixels(int x, int y, int w, int h, int format, int type, ByteBuffer data) {
		glReadPixels(x, y, w, h, format, type, data);
	}

	public void deleteFramebuffers(int framebuffer) {
		glDeleteFramebuffers(framebuffer);
	}

	public void framebufferTexture2D(int target, int attachment, int textarget, int texture,
			int level) {
		glFramebufferTexture2D(target, attachment, textarget, texture, level);
	}

	public void texSubImage2D(int target, int level, int x, int y, int w, int h, int format,
			int type, ByteBuffer data) {
		glTexSubImage2D(target, level, x, y, w, h, format, type, data);
	}

	public void flush() {
		glFlush();
	}

	public void clear(int mask) {
		glClear(mask);
	}

	public void clearColor(float r, float g, float b, float a) {
		glClearColor(r, g, b, a);
	}

	public void finish() {
		glFinish();
	}

	public void enable(int state) {
		glEnable(state);
	}

	public void disable(int state) {
		glDisable(state);
	}

	public void scissor(int x, int y, int w, int h) {
		glScissor(x, y, w, h);
	}

	public void bindRenderbuffer(int target, int renderbuffer) {
		if (!bindRenderbuffer.containsKey(target)
				|| bindRenderbuffer.put(target, renderbuffer) != renderbuffer) {
			glBindRenderbuffer(target, renderbuffer);
		}
	}

	public void bindVertexArray(int vao) {
		if (bindVertexArray.getAndSet(vao) != vao) {
			glBindVertexArray(vao);
		}
	}

	public void depthFunc(int function) {
		glDepthFunc(function);
	}

	public void blendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
		glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
	}

	public void blendFunc(int src, int dst) {
		glBlendFunc(src, dst);
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

	public int genVertexArrays() {
		return glGenVertexArrays();
	}

	public void vertexAttribPointer(int index, int size, int type, boolean normalized, int stride,
			int pointer) {
		glVertexAttribPointer(index, size, type, normalized, stride, pointer);
	}

	public void bufferData(int target, IntBuffer data, int usage) {
		glBufferData(target, data, usage);
	}

	public void bufferData(int target, FloatBuffer data, int usage) {
		glBufferData(target, data, usage);
	}

	public void enableVertexAttribArray(int index) {
		glEnableVertexAttribArray(index);
	}

	public void drawElements(int mode, int count, int type, int indices) {
		glDrawElements(mode, count, type, indices);
	}

	public void disableVertexAttribArray(int index) {
		glDisableVertexAttribArray(index);
	}

	public void deleteBuffers(int vbo) {
		glDeleteBuffers(vbo);
	}

	public void deleteVertexArrays(int vaoId) {
		glDeleteVertexArrays(vaoId);
	}

	public int genBuffers() {
		return glGenBuffers();
	}

	public void framebufferRenderbuffer(int target, int attachment, int renderbuffertarget,
			int renderbuffer) {
		glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
	}

	public int checkFramebufferStatus(int framebuffertarget) {
		return glCheckFramebufferStatus(framebuffertarget);
	}

	public void deleteRenderbuffers(int id) {
		glDeleteRenderbuffers(id);
	}

	public void renderbufferStorage(int target, int internalformat, int width, int height) {
		glRenderbufferStorage(target, internalformat, width, height);
	}

	public int genRenderbuffers() {
		return glGenRenderbuffers();
	}

	public void viewport(int x, int y, int width, int height) {
		glViewport(x, y, width, height);
	}

	public void uniform1fv(int id, FloatBuffer floatBuffer) {
		glUniform1fv(id, floatBuffer);
	}

	public void uniform2fv(int id, FloatBuffer floatBuffer) {
		glUniform2fv(id, floatBuffer);
	}

	public void uniform3fv(int id, FloatBuffer floatBuffer) {
		glUniform3fv(id, floatBuffer);
	}

	public void uniform4fv(int id, FloatBuffer floatBuffer) {
		glUniform4fv(id, floatBuffer);
	}

	public void uniform1iv(int id, IntBuffer intBuffer) {
		glUniform1iv(id, intBuffer);
	}

	public void uniformMatrix4fv(int id, boolean transpose, FloatBuffer floatBuffer) {
		glUniformMatrix4fv(id, transpose, floatBuffer);
	}

	public void deleteProgram(int programId) {
		glDeleteProgram(programId);
	}

	public String getProgramInfoLog(int programId, int maxSize) {
		return glGetProgramInfoLog(programId, maxSize);
	}

	public int getProgrami(int programId, int param) {
		return glGetProgrami(programId, param);
	}

	public void validateProgram(int programId) {
		glValidateProgram(programId);
	}

	public void detachShader(int programId, int shader) {
		glDetachShader(programId, shader);
	}

	public void linkProgram(int programId) {
		glLinkProgram(programId);
	}

	public String getShaderInfoLog(int shaderId, int maxSize) {
		return glGetShaderInfoLog(shaderId, maxSize);
	}

	public void shaderSource(int shaderId, String shaderCode) {
		glShaderSource(shaderId, shaderCode);
	}

	public void compileShader(int shaderId) {
		glCompileShader(shaderId);
	}

	public void attachShader(int programId, int shaderId) {
		glAttachShader(programId, shaderId);
	}

	public int getShaderi(int shaderId, int param) {
		return glGetShaderi(shaderId, param);
	}

	public void deleteShader(int shader) {
		glDeleteShader(shader);
	}

	public int createProgram() {
		return glCreateProgram();
	}

	public int getUniformLocation(int programId, String name) {
		return glGetUniformLocation(programId, name);
	}

	public int getError() {
		return glGetError();
	}

	public void readBuffer(int buffer) {
		glReadBuffer(buffer);
	}

	public void drawBuffer(int buffer) {
		glDrawBuffers(buffer);
	}

	public void blitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0,
			int dstX1, int dstY1, int mask, int filter) {
		glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
	}


	public static class DepthState {

		public final BooleanState enabled = new BooleanState(GL_DEPTH_TEST);
		public int func;

		public void setFunc(int func) {
			if (this.func != func) {
				this.func = func;
				glDepthFunc(func);
			}
		}

	}


	public static class BlendState {

		public final BooleanState enabled = new BooleanState(GL_BLEND);
		public int srcrgb;
		public int dstrgb;
		public int srcalpha;
		public int dstalpha;

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

		public int getState() {
			return state;
		}

	}

}
