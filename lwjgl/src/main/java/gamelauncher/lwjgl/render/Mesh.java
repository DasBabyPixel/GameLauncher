package gamelauncher.lwjgl.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {
	private final int vaoId;
	private final int vboId;
	private final int colorVboId;
	private final int idxVboId;
	private final int vertexCount;

	public Mesh(float[] positions, float[] colors, int[] indices) {
		vertexCount = indices.length;

		vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);

		idxVboId = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboId);
		IntBuffer indicesBuffer = memAllocInt(indices.length);
		indicesBuffer.put(indices).flip();
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
		memFree(indicesBuffer);

		colorVboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, colorVboId);
		FloatBuffer colorBuffer = memAllocFloat(colors.length);
		colorBuffer.put(colors).flip();
		glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
		memFree(colorBuffer);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

		vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		FloatBuffer verticesBuffer = memAllocFloat(positions.length);
		verticesBuffer.put(positions).flip();
		glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
		memFree(verticesBuffer);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
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
		glDeleteBuffers(vboId);
		glDeleteBuffers(colorVboId);
		glDeleteBuffers(idxVboId);
		// Delete the VAO
		glDeleteVertexArrays(vaoId);
	}
}
