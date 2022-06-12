package gamelauncher.lwjgl.render.shader;

import static org.lwjgl.opengl.GL20.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
	public final Map<String, Uniform> uniformMap;
	public final Collection<Uniform> uploadUniforms;
//	private final FloatBuffer fbuf;

	public Uniform umaterial = EmptyUniform.instance;
	public Uniform umodelMatrix = EmptyUniform.instance;
	public Uniform ucolor = EmptyUniform.instance;
	public Uniform umodelViewMatrix = EmptyUniform.instance;
	public Uniform uviewMatrix = EmptyUniform.instance;
	public Uniform uprojectionMatrix = EmptyUniform.instance;
	public Uniform ucamera_pos = EmptyUniform.instance;
	public Uniform uambientLight = EmptyUniform.instance;
	public Uniform utexture_sampler = EmptyUniform.instance;
	public Uniform uspecularPower = EmptyUniform.instance;
	public Uniform upointLight = EmptyUniform.instance;
	public Uniform udirectionalLight = EmptyUniform.instance;
	public Uniform utextureAddColor = EmptyUniform.instance;

	public ShaderProgram(GameLauncher launcher) throws GameException {
		this.launcher = launcher;
		this.uniformMap = new HashMap<>();
		this.uploadUniforms = new HashSet<>();
		this.programId = glCreateProgram();
		if (this.programId == 0) {
			throw new GameException("Could not create Shader");
		}
//		fbuf = memAllocFloat(16);
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

	public void uploadUniforms() {
		for (Uniform uniform : uploadUniforms) {
			uniform.upload();
		}
	}

	public void clearUniforms() {
		for (Uniform uniform : uniformMap.values()) {
			uniform.clear();
		}
	}

	public int getProgramId() {
		return programId;
	}

//	public void setUniform(String uniform, float f) {
//		set(uniform, id -> glUniform1f(id, f));
//	}
//
//	public void setUniform(String uniform, Vector2f vec) {
//		set(uniform, id -> glUniform2f(id, vec.x, vec.y));
//	}
//
//	public void setUniform(String uniform, Vector3f vec) {
//		set(uniform, id -> glUniform3f(id, vec.x, vec.y, vec.z));
//	}
//
//	public void setUniform(String uniform, Vector4f vec) {
//		set(uniform, id -> glUniform4f(id, vec.x, vec.y, vec.z, vec.w));
//	}
//
//	public void setUniform(String uniform, Matrix4f mat) {
//		set(uniform, id -> {
//			mat.get(fbuf);
//			glUniformMatrix4fv(id, false, fbuf);
//		});
//	}
//
//	public void setUniform(String uniform, PointLight light) {
//		setUniform(uniform + ".color", light.color);
//		setUniform(uniform + ".intensity", light.intensity);
//		setUniform(uniform + ".position", light.position);
//		setUniform(uniform + ".att", light.att);
//	}
//
//	public void setUniform(String uniform, Attenuation att) {
//		setUniform(uniform + ".constant", att.constant);
//		setUniform(uniform + ".exponent", att.exponent);
//		setUniform(uniform + ".linear", att.linear);
//	}
//
//	public void setUniform(String uniform, DirectionalLight light) {
//		setUniform(uniform + ".color", light.color);
//		setUniform(uniform + ".direction", light.direction);
//		setUniform(uniform + ".intensity", light.intensity);
//	}
//
//	public void setUniform(String uniform, Material mat) {
//		setUniform(uniform + ".ambient", mat.ambientColour);
//		setUniform(uniform + ".diffuse", mat.diffuseColour);
//		setUniform(uniform + ".reflectance", mat.reflectance);
//		setUniform(uniform + ".specular", mat.specularColour);
//		setUniform(uniform + ".hasTexture", mat.texture == null ? 0 : 1);
//	}
//
//	public void setUniform(String uniform, int i) {
//		set(uniform, id -> glUniform1i(id, i));
//	}
//
//	private void set(String uniform, GameConsumer<Integer> run) {
//		if (hasUniform(uniform)) {
//			try {
//				run.accept(uniformMap.get(uniform));
//			} catch (GameException ex) {
//				ex.printStackTrace();
//			}
//		}
//	}

//	public Uniform getUniform(String uniformName) throws GameException {
//		if (!hasUniform(uniformName)) {
//			throw new GameException("No Uniform with name " + uniformName + " present");
//		}
//		return uniformMap.get(uniformName);
//	}

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
//			uniformMap.put(uniformName, glGetUniformLocation(programId, uniformName));
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