package gamelauncher.lwjgl.render.font;

import static org.lwjgl.opengles.GLES20.*;
import static org.lwjgl.opengles.GLES30.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Vector4f;

import gamelauncher.engine.render.model.ColorMultiplierModel;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.util.Color;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.render.states.GlStates;
import gamelauncher.lwjgl.render.texture.LWJGLTexture;

@SuppressWarnings("javadoc")
public class GlyphsMesh implements ColorMultiplierModel {

	private final Color textureAddColor = Color.white.withAlpha(0F);
	private final Vector4f vectorTextureAddColor = new Vector4f(textureAddColor.r, textureAddColor.g,
			textureAddColor.g, textureAddColor.a);

	private final int vao;
	private final int posbuffer;
	private final int texbuffer;
	private final int idxbuffer;
	private final int vertexCount;
	private final GlyphKey[] texMapIds;
	private final LWJGLGlyphProvider provider;
	private final LWJGLTexture texture;
	private final Vector4f color;

	public GlyphsMesh(LWJGLGlyphProvider provider, int[] idxA, float[] posA, float[] texA, GlyphKey[] texMapIds, LWJGLTexture texture,
			Color color) {
		this.provider = provider;
		this.color = new Vector4f(color.r, color.g, color.b, color.a);
		this.texture = texture;
		this.texMapIds = texMapIds;
		this.vertexCount = idxA.length;
		vao = glGenVertexArrays();
		GlStates.current().bindVertexArray(vao);
		idxbuffer = glGenBuffers();
		IntBuffer ibuffer = memAllocInt(idxA.length);
		ibuffer.put(idxA).flip();
		GlStates.current().bindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxbuffer);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, ibuffer, GL_STATIC_DRAW);
		memFree(ibuffer);

		posbuffer = glGenBuffers();
		FloatBuffer fbuffer = memAllocFloat(posA.length);
		fbuffer.put(posA).flip();
		GlStates.current().bindBuffer(GL_ARRAY_BUFFER, posbuffer);
		glBufferData(GL_ARRAY_BUFFER, fbuffer, GL_STATIC_DRAW);
		memFree(fbuffer);

		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

		texbuffer = glGenBuffers();
		fbuffer = memAllocFloat(texA.length);
		fbuffer.put(texA).flip();
		GlStates.current().bindBuffer(GL_ARRAY_BUFFER, texbuffer);
		glBufferData(GL_ARRAY_BUFFER, fbuffer, GL_STATIC_DRAW);
		memFree(fbuffer);

		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

	}

	@Override
	public Vector4f getColor() {
		return color;
	}

	@Override
	public void render(ShaderProgram program) throws GameException {
		GlStates.current().activeTexture(GL_TEXTURE0);
		texture.bind();

		program.utextureAddColor.set(vectorTextureAddColor);
		program.uploadUniforms();

		GlStates.current().bindVertexArray(vao);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);

		program.utextureAddColor.set(0, 0, 0, 0);
	}

	@Override
	public void cleanup() throws GameException {
		glDeleteBuffers(idxbuffer);
		glDeleteBuffers(posbuffer);
		glDeleteBuffers(texbuffer);
		glDeleteVertexArrays(vao);

		for (GlyphKey key : this.texMapIds) {
			provider.releaseGlyphKey(key);
		}
	}

	private class Bundle {
		private final float[] positions;
		private final float[] texCoords;
		private final int[] indices;

		public Bundle(float[] positions, float[] texCoords, int[] indices) {
			this.positions = positions;
			this.texCoords = texCoords;
			this.indices = indices;
		}
	}
}
