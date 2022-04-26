package gamelauncher.lwjgl.render;

import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import gamelauncher.engine.GameException;
import gamelauncher.engine.GameLauncher;
import gamelauncher.lwjgl.render.light.Material;
import gamelauncher.lwjgl.render.light.PointLight;

public class ShaderProgram {

	private final int programId;

	private int vertexShaderId;

	private int fragmentShaderId;
	private final GameLauncher launcher;
	private final Map<String, Integer> uniforms;

	public ShaderProgram(GameLauncher launcher) throws GameException {
		this.launcher = launcher;
		this.uniforms = new HashMap<>();
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

	public void createUniform(String uniformName) throws GameException {
		int uniformLocation = glGetUniformLocation(programId, uniformName);
		if (uniformLocation < 0) {
			throw new GameException("Could not find uniform: " + uniformName);
		}
		uniforms.put(uniformName, uniformLocation);
	}

	public void createPointLightUniform(String uniformName) throws GameException {
		createUniform(uniformName + ".color");
		createUniform(uniformName + ".position");
		createUniform(uniformName + ".intensity");
		createUniform(uniformName + ".att.constant");
		createUniform(uniformName + ".att.linear");
		createUniform(uniformName + ".att.exponent");
	}

	public void createMaterialUniform(String uniformName) throws GameException {
		createUniform(uniformName + ".ambient");
		createUniform(uniformName + ".diffuse");
		createUniform(uniformName + ".specular");
		createUniform(uniformName + ".hasTexture");
		createUniform(uniformName + ".reflectance");
	}

	public void setUniform(String uniformName, PointLight pointLight) throws GameException {
		setUniform(uniformName + ".color", pointLight.color);
		setUniform(uniformName + ".position", pointLight.position);
		setUniform(uniformName + ".intensity", pointLight.intensity);
		PointLight.Attenuation att = pointLight.att;
		setUniform(uniformName + ".att.constant", att.constant);
		setUniform(uniformName + ".att.exponent", att.exponent);
		setUniform(uniformName + ".att.linear", att.linear);
	}

	public void setUniform(String uniformName, Material material) throws GameException {
		setUniform(uniformName + ".ambient", material.ambient);
		setUniform(uniformName + ".diffuse", material.diffuse);
		setUniform(uniformName + ".reflectance", material.reflectance);
		setUniform(uniformName + ".specular", material.specular);
		setUniform(uniformName + ".hasTexture", material.texture != null ? 1 : 0);
	}

	public void setUniform(String uniformName, int value) throws GameException {
		glUniform1i(getUniform(uniformName), value);
	}

	public void setUniform(String uniformName, float value) throws GameException {
		glUniform1f(getUniform(uniformName), value);
	}

	public void setUniform(String uniformName, Vector3f value) throws GameException {
		glUniform3f(getUniform(uniformName), value.x, value.y, value.z);
	}

	public void setUniform(String uniformName, Vector4f value) throws GameException {
		glUniform4f(getUniform(uniformName), value.x, value.y, value.z, value.w);
	}

	public void setUniform(String uniformName, Matrix4f value) throws GameException {
		// Dump the matrix into a float buffer
		int uniform = getUniform(uniformName);
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer fb = stack.mallocFloat(16);
			value.get(fb);
			glUniformMatrix4fv(uniform, false, fb);
		}
	}

	private int getUniform(String uniformName) throws GameException {
		if (!uniforms.containsKey(uniformName)) {
			throw new GameException("No Uniform with name " + uniformName + " present");
		}
		return uniforms.get(uniformName);
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