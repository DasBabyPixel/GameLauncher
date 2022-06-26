package gamelauncher.lwjgl.render.shader.struct;

import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.render.shader.Uniform;
import gamelauncher.lwjgl.render.shader.BasicUniform.Type;

/**
 * @author DasBabyPixel
 */
public interface Struct {

	@SuppressWarnings("javadoc")
	Struct sint = new Primary(Type.INT1);
	@SuppressWarnings("javadoc")
	Struct sfloat = new Primary(Type.FLOAT1);
	@SuppressWarnings("javadoc")
	Struct svec2 = new Primary(Type.FLOAT2);
	@SuppressWarnings("javadoc")
	Struct svec3 = new Primary(Type.FLOAT3);
	@SuppressWarnings("javadoc")
	Struct svec4 = new Primary(Type.FLOAT4);
	@SuppressWarnings("javadoc")
	Struct smat4 = new Primary(Type.MAT4);
	@SuppressWarnings("javadoc")
	Struct ssampler2D = new Primary(Type.SAMPLER2D);

	@SuppressWarnings("javadoc")
	Struct[] primitives = new Struct[] {
			sint, sfloat, svec2, svec3, svec4, smat4, ssampler2D
	};

	/**
	 * @return the name of this struct
	 */
	String name();

	/**
	 * Creates a new {@link Uniform} with the {@link ShaderProgram} and name
	 * 
	 * @param program
	 * @param name
	 * @return the created uniform
	 */
	Uniform createUniform(ShaderProgram program, String name);

}
