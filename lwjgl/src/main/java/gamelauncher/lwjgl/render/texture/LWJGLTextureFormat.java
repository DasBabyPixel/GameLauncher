package gamelauncher.lwjgl.render.texture;

import static org.lwjgl.opengles.GLES20.*;

/**
 * @author DasBabyPixel
 */
@SuppressWarnings("javadoc")
public enum LWJGLTextureFormat {

	RGBA(GL_RGBA, 4), ALPHA(GL_RGBA, 4);

	;

	public final int gl;
	public final int glInternal;
	public final int size;

	private LWJGLTextureFormat(int gl, int size) {
		this(gl, gl, size);
	}

	private LWJGLTextureFormat(int gl, int glInternal, int size) {
		this.gl = gl;
		this.glInternal = glInternal;
		this.size = size;
	}
}
