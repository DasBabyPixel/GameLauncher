package gamelauncher.lwjgl.render.font;

import static org.lwjgl.opengles.GLES20.*;
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

	private final Vector4f vectorTextureAddColor = new Vector4f(textureAddColor.r, textureAddColor.g, textureAddColor.g,
			textureAddColor.a);

	private final int vao;

	private final int posbuffer;

	private final int texbuffer;

	private final int idxbuffer;

	private final int vertexCount;

	private final GlyphKey[] texMapIds;

	private final LWJGLGlyphProvider provider;

	private final LWJGLTexture texture;

	private final Vector4f color;

	public GlyphsMesh(LWJGLGlyphProvider provider, int[] idxA, float[] posA, float[] texA, GlyphKey[] texMapIds,
			LWJGLTexture texture, Color color) {
		this.provider = provider;
		this.color = new Vector4f(color.r, color.g, color.b, color.a);
		this.texture = texture;
		this.texMapIds = texMapIds;
		this.vertexCount = idxA.length;
		GlStates c = GlStates.current();
		vao = c.genVertexArrays();
		c.bindVertexArray(vao);
		idxbuffer = c.genBuffers();
		IntBuffer ibuffer = memAllocInt(idxA.length);
		ibuffer.put(idxA).flip();
		c.bindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxbuffer);
		c.bufferData(GL_ELEMENT_ARRAY_BUFFER, ibuffer, GL_STATIC_DRAW);
		memFree(ibuffer);

		posbuffer = c.genBuffers();
		FloatBuffer fbuffer = memAllocFloat(posA.length);
		fbuffer.put(posA).flip();
		c.bindBuffer(GL_ARRAY_BUFFER, posbuffer);
		c.bufferData(GL_ARRAY_BUFFER, fbuffer, GL_STATIC_DRAW);
		memFree(fbuffer);

		c.vertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

		texbuffer = c.genBuffers();
		fbuffer = memAllocFloat(texA.length);
		fbuffer.put(texA).flip();
		c.bindBuffer(GL_ARRAY_BUFFER, texbuffer);
		c.bufferData(GL_ARRAY_BUFFER, fbuffer, GL_STATIC_DRAW);
		memFree(fbuffer);

		c.vertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

	}

	@Override
	public Vector4f getColor() {
		return color;
	}

	@Override
	public void render(ShaderProgram program) throws GameException {
		GlStates c = GlStates.current();
		c.activeTexture(GL_TEXTURE0);
		c.bindTexture(GL_TEXTURE_2D, this.texture.getTextureId());

		program.utextureAddColor.set(vectorTextureAddColor);
		program.uploadUniforms();

		c.bindVertexArray(vao);
		c.enableVertexAttribArray(0);
		c.enableVertexAttribArray(1);
		c.drawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
		c.disableVertexAttribArray(0);
		c.disableVertexAttribArray(1);

		program.utextureAddColor.set(0, 0, 0, 0);
	}

	@Override
	public void cleanup() throws GameException {
		GlStates c = GlStates.current();
		c.deleteBuffers(idxbuffer);
		c.deleteBuffers(posbuffer);
		c.deleteBuffers(texbuffer);
		c.deleteVertexArrays(vao);

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
