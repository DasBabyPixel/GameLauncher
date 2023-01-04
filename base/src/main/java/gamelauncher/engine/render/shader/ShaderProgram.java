package gamelauncher.engine.render.shader;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.resource.AbstractGameResource;

/**
 * @author DasBabyPixel
 */
@SuppressWarnings("javadoc")
public abstract class ShaderProgram extends AbstractGameResource {

	protected final GameLauncher launcher;

	public final Map<String, Uniform> uniformMap;
	public final Collection<Uniform> uploadUniforms;

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
	public Uniform uapplyLighting = EmptyUniform.instance;
	public Uniform uhasTexture = EmptyUniform.instance;

	/**
	 * @param launcher
	 */
	public ShaderProgram(GameLauncher launcher) {
		this.launcher = launcher;
		this.uniformMap = new HashMap<>();
		this.uploadUniforms = new HashSet<>();
	}

	/**
	 * @return the {@link GameLauncher}
	 */
	public GameLauncher getLauncher() {
		return this.launcher;
	}

	/**
	 * Clears the value of all {@link Uniform}s in this {@link ShaderProgram}
	 */
	public void clearUniforms() {
		for (Uniform uniform : this.uniformMap.values()) {
			uniform.clear();
		}
	}
	
	/**
	 * @param name
	 * @return if this {@link ShaderProgram} has a {@link Uniform} with the given name
	 */
	public boolean hasUniform(String name) {
		return this.uniformMap.containsKey(name);
	}

	/**
	 * Uploads all {@link Uniform}s in this {@link ShaderProgram}
	 */
	public void uploadUniforms() {
		this.clearUniforms();
		for (Uniform uniform : this.uploadUniforms) {
			uniform.upload();
		}
		this.clearUniforms();
	}

	/**
	 * 
	 */
	public abstract void bind();

	/**
	 * 
	 */
	public abstract void unbind();

}
