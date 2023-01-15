package gamelauncher.lwjgl.render.shader;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import org.lwjgl.opengles.GLES20;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.render.states.GlStates;

@SuppressWarnings("javadoc")
public class LWJGLShaderProgram extends ShaderProgram {

	private final int programId;

	final AtomicInteger refCount = new AtomicInteger(1);

	final Path path;

	private int vertexShaderId;

	private int fragmentShaderId;

	public LWJGLShaderProgram(GameLauncher launcher, Path path) throws GameException {
		super(launcher);
		this.path = path;
		this.programId = GlStates.current().createProgram();
		if (this.programId == 0) {
			throw new GameException("Could not create Shader");
		}
	}

	public void createVertexShader(String shaderCode) throws GameException {
		this.vertexShaderId = this.createShader(shaderCode, GLES20.GL_VERTEX_SHADER);
	}

	public void createFragmentShader(String shaderCode) throws GameException {
		this.fragmentShaderId = this.createShader(shaderCode, GLES20.GL_FRAGMENT_SHADER);
	}

	public int getProgramId() {
		return this.programId;
	}

	public void deleteVertexShader() {
		GlStates.current().deleteShader(this.vertexShaderId);
	}

	public void deleteFragmentShader() {
		GlStates.current().deleteShader(this.fragmentShaderId);
	}

	protected int createShader(String shaderCode, int shaderType) throws GameException {
		int shaderId = GLES20.glCreateShader(shaderType);
		if (shaderId == 0) {
			throw new GameException("Error creating shader. Type: " + shaderType);
		}
		GlStates c = GlStates.current();

		c.shaderSource(shaderId, shaderCode);
		c.compileShader(shaderId);

		if (c.getShaderi(shaderId, GLES20.GL_COMPILE_STATUS) == 0) {
			throw new GameException("Error compiling Shader code: " + c.getShaderInfoLog(shaderId, 1024));
		}

		c.attachShader(this.programId, shaderId);

		return shaderId;
	}

	public void link() throws GameException {
		GlStates c = GlStates.current();
		c.linkProgram(this.programId);
		if (c.getProgrami(this.programId, GLES20.GL_LINK_STATUS) == 0) {
			throw new GameException("Error linking Shader code: " + c.getProgramInfoLog(this.programId, 1024));
		}

		if (this.vertexShaderId != 0) {
			c.detachShader(this.programId, this.vertexShaderId);
		}
		if (this.fragmentShaderId != 0) {
			c.detachShader(this.programId, this.fragmentShaderId);
		}

		if (this.launcher.debugMode()) {
			c.validateProgram(this.programId);
			if (c.getProgrami(this.programId, GLES20.GL_VALIDATE_STATUS) == 0) {
				this.launcher.logger()
						.warnf("Warning validating Shader code: %s", c.getProgramInfoLog(this.programId, 1024));
			}
		}
	}

	@Override
	public void bind() {
		GlStates.current().useProgram(this.programId);
	}

	@Override
	public void unbind() {
		GlStates.current().useProgram(0);
	}

	@Override
	public boolean cleanedUp() {
		return this.refCount.get() == 0;
	}

	@Override
	public void cleanup0() {
		if (this.refCount.decrementAndGet() == 0) {
			this.unbind();
			if (this.programId != 0) {
				GlStates.current().deleteProgram(this.programId);
			}
		}
	}

}
