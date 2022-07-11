package gamelauncher.lwjgl.render.model;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.render.GlStates;
import gamelauncher.lwjgl.render.texture.LWJGLTexture;

/**
 * @author DasBabyPixel
 */
public class Texture2DModel implements Model {

	private final int vao;
	private final int posbuffer;
	private final int texbuffer;
	private final int idxbuffer;
	private final int vertexCount;
	private final LWJGLTexture texture;

	/**
	 * @param texture
	 */
	public Texture2DModel(LWJGLTexture texture) {
		this.texture = texture;
		//@formatter:off
		vao = glGenVertexArrays();
		GlStates.bindVertexArray(vao);
		idxbuffer = glGenBuffers();
		IntBuffer ibuffer = memAllocInt(6);
		ibuffer.put(new int[] {
				0, 2, 1,
				0, 3, 2
		}).flip();
		vertexCount = 6;
		GlStates.bindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxbuffer);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, ibuffer, GL_STATIC_DRAW);
		memFree(ibuffer);
		posbuffer = glGenBuffers();
		FloatBuffer fbuffer = memAllocFloat(12);
		fbuffer.put(new float[] {
				-0.5F, -0.5F, 0F,
				-0.5F, 0.5F, 0F,
				0.5F, 0.5F, 0F,
				0.5F, -0.5F, 0F
		}).flip();
		GlStates.bindBuffer(GL_ARRAY_BUFFER, posbuffer);
		glBufferData(GL_ARRAY_BUFFER, fbuffer, GL_STATIC_DRAW);
		memFree(fbuffer);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		
		texbuffer = glGenBuffers();
		fbuffer = memAllocFloat(8);
		fbuffer.put(new float[] {
				0, 1,
				0, 0,
				1, 0,
				1, 1
		}).flip();
		GlStates.bindBuffer(GL_ARRAY_BUFFER, texbuffer);
		glBufferData(GL_ARRAY_BUFFER, fbuffer, GL_STATIC_DRAW);
		memFree(fbuffer);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		//@formatter:on
	}

	@Override
	public void render(ShaderProgram program) throws GameException {
		program.uapplyLighting.set(0).upload();
		GlStates.activeTexture(GL_TEXTURE0);
		texture.bind();
		GlStates.bindVertexArray(vao);

		program.uploadUniforms();
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glDrawElements(GL_TRIANGLES, this.vertexCount, GL_UNSIGNED_INT, 0);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
	}

	@Override
	public void cleanup() throws GameException {
		glDeleteBuffers(idxbuffer);
		glDeleteBuffers(posbuffer);
		glDeleteBuffers(texbuffer);
		glDeleteVertexArrays(vao);
	}
}
