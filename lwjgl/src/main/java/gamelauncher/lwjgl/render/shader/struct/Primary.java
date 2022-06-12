package gamelauncher.lwjgl.render.shader.struct;

import static org.lwjgl.opengl.GL20.*;

import gamelauncher.lwjgl.render.shader.BasicUniform;
import gamelauncher.lwjgl.render.shader.BasicUniform.Type;
import gamelauncher.lwjgl.render.shader.ShaderProgram;
import gamelauncher.lwjgl.render.shader.Uniform;

public class Primary implements Struct {

	private final Type type;

	public Primary(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}

	@Override
	public String name() {
		return type.getGlName();
	}

	@Override
	public Uniform createUniform(ShaderProgram program, String name) {
		return new BasicUniform(name, glGetUniformLocation(program.getProgramId(), name), type);
	}
}
