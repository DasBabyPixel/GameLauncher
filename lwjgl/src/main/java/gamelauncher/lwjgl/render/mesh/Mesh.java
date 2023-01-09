package gamelauncher.lwjgl.render.mesh;

import gamelauncher.engine.render.shader.ProgramObject;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.render.states.GlStates;
import gamelauncher.lwjgl.render.texture.LWJGLTexture;
import org.joml.Vector4f;
import org.lwjgl.opengles.GLES20;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Mesh extends AbstractGameResource {

	private final List<Integer> vbos = new ArrayList<>();
	private final int vaoId;
	private final int vertexCount;
	private final Material material = new Material();
	private final int vaoSize;
	private final int renderType;

	public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices,
			int renderType) {
		this.vertexCount = indices.length;
		this.renderType = renderType;

		GlStates cur = GlStates.current();

		this.vaoId = cur.genVertexArrays();
		cur.bindVertexArray(this.vaoId);

		this.createBuffer(indices);

		this.createBuffer(positions);
		cur.vertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, 0);

		this.createBuffer(textCoords);
		cur.vertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 0, 0);

		this.createBuffer(normals);
		cur.vertexAttribPointer(2, 3, GLES20.GL_FLOAT, false, 0, 0);

		this.vaoSize = 3;

		cur.bindVertexArray(0);
	}

	public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices) {
		this(positions, textCoords, normals, indices, GLES20.GL_TRIANGLES);
	}

	public Material getMaterial() {
		return this.material;
	}

	private void createBuffer(int[] array) {
		this.createBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER);
		this.upload(array);
	}

	private void createBuffer(float[] array) {
		this.createBuffer(GLES20.GL_ARRAY_BUFFER);
		this.upload(array);
	}

	private void createBuffer(int target) {
		int id = GlStates.current().genBuffers();
		GlStates.current().bindBuffer(target, id);
		this.vbos.add(id);
	}

	private void upload(int[] array) {
		IntBuffer buffer = MemoryUtil.memAllocInt(array.length);
		buffer.put(array).flip();
		GlStates.current()
				.bufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffer, GLES20.GL_STATIC_DRAW);
		MemoryUtil.memFree(buffer);
	}

	private void upload(float[] array) {
		FloatBuffer buffer = MemoryUtil.memAllocFloat(array.length);
		buffer.put(array).flip();
		GlStates.current().bufferData(GLES20.GL_ARRAY_BUFFER, buffer, GLES20.GL_STATIC_DRAW);
		MemoryUtil.memFree(buffer);
	}

	public void render() {
		if (this.material.texture != null) {
			GlStates.current().activeTexture(GLES20.GL_TEXTURE0);
			GlStates.current()
					.bindTexture(GLES20.GL_TEXTURE_2D, this.material.texture.getTextureId());
			//			glBindTexture(GL_TEXTURE_2D, material.texture.getTextureId());
		}

		GlStates cur = GlStates.current();
		cur.bindVertexArray(this.getVaoId());
		for (int i = 0; i < this.vaoSize; i++) {
			cur.enableVertexAttribArray(i);
		}
		cur.drawElements(this.renderType, this.getVertexCount(), GLES20.GL_UNSIGNED_INT, 0);

		for (int i = 0; i < this.vaoSize; i++) {
			cur.disableVertexAttribArray(i);
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
		// Delete the VBOs
		for (int vbo : this.vbos) {
			GlStates.current().deleteBuffers(vbo);
		}
		if (material.texture != null) {
			material.texture.cleanup();
		}
		this.vbos.clear();
		// Delete the VAO
		GlStates.current().deleteVertexArrays(this.vaoId);
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
		public LWJGLTexture texture;

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

		public Material(LWJGLTexture texture) {
			this(Material.DEFAULT_COLOUR, Material.DEFAULT_COLOUR, Material.DEFAULT_COLOUR, texture,
					0);
		}

		public Material(LWJGLTexture texture, float reflectance) {
			this(Material.DEFAULT_COLOUR, Material.DEFAULT_COLOUR, Material.DEFAULT_COLOUR, texture,
					reflectance);
		}

		public Material(Vector4f ambientColour, Vector4f diffuseColour, Vector4f specularColour,
				LWJGLTexture texture, float reflectance) {
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
