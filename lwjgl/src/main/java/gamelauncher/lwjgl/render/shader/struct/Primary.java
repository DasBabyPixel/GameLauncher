package gamelauncher.lwjgl.render.shader.struct;

import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.render.shader.Uniform;
import gamelauncher.lwjgl.render.shader.BasicUniform;
import gamelauncher.lwjgl.render.shader.BasicUniform.Type;
import gamelauncher.lwjgl.render.shader.LWJGLShaderProgram;
import gamelauncher.lwjgl.render.states.GlStates;

/**
 * @author DasBabyPixel
 *
 */
public class Primary implements Struct {

	private final Type type;

	/**
	 * @param type
	 */
	public Primary(Type type) {
		this.type = type;
	}

	/**
	 * @return the type of this struct
	 */
	public Type getType() {
		return type;
	}

	@Override
	public String name() {
		return type.getGlName();
	}

	@Override
	public Uniform createUniform(ShaderProgram program, String name) {
		return new BasicUniform(name,
				GlStates.current().getUniformLocation(((LWJGLShaderProgram) program).getProgramId(), name), type);
	}

}
