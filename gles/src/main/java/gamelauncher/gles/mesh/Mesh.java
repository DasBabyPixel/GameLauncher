package gamelauncher.gles.mesh;

import gamelauncher.engine.render.shader.ProgramObject;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.gles.GLES;
import gamelauncher.gles.gl.GLES20;
import gamelauncher.gles.gl.GLES30;
import gamelauncher.gles.states.StateRegistry;
import gamelauncher.gles.texture.GLESTexture;
import org.joml.Vector4f;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Mesh extends AbstractGameResource {

    private final GLES gles;
    private final List<Integer> vbos = new ArrayList<>();
    private final int vaoId;
    private final int vertexCount;
    private final Material material = new Material();
    private final int vaoSize;
    private final int renderType;

    public Mesh(GLES gles, float[] positions, float[] textCoords, float[] normals, int[] indices,
                int renderType) {
        this.gles = gles;
        this.vertexCount = indices.length;
        this.renderType = renderType;

        GLES30 cur = StateRegistry.currentGl();

        this.vaoId = cur.glGenVertexArrays();
        cur.glBindVertexArray(this.vaoId);

        this.createBuffer(cur, indices);

        this.createBuffer(cur, positions);
        cur.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, 0);

        this.createBuffer(cur, textCoords);
        cur.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 0, 0);

        this.createBuffer(cur, normals);
        cur.glVertexAttribPointer(2, 3, GLES20.GL_FLOAT, false, 0, 0);

        this.vaoSize = 3;

        cur.glBindVertexArray(0);
    }

    public Mesh(GLES gles, float[] positions, float[] textCoords, float[] normals, int[] indices) {
        this(gles, positions, textCoords, normals, indices, GLES20.GL_TRIANGLES);
    }

    public Material material() {
        return this.material;
    }

    private void createBuffer(GLES20 gl, int[] array) {
        this.createBuffer(gl, GLES20.GL_ELEMENT_ARRAY_BUFFER);
        this.upload(gl, array);
    }

    private void createBuffer(GLES20 gl, float[] array) {
        this.createBuffer(gl, GLES20.GL_ARRAY_BUFFER);
        this.upload(gl, array);
    }

    private void createBuffer(GLES20 gl, int target) {
        int id = gl.glGenBuffers();
        gl.glBindBuffer(target, id);
        this.vbos.add(id);
    }

    private void upload(GLES20 gl, int[] array) {
        IntBuffer buffer = gles.memoryManagement().allocDirectInt(array.length);
        buffer.put(array).flip();
        gl.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffer, GLES20.GL_STATIC_DRAW);
        gles.memoryManagement().free(buffer);
    }

    private void upload(GLES20 gl, float[] array) {
        FloatBuffer buffer = gles.memoryManagement().allocDirectFloat(array.length);
        buffer.put(array).flip();
        gl.glBufferData(GLES20.GL_ARRAY_BUFFER, buffer, GLES20.GL_STATIC_DRAW);
        gles.memoryManagement().free(buffer);
    }

    public void render(GLES30 gl) {
        if (this.material.texture != null) {
            gl.glActiveTexture(GLES20.GL_TEXTURE0);
            gl.glBindTexture(GLES20.GL_TEXTURE_2D, this.material.texture.getTextureId());
            //			glBindTexture(GL_TEXTURE_2D, material.texture.getTextureId());
        }

        gl.glBindVertexArray(this.getVaoId());
        for (int i = 0; i < this.vaoSize; i++) {
            gl.glEnableVertexAttribArray(i);
        }
        gl.glDrawElements(this.renderType, this.getVertexCount(), GLES20.GL_UNSIGNED_INT, 0);

        for (int i = 0; i < this.vaoSize; i++) {
            gl.glDisableVertexAttribArray(i);
        }
    }

    public int getVaoId() {
        return this.vaoId;
    }

    public int getVertexCount() {
        return this.vertexCount;
    }

    @Override
    public void cleanup0() throws GameException {
        GLES30 gl = StateRegistry.currentGl();
        int[] vbos = new int[this.vbos.size()];
        // Delete the VBOs
        int i = 0;
        for (int vbo : this.vbos) {
            vbos[i++] = vbo;
        }
        gl.glDeleteBuffers(vbos.length, vbos, 0);
        if (material.texture != null) {
            material.texture.cleanup();
        }
        this.vbos.clear();
        // Delete the VAO
        gl.glDeleteVertexArrays(1, new int[]{this.vaoId}, 0);
    }

    public boolean applyLighting() {
        return true;
    }

    public static class Material implements ProgramObject {
        private static final Vector4f DEFAULT_COLOUR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        public Vector4f ambientColour;
        public Vector4f diffuseColour;
        public Vector4f specularColour;
        public float reflectance;
        public GLESTexture texture;

        public Material() {
            this.ambientColour = Material.DEFAULT_COLOUR;
            this.diffuseColour = Material.DEFAULT_COLOUR;
            this.specularColour = Material.DEFAULT_COLOUR;
            this.texture = null;
            this.reflectance = 0;
        }

        public Material(Vector4f colour, float reflectance) {
            this(colour, colour, colour, null, reflectance);
        }

        public Material(GLESTexture texture) {
            this(Material.DEFAULT_COLOUR, Material.DEFAULT_COLOUR, Material.DEFAULT_COLOUR, texture,
                    0);
        }

        public Material(GLESTexture texture, float reflectance) {
            this(Material.DEFAULT_COLOUR, Material.DEFAULT_COLOUR, Material.DEFAULT_COLOUR, texture,
                    reflectance);
        }

        public Material(Vector4f ambientColour, Vector4f diffuseColour, Vector4f specularColour,
                        GLESTexture texture, float reflectance) {
            this.ambientColour = ambientColour;
            this.diffuseColour = diffuseColour;
            this.specularColour = specularColour;
            this.texture = texture;
            this.reflectance = reflectance;
        }

        @Override
        public void upload(ShaderProgram program, String name) {
            program.uniformMap.get(name + ".ambient").set(this.ambientColour).upload();
            program.uniformMap.get(name + ".diffuse").set(this.diffuseColour).upload();
            program.uniformMap.get(name + ".specular").set(this.specularColour).upload();
            program.uniformMap.get(name + ".reflectance").set(this.reflectance).upload();
            program.uniformMap.get(name + ".hasTexture").set(this.texture == null ? 0 : 1).upload();
        }

    }
}
