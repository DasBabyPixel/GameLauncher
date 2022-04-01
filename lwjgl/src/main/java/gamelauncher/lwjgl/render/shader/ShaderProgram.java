package gamelauncher.lwjgl.render.shader;

import static org.lwjgl.opengl.GL20.*;

import gamelauncher.engine.GameException;
import gamelauncher.engine.GameLauncher;

public class ShaderProgram {

	private final int programId;

	private int vertexShaderId;

	private int fragmentShaderId;
	private final GameLauncher launcher;

	public ShaderProgram(GameLauncher launcher) throws GameException {
		this.launcher = launcher;
		programId = glCreateProgram();
		if (programId == 0) {
			throw new GameException("Could not create Shader");
		}
	}

	public void createVertexShader(String shaderCode) throws GameException {
		vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
	}

	public void createFragmentShader(String shaderCode) throws GameException {
		fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
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
				System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
			}
		}
	}

	public void bind() {
		glUseProgram(programId);
	}

	public void unbind() {
		glUseProgram(0);
	}

	public void cleanup() {
		unbind();
		if (programId != 0) {
			glDeleteProgram(programId);
		}
	}
}