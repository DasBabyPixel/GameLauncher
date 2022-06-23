package gamelauncher.engine.render.shader;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.util.GameResource;

public abstract class ShaderProgram implements GameResource {

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

	public ShaderProgram(GameLauncher launcher) {
		this.launcher = launcher;
		this.uniformMap = new HashMap<>();
		this.uploadUniforms = new HashSet<>();
	}

	public GameLauncher getLauncher() {
		return launcher;
	}

	public void clearUniforms() {
		for (Uniform uniform : uniformMap.values()) {
			uniform.clear();
		}
	}
	
	public boolean hasUniform(String name) {
		return uniformMap.containsKey(name);
	}

	public void uploadUniforms() {
		for (Uniform uniform : uploadUniforms) {
			uniform.upload();
		}
	}

	public abstract void bind();

	public abstract void unbind();

}
