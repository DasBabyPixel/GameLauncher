package gamelauncher.gles.mesh;

import gamelauncher.gles.GLES;
import gamelauncher.gles.gl.GLES20;

/**
 * @author DasBabyPixel
 */
public class RectMesh extends Mesh {

    public RectMesh(GLES gles) {
        // @formatter:off
        super(gles, new float[] {
                0F, 0F, 0F,
                0F, 1F, 0F,
                1F, 1F, 0F,
                1F, 0F, 0F,
        }, new float[] {
                0, 0,
                0, 0,
                0, 0,
                0, 0,
        }, new int[] {
                0, 3, 2,
                0, 2, 1,
        }, GLES20.GL_TRIANGLES,false);
        // @formatter:on
    }
}
