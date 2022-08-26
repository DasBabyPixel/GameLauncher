package gamelauncher.lwjgl.render.shader;

import static org.lwjgl.opengles.GLES20.*;

import java.util.concurrent.atomic.AtomicInteger;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.render.states.GlStates;

@SuppressWarnings("javadoc")
public class LWJGLShaderProgram extends ShaderProgram {

	private final int programId;

	final AtomicInteger refCount = new AtomicInteger(1);

	private int vertexShaderId;

	private int fragmentShaderId;

	public LWJGLShaderProgram(GameLauncher launcher) throws GameException {
		super(launcher);
		this.programId = GlStates.current().createProgram();
		if (this.programId == 0) {
			throw new GameException("Could not create Shader");
		}
	}

	public void createVertexShader(String shaderCode) throws GameException {
		vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
	}

	public void createFragmentShader(String shaderCode) throws GameException {
		fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
	}

	public int getProgramId() {
		return programId;
	}

	public void deleteVertexShader() {
		GlStates.current().deleteShader(vertexShaderId);
	}

	public void deleteFragmentShader() {
		GlStates.current().deleteShader(fragmentShaderId);
	}

	protected int createShader(String shaderCode, int shaderType) throws GameException {
		int shaderId = glCreateShader(shaderType);
		if (shaderId == 0) {
			throw new GameException("Error creating shader. Type: " + shaderType);
		}
		GlStates c = GlStates.current();

		c.shaderSource(shaderId, shaderCode);
		c.compileShader(shaderId);

		if (c.getShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
			throw new GameException("Error compiling Shader code: " + c.getShaderInfoLog(shaderId, 1024));
		}

		c.attachShader(programId, shaderId);

		return shaderId;
	}

	public void link() throws GameException {
		GlStates c = GlStates.current();
		c.linkProgram(programId);
		if (c.getProgrami(programId, GL_LINK_STATUS) == 0) {
			throw new GameException("Error linking Shader code: " + c.getProgramInfoLog(programId, 1024));
		}

		if (vertexShaderId != 0) {
			c.detachShader(programId, vertexShaderId);
		}
		if (fragmentShaderId != 0) {
			c.detachShader(programId, fragmentShaderId);
		}

		if (launcher.isDebugMode()) {
			c.validateProgram(programId);
			if (c.getProgrami(programId, GL_VALIDATE_STATUS) == 0) {
				launcher.getLogger().warnf("Warning validating Shader code: %s", c.getProgramInfoLog(programId, 1024));
			}
		}
	}

	@Override
	public void bind() {
		GlStates.current().useProgram(programId);
	}

	@Override
	public void unbind() {
		GlStates.current().useProgram(0);
	}

	@Override
	public void cleanup0() {
		if (refCount.decrementAndGet() == 0) {
			unbind();
			if (programId != 0) {
				GlStates.current().deleteProgram(programId);
			}
		}
	}

}