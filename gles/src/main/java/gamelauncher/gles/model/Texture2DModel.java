package gamelauncher.gles.model;

import gamelauncher.engine.render.model.Model;
import gamelauncher.gles.gl.GLES20;
import gamelauncher.gles.mesh.Mesh;
import gamelauncher.gles.texture.GLESTexture;

public class Texture2DModel extends MeshModel implements Model {
    //    private final int vao;
//    private final int posbuffer;
//    private final int texbuffer;
//    private final int idxbuffer;
//    private final int vertexCount;

    public Texture2DModel(GLESTexture texture) {
        this(texture, 0F, 1F, 1F, 0F);
    }

    public Texture2DModel(GLESTexture texture, float textureLeft, float textureTop, float textureRight, float textureBottom) {
        super(new Mesh(texture.gles(), new float[]{-0.5F, -0.5F, 0F, -0.5F, 0.5F, 0F, 0.5F, 0.5F, 0F, 0.5F, -0.5F, 0F}, new float[]{textureLeft, 1 - textureBottom, textureLeft, 1 - textureTop, textureRight, 1 - textureTop, textureRight, 1 - textureBottom}, new int[]{0, 2, 1, 0, 3, 2}, GLES20.GL_TRIANGLES, false));
        mesh.material().texture = texture;
//        this.texture = texture;
//        textureTop = 1 - textureTop;
//        textureBottom = 1 - textureBottom;
//        GLES30 c = StateRegistry.currentGl();
//        //@formatter:off
//        this.vao = c.glGenVertexArrays();
//        c.glBindVertexArray(this.vao);
//        this.idxbuffer = c.glGenBuffers();
//        IntBuffer ibuffer = texture.memoryManagement().allocDirectInt(6);
//        ibuffer.put(new int[] {
//                0, 2, 1,
//                0, 3, 2
//        }).flip();
//        this.vertexCount = 6;
//        c.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, this.idxbuffer);
//        c.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibuffer, GLES20.GL_STATIC_DRAW);
//        texture.memoryManagement().free(ibuffer);
//        this.posbuffer = c.glGenBuffers();
//        FloatBuffer fbuffer = texture.memoryManagement().allocDirectFloat(12);
//        fbuffer.put(new float[] {
//                -0.5F, -0.5F, 0F,
//                -0.5F, 0.5F, 0F,
//                0.5F, 0.5F, 0F,
//                0.5F, -0.5F, 0F
//        }).flip();
//        c.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.posbuffer);
//        c.glBufferData(GLES20.GL_ARRAY_BUFFER, fbuffer, GLES20.GL_STATIC_DRAW);
//        texture.memoryManagement().free(fbuffer);
//        c.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, 0);
//
//        this.texbuffer = c.glGenBuffers();
//        fbuffer = texture.memoryManagement().allocDirectFloat(8);
//        fbuffer.put(new float[] {
//                textureLeft, textureBottom,
//                textureLeft, textureTop,
//                textureRight, textureTop,
//                textureRight, textureBottom
//        }).flip();
//        c.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.texbuffer);
//        c.glBufferData(GLES20.GL_ARRAY_BUFFER, fbuffer, GLES20.GL_STATIC_DRAW);
//        texture.memoryManagement().free(fbuffer);
//        c.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 0, 0);
//        //@formatter:on
    }

    public GLESTexture texture() {
        return this.mesh().material().texture;
    }
}
