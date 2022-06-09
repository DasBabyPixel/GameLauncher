package gamelauncher.lwjgl.render.shader;

import static org.lwjgl.opengl.GL20.*;

import java.util.HashMap;
import java.util.Map;

import gamelauncher.engine.GameException;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.util.GameResource;
import gamelauncher.lwjgl.render.GlStates;

public class ShaderProgram implements GameResource {

	private final int programId;

	private int vertexShaderId;

	private int fragmentShaderId;
	private final GameLauncher launcher;
	private final Map<String, Uniform> uniformMap;

	public ShaderProgram(GameLauncher launcher) throws GameException {
		this.launcher = launcher;
		this.uniformMap = new HashMap<>();
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

	public boolean hasUniform(String uniformName) {
		return uniformMap.containsKey(uniformName);
	}

	public GameLauncher getLauncher() {
		return launcher;
	}

	public Uniform getUniform(String uniformName) throws GameException {
		if (!hasUniform(uniformName)) {
			throw new GameException("No Uniform with name " + uniformName + " present");
		}
		return uniformMap.get(uniformName);
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

//		int count = glGetProgrami(programId, GL_ACTIVE_UNIFORMS);
//		stackPush();
//		IntBuffer uniformSize = stackMallocInt(1);
//		IntBuffer uniformType = stackMallocInt(1);
//		for (int i = 0; i < count; i++) {
//			String uniformName = glGetActiveUniform(programId, i, uniformSize, uniformType);
//			uniforms.put(uniformName, glGetUniformLocation(programId, uniformName));
//		}
//		stackPop();
	}

	public void bind() {
		GlStates.useProgram(programId);
	}

	public void unbind() {
		GlStates.useProgram(0);
	}

	@Override
	public void cleanup() {
		unbind();
		if (programId != 0) {
			glDeleteProgram(programId);
		}
	}
}