package gamelauncher.lwjgl.render.shader.struct;

import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.render.shader.Uniform;
import gamelauncher.lwjgl.render.shader.BasicUniform.Type;

/**
 * @author DasBabyPixel
 */
public interface Struct {

	Struct sint = new Primary(Type.INT1);
	Struct sfloat = new Primary(Type.FLOAT1);
	Struct svec2 = new Primary(Type.FLOAT2);
	Struct svec3 = new Primary(Type.FLOAT3);
	Struct svec4 = new Primary(Type.FLOAT4);
	Struct smat4 = new Primary(Type.MAT4);
	Struct ssampler2D = new Primary(Type.SAMPLER2D);

	Struct[] primitives = new Struct[] {
			Struct.sint, Struct.sfloat, Struct.svec2, Struct.svec3, Struct.svec4, Struct.smat4, Struct.ssampler2D
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
