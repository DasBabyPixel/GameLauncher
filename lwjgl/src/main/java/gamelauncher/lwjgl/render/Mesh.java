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

import gamelauncher.engine.render.Model;

public class Mesh {

	private final List<Integer> vbos = new ArrayList<>();
	private final int vaoId;
	private final int vertexCount;
	private final LWJGLTexture texture;

	public Mesh(float[] positions, float[] textCoords, int[] indices, LWJGLTexture texture) {
		this.texture = texture;
		vertexCount = indices.length;

		vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);

		createBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_STATIC_DRAW, indices);
		
		createBuffer(GL_ARRAY_BUFFER, GL_STATIC_DRAW, positions);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

		createBuffer(GL_ARRAY_BUFFER, GL_STATIC_DRAW, textCoords);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

		glBindVertexArray(0);
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
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texture.getTextureId());
		
		glBindVertexArray(getVaoId());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(0);
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
}
