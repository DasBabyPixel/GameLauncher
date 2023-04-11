package gamelauncher.gles.mesh;

import gamelauncher.gles.GLES;
import gamelauncher.gles.gl.GLES20;

/**
 * @author DasBabyPixel
 */
public class PlaneMesh extends Mesh {

    public PlaneMesh(GLES gles) {
        // @formatter:off
        super(gles, new float[] {
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
        }, GLES20.GL_TRIANGLES);
        // @formatter:on
    }

    @Override
    public boolean applyLighting() {
        return false;
    }

}
