package gamelauncher.gles.texture;

import static gamelauncher.gles.gl.GLES20.GL_ALPHA;
import static gamelauncher.gles.gl.GLES20.GL_RGBA;

/**
 * @author DasBabyPixel
 */
public enum GLESTextureFormat {

    RGBA(GL_RGBA, 4), ALPHA(GL_ALPHA, GL_ALPHA, 1);

    private final int gl;
    private final int glInternal;
    private final int size;

    GLESTextureFormat(int gl, int size) {
        this(gl, gl, size);
    }

    GLESTextureFormat(int gl, int glInternal, int size) {
        this.gl = gl;
        this.glInternal = glInternal;
        this.size = size;
    }

    public int gl() {
        return gl;
    }

    public int glInternal() {
        return glInternal;
    }

    public int size() {
        return size;
    }
}
