package gamelauncher.lwjgl.render.mesh;

import static org.lwjgl.opengles.GLES20.*;

/**
 * @author DasBabyPixel
 */
public class PlaneMesh extends Mesh {

	/**
	 */
	public PlaneMesh() {
		// @formatter:off
		super(new float[] {
				-0.5F, -0.5F,  0.0F,
				-0.5F,  0.5F,  0.0F,
				 0.5F,  0.5F,  0.0F,
				 0.5F, -0.5F,  0.0F,
		}, new float[] {
				0, 0, 
				0, 0, 
				0, 0, 
				0, 0,
		}, new float[] {
				0, 0, 0,
				0, 0, 0,
				0, 0, 0,
				0, 0, 0,
		}, new int[] {
				0, 3, 2, 
				0, 2, 1,
		}, GL_TRIANGLES);
		// @formatter:on
	}
	
	@Override
	public boolean applyLighting() {
		return false;
	}

}
