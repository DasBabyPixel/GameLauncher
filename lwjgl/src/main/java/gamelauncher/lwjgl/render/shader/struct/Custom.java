package gamelauncher.lwjgl.render.shader.struct;

import java.util.HashMap;
import java.util.Map;

import gamelauncher.lwjgl.render.shader.ObjectUniform;
import gamelauncher.lwjgl.render.shader.ShaderProgram;
import gamelauncher.lwjgl.render.shader.Uniform;

public class Custom implements Struct {

	private final String name;
	private final Map<String, Struct> variables;

	public Custom(String name) {
		this.name = name;
		this.variables = new HashMap<>();
	}

	public Map<String, Struct> getVariables() {
		return variables;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public Uniform createUniform(ShaderProgram program, String name) {
		return new ObjectUniform(program, name);
	}
}
