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
	private final LWJGLShaderLoader loader;
	
	private int vertexShaderId;

	private int fragmentShaderId;

	public LWJGLShaderProgram(LWJGLShaderLoader loader, GameLauncher launcher) throws GameException {
		super(launcher);
		this.loader = loader;
		this.programId = glCreateProgram();
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
		glDeleteShader(vertexShaderId);
	}

	public void deleteFragmentShader() {
		glDeleteShader(fragmentShaderId);
	}

	protected int createShader(String shaderCode, int shaderType) throws GameException {
		int shaderId = glCreateShader(shaderType);
		if (shaderId == 0) {
			throw new GameException("Error creating shader. Type: " + shaderType);
		}

		glShaderSource(shaderId, shaderCode);
		glCompileShader(shaderId);

		if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
			throw new GameException("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
		}

		glAttachShader(programId, shaderId);

		return shaderId;
	}

	public void link() throws GameException {
		glLinkProgram(programId);
		if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
			throw new GameException("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
		}

		if (vertexShaderId != 0) {
			glDetachShader(programId, vertexShaderId);
		}
		if (fragmentShaderId != 0) {
			glDetachShader(programId, fragmentShaderId);
		}

		if (launcher.isDebugMode()) {
			glValidateProgram(programId);
			if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
				launcher.getLogger().warnf("Warning validating Shader code: %s", glGetProgramInfoLog(programId, 1024));
			}
		}
	}

	@Override
	public void bind() {
		GlStates.useProgram(programId);
	}

	@Override
	public void unbind() {
		GlStates.useProgram(0);
	}

	@Override
	public void cleanup() {
		if (refCount.decrementAndGet() == 0) {
			unbind();
			if (programId != 0) {
				glDeleteProgram(programId);
			}
		}
	}
}