package gamelauncher.gles.mesh;

import gamelauncher.engine.render.shader.ProgramObject;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.gles.GLES;
import gamelauncher.gles.gl.GLES20;
import gamelauncher.gles.texture.GLESTexture;
import java8.util.concurrent.CompletableFuture;
import org.joml.Vector4f;

public class Mesh extends AbstractGameResource {

    private final Material material = new Material();
    private final MeshGLData glData;
    private final boolean applyLighting;

    public Mesh(GLES gles, float[] positions, float[] textCoords, float[] normals, int[] indices, int renderType) {
        this(gles, positions, textCoords, normals, indices, renderType, true);
    }

    public Mesh(GLES gles, float[] positions, float[] textCoords, float[] normals, int[] indices) {
        this(gles, positions, textCoords, normals, indices, GLES20.GL_TRIANGLES);
    }

    public Mesh(GLES gles, float[] positions, float[] textCoords, float[] normals, int[] indices, int renderType, boolean applyLighting) {
        this.applyLighting = applyLighting;
        MeshGLDataFactory factory = gles.launcher().serviceProvider().service(MeshGLDataFactory.class);
        glData = factory.createMeshGLData(positions, textCoords, normals, indices, renderType);
    }

    public Mesh(GLES gles, float[] vertices, float[] texCoords, int[] indices, int renderType, boolean applyLighting) {
        this.applyLighting = applyLighting;
        MeshGLDataFactory factory = gles.launcher().serviceProvider().service(MeshGLDataFactory.class);
        glData = factory.createMeshGLData(vertices, texCoords, indices, renderType);
    }

    public Material material() {
        return this.material;
    }

    public MeshGLData glData() {
        return glData;
    }
//    public void render(GLES30 gl) {
//        if (this.material.texture != null) {
//            gl.glActiveTexture(GLES20.GL_TEXTURE0);
//            gl.glBindTexture(GLES20.GL_TEXTURE_2D, this.material.texture.getTextureId());
//            //			glBindTexture(GL_TEXTURE_2D, material.texture.getTextureId());
//        }
//
//        gl.glBindVertexArray(this.getVaoId());
//        for (int i = 0; i < this.vaoSize; i++) {
//            gl.glEnableVertexAttribArray(i);
//        }
//        gl.glDrawElements(this.renderType, this.getVertexCount(), GLES20.GL_UNSIGNED_INT, 0);
//
//        for (int i = 0; i < this.vaoSize; i++) {
//            gl.glDisableVertexAttribArray(i);
//        }
//    }

    public boolean applyLighting() {
        return applyLighting;
    }

    @Override public CompletableFuture<Void> cleanup0() throws GameException {
        return glData.cleanup();
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
            this(Material.DEFAULT_COLOUR, Material.DEFAULT_COLOUR, Material.DEFAULT_COLOUR, texture, 0);
        }

        public Material(GLESTexture texture, float reflectance) {
            this(Material.DEFAULT_COLOUR, Material.DEFAULT_COLOUR, Material.DEFAULT_COLOUR, texture, reflectance);
        }

        public Material(Vector4f ambientColour, Vector4f diffuseColour, Vector4f specularColour, GLESTexture texture, float reflectance) {
            this.ambientColour = ambientColour;
            this.diffuseColour = diffuseColour;
            this.specularColour = specularColour;
            this.texture = texture;
            this.reflectance = reflectance;
        }

        @Override public void upload(ShaderProgram program, String name) {
            program.uniformMap.get(name + ".ambient").set(this.ambientColour).upload();
            program.uniformMap.get(name + ".diffuse").set(this.diffuseColour).upload();
            program.uniformMap.get(name + ".specular").set(this.specularColour).upload();
            program.uniformMap.get(name + ".reflectance").set(this.reflectance).upload();
            program.uniformMap.get(name + ".hasTexture").set(this.texture == null ? 0 : 1).upload();
        }

    }
}
