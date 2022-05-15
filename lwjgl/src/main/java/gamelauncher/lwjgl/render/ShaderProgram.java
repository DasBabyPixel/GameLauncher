package gamelauncher.lwjgl.render;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import gamelauncher.engine.GameException;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.GameThread.GameRunnable;
import gamelauncher.lwjgl.render.Mesh.Material;
import gamelauncher.lwjgl.render.light.DirectionalLight;
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

	public void setUniform(String uniformName, PointLight pointLight) throws GameException {
		setUniform(uniformName + ".color", pointLight.color);
		setUniform(uniformName + ".position", pointLight.position);
		setUniform(uniformName + ".intensity", pointLight.intensity);
		PointLight.Attenuation att = pointLight.att;
		setUniform(uniformName + ".att.constant", att.constant);
		setUniform(uniformName + ".att.linear", att.linear);
		setUniform(uniformName + ".att.exponent", att.exponent);
	}

	public void setUniform(String uniformName, DirectionalLight light) throws GameException {
		setUniform(uniformName + ".color", light.color);
		setUniform(uniformName + ".direction", light.direction);
		setUniform(uniformName + ".intensity", light.intensity);
	}

	public void setUniform(String uniformName, Material material) throws GameException {
		setUniform(uniformName + ".ambient", material.ambientColour);
		setUniform(uniformName + ".diffuse", material.diffuseColour);
		setUniform(uniformName + ".specular", material.specularColour);
		setUniform(uniformName + ".hasTexture", material.texture != null ? 1 : 0);
		setUniform(uniformName + ".reflectance", material.reflectance);
	}

	public void createVertexShader(String shaderCode) throws GameException {
		vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
	}

	public void createFragmentShader(String shaderCode) throws GameException {
		fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
	}

	public void setUniform(String uniformName, int value) throws GameException {

		setUniform(uniformName, () -> glUniform1i(getUniform(uniformName), value));
	}

	public void setUniform(String uniformName, float value) throws GameException {

		setUniform(uniformName, () -> glUniform1f(getUniform(uniformName), value));
	}

	public void setUniform(String uniformName, Vector3f value) throws GameException {

		setUniform(uniformName, () -> glUniform3f(getUniform(uniformName), value.x, value.y, value.z));
	}

	public void setUniform(String uniformName, Vector4f value) throws GameException {

		setUniform(uniformName, () -> glUniform4f(getUniform(uniformName), value.x, value.y, value.z, value.w));
	}

	public void setUniform(String uniformName, Matrix4f value) throws GameException {
		setUniform(uniformName, () -> {
			// Dump the matrix into a float buffer
			int uniform = getUniform(uniformName);
			try (MemoryStack stack = MemoryStack.stackPush()) {
				FloatBuffer fb = stack.mallocFloat(16);
				value.get(fb);
				glUniformMatrix4fv(uniform, false, fb);
			}
		});
	}

	public boolean hasUniform(String uniformName) {
		return uniforms.containsKey(uniformName);
	}
	
	public GameLauncher getLauncher() {
		return launcher;
	}

	private void setUniform(String uniformName, GameRunnable run) throws GameException {
		if (!hasUniform(uniformName)) {
			return;
		}
//		System.out.println("Set " + uniformName);
		run.run();
	}

	private int getUniform(String uniformName) throws GameException {
		if (!hasUniform(uniformName)) {
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
				launcher.getLogger().warnf("Warning validating Shader code: %s", glGetProgramInfoLog(programId, 1024));
			}
		}

		int count = glGetProgrami(programId, GL_ACTIVE_UNIFORMS);
		stackPush();
		IntBuffer uniformSize = stackMallocInt(1);
		IntBuffer uniformType = stackMallocInt(1);
		for (int i = 0; i < count; i++) {
			String uniformName = glGetActiveUniform(programId, i, uniformSize, uniformType);
			uniforms.put(uniformName, glGetUniformLocation(programId, uniformName));
		}
		stackPop();
		launcher.getLogger().infof("Uniforms (%s): %n%s", count, uniforms);

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