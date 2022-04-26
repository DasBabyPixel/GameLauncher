package gamelauncher.lwjgl.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector4f;

import gamelauncher.engine.render.Model;
import gamelauncher.lwjgl.render.light.Material;

public class Mesh {

	private static final Vector4f emptyColor = new Vector4f(1, 1, 1, 1);

	private final List<Integer> vbos = new ArrayList<>();
	private final int vaoId;
	private final int vertexCount;
//	private LWJGLTexture texture;
//	private Vector4f color = emptyColor;
	private Material material;
	private final int vaoSize;

	public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices) {
		vertexCount = indices.length;

		vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);

		createBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW, indices);

		createBuffer(GL_ARRAY_BUFFER, GL_STATIC_DRAW, positions);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

		createBuffer(GL_ARRAY_BUFFER, GL_STATIC_DRAW, textCoords);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

		createBuffer(GL_ARRAY_BUFFER, GL_STATIC_DRAW, normals);
		glVertexAttribPointer(2, 3, GL_FLOAT_32_UNSIGNED_INT_24_8_REV, false, 0, 0);

		vaoSize = 3;

		glBindVertexArray(0);
	}

//	public Vector4f getColor() {
//		return color;
//	}
//
//	public LWJGLTexture getTexture() {
//		return texture;
//	}
//
//	public void setColor(Vector4f color) {
//		this.color = color == null ? emptyColor : color;
//	}
//
//	public void setTexture(LWJGLTexture texture) {
//		this.texture = texture;
//	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	private int createBuffer(int target, int usage, int[] array) {
		int id = createBuffer(target);
		upload(target, usage, array);
		return id;
	}

	private int createBuffer(int target, int usage, float[] array) {
		int id = createBuffer(target);
		upload(target, usage, array);
		return id;
	}

	private int createBuffer(int target) {
		int id = glGenBuffers();
		glBindBuffer(target, id);
		vbos.add(id);
		return id;
	}

	private void upload(int target, int usage, int[] array) {
		IntBuffer buffer = memAllocInt(array.length);
		buffer.put(array).flip();
		glBufferData(target, buffer, usage);
		memFree(buffer);
	}

	private void upload(int target, int usage, float[] array) {
		FloatBuffer buffer = memAllocFloat(array.length);
		buffer.put(array).flip();
		glBufferData(target, buffer, usage);
		memFree(buffer);
	}

	public void render() {
		if (material.texture != null) {
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, material.texture.getTextureId());
		}

		glBindVertexArray(getVaoId());
		for (int i = 0; i < vaoSize; i++) {
			glEnableVertexAttribArray(i);
		}
		glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
		for (int i = 0; i < vaoSize; i++) {
			glDisableVertexAttribArray(i);
		}
		glBindVertexArray(0);
	}

	public int getVaoId() {
		return vaoId;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public void cleanup() {
		// Delete the VBOs
		for (int vbo : vbos) {
			glDeleteBuffers(vbo);
		}
		vbos.clear();
		// Delete the VAO
		glDeleteVertexArrays(vaoId);
	}

	public static class MeshModel implements Model {
		public final Mesh mesh;

		public MeshModel(Mesh mesh) {
			this.mesh = mesh;
		}

		@Override
		public void cleanup() {
			mesh.cleanup();
		}
	}

//	public boolean hasTexture() {
//		return texture != null;
//	}
}
