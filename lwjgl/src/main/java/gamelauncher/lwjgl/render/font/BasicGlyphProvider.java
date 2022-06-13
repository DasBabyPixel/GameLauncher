package gamelauncher.lwjgl.render.font;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.awt.Rectangle;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.stb.STBTTFontinfo;

import gamelauncher.engine.GameException;
import gamelauncher.engine.render.Model;
import gamelauncher.engine.render.font.Font;
import gamelauncher.engine.render.font.GlyphProvider;
import gamelauncher.engine.util.Color;
import gamelauncher.lwjgl.render.GlStates;
import gamelauncher.lwjgl.render.model.ColorMultiplierModel;
import gamelauncher.lwjgl.render.model.MeshLikeModel;
import gamelauncher.lwjgl.render.shader.ShaderProgram;

public class BasicGlyphProvider implements GlyphProvider {

	private final Map<GlyphKey, GlyphEntry> entries = new ConcurrentHashMap<>();
	private final DynamicSizeTextureAtlasArray textures;

	public BasicGlyphProvider() {
		textures = new DynamicSizeTextureAtlasArray();
	}

	@Override
	public void cleanup() throws GameException {
		textures.cleanup();
	}

	@Override
	public Model loadStaticModel(Font font, String text, int pixelHeight) throws GameException {
		STBTTFontinfo finfo = STBTTFontinfo.malloc();
		if (!stbtt_InitFont(finfo, font.data())) {
			throw new GameException("Failed to initialize font");
		}
		float scale = stbtt_ScaleForPixelHeight(finfo, pixelHeight);
		Map<DynamicSizeTextureAtlas, List<GlyphEntry>> entries = new HashMap<>();
		char[] ar = text.toCharArray();
		for (int i = 0; i < ar.length; i++) {
			char ch = ar[i];
			GlyphEntry entry = requireGlyphEntry(font, finfo, ch, pixelHeight, scale);
			GlyphKey key = new GlyphKey(scale, ch);
			int id = getId(key);
			DynamicSizeTextureAtlas at = textures.map.get(id);
			List<GlyphEntry> e = entries.get(at);
			if (e == null) {
				e = new ArrayList<>();
				entries.put(at, e);
			}
			e.add(entry);
		}
		Set<GlyphsMesh> meshes = new HashSet<>();

		float xpos = 0;
		float z = 0;
//		float zincrease = -0.1F;
		for (Entry<DynamicSizeTextureAtlas, List<GlyphEntry>> entry : entries.entrySet()) {

			List<IndexGroup> groups = new ArrayList<>();
			List<Vector3f> v = new ArrayList<>();
			List<Vector2f> vt = new ArrayList<>();
			List<Integer> indexList = new ArrayList<>();
			List<GlyphKey> mapIdList = new ArrayList<>();

			int i = 0;
			int vsize = entry.getValue().size();
			for (GlyphEntry e : entry.getValue()) {
				int id = getId(e.key);
				DynamicSizeTextureAtlas at = textures.map.get(id);
				Rectangle bd = at.glyphBounds.get(id);
				mapIdList.add(e.key);

				float tl = (float) bd.x / (float) at.size;
				float tt = (float) bd.y / (float) at.size;
				float tr = (float) (bd.x + bd.width) / (float) at.size;
				float tb = (float) (bd.y + bd.height) / (float) at.size;

				float ypos = 0;

				float pb = ypos - e.glyphData.bearingY;
				float pt = pb + e.glyphData.height;
				float pl = xpos + e.glyphData.bearingX;
				float pr = pl + e.glyphData.width;

				Vector3f vtl = new Vector3f(pl, pt, z);
				Vector3f vtr = new Vector3f(pr, pt, z);
				Vector3f vbl = new Vector3f(pl, pb, z);
				Vector3f vbr = new Vector3f(pr, pb, z);

//				z += zincrease;

				System.out.println(Character.getName(e.key.codepoint));

				Vector2f ttl = new Vector2f(tl, tt);
				Vector2f ttr = new Vector2f(tr, tt);
				Vector2f tbl = new Vector2f(tl, tb);
				Vector2f tbr = new Vector2f(tr, tb);

				int itl = addvtx(v, vt, groups, vtl, ttl);
				int itr = addvtx(v, vt, groups, vtr, ttr);
				int ibl = addvtx(v, vt, groups, vbl, tbl);
				int ibr = addvtx(v, vt, groups, vbr, tbr);

				indexList.add(ibl);
				indexList.add(itr);
				indexList.add(itl);
				indexList.add(ibl);
				indexList.add(ibr);
				indexList.add(itr);

				xpos = xpos + e.glyphData.advance;
				if (i != vsize - 1) {
					xpos = xpos
							+ stbtt_GetGlyphKernAdvance(finfo, e.glyphIndex, entry.getValue().get(i + 1).glyphIndex);
				}
				i++;
			}
			float[] vertices = new float[groups.size() * 3];
			float[] texCoord = new float[groups.size() * 2];
			int[] indices = indexList.stream().mapToInt(in -> in).toArray();

			i = 0;
			for (IndexGroup group : groups) {
				Vector3f v3f = v.get(group.pos);
				Vector2f v2f = vt.get(group.tex);
				vertices[i * 3 + 0] = v3f.x;
				vertices[i * 3 + 1] = v3f.y;
				vertices[i * 3 + 2] = v3f.z;
				texCoord[i * 2 + 0] = v2f.x;
				texCoord[i * 2 + 1] = v2f.y;
				i++;
			}
			GlyphsMesh gmesh = new GlyphsMesh(indices, vertices, texCoord,
					mapIdList.toArray(new GlyphKey[mapIdList.size()]), entry.getKey(), Color.white);
			meshes.add(gmesh);
		}
		GlyphsModel gmodel = new GlyphsModel(meshes.toArray(new GlyphsMesh[meshes.size()]));
		return gmodel;
	}

	private int addvtx(List<Vector3f> v, List<Vector2f> vt, List<IndexGroup> groups, Vector3f pos, Vector2f tex) {
		int posidx = v.indexOf(pos);
		if (posidx == -1) {
			posidx = v.size();
			v.add(pos);
		}
		int texidx = vt.indexOf(tex);
		if (texidx == -1) {
			texidx = vt.size();
			vt.add(tex);
		}
		IndexGroup gr = new IndexGroup();
		gr.pos = posidx;
		gr.tex = texidx;
		int gridx = groups.size();
		groups.add(gr);
		return gridx;
	}

	private static class IndexGroup {
		private int pos;
		private int tex;
	}

	public static class GlyphsModel implements MeshLikeModel {

		private final GlyphsMesh[] meshes;

		public GlyphsModel(GlyphsMesh[] meshes) {
			this.meshes = meshes;
		}

		@Override
		public void render(ShaderProgram program) throws GameException {
			for (GlyphsMesh mesh : meshes) {
				mesh.render(program);
			}
		}

		@Override
		public void cleanup() throws GameException {
			for (GlyphsMesh mesh : meshes) {
				if (mesh != null) {
					mesh.cleanup();
				}
			}
			Arrays.fill(meshes, null);
		}
	}

	public class GlyphsMesh implements MeshLikeModel, ColorMultiplierModel {

		private final Color textureAddColor = Color.white.withAlpha(0F);
		private final Vector4f vectorTextureAddColor = new Vector4f(textureAddColor.r, textureAddColor.g,
				textureAddColor.g, textureAddColor.a);

		private final int vao;
		private final int posbuffer;
		private final int texbuffer;
		private final int idxbuffer;
		private final int vertexCount;
		private final GlyphKey[] texMapIds;
		private final DynamicSizeTextureAtlas texture;
		private final Vector4f color;

		public GlyphsMesh(int[] idxA, float[] posA, float[] texA, GlyphKey[] texMapIds, DynamicSizeTextureAtlas texture,
				Color color) {
			this.color = new Vector4f(color.r, color.g, color.b, color.a);
			this.texture = texture;
			this.texMapIds = texMapIds;
			this.vertexCount = idxA.length;
			vao = glGenVertexArrays();
			GlStates.bindVertexArray(vao);
			idxbuffer = glGenBuffers();
			IntBuffer ibuffer = memAllocInt(idxA.length);
			ibuffer.put(idxA).flip();
			GlStates.bindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxbuffer);
			glBufferData(GL_ELEMENT_ARRAY_BUFFER, ibuffer, GL_STATIC_DRAW);
			memFree(ibuffer);

			posbuffer = glGenBuffers();
			FloatBuffer fbuffer = memAllocFloat(posA.length);
			fbuffer.put(posA).flip();
			GlStates.bindBuffer(GL_ARRAY_BUFFER, posbuffer);
			glBufferData(GL_ARRAY_BUFFER, fbuffer, GL_STATIC_DRAW);
			memFree(fbuffer);

			glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

			texbuffer = glGenBuffers();
			fbuffer = memAllocFloat(texA.length);
			fbuffer.put(texA).flip();
			GlStates.bindBuffer(GL_ARRAY_BUFFER, texbuffer);
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
			GlStates.activeTexture(GL_TEXTURE0);
			GlStates.bindTexture(GL_TEXTURE_2D, texture.getTexture().getTextureId());

			program.utextureAddColor.set(vectorTextureAddColor);
			program.uploadUniforms();

			GlStates.bindVertexArray(vao);
			glEnableVertexAttribArray(0);
			glEnableVertexAttribArray(1);
			glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
			glDisableVertexAttribArray(0);
			glDisableVertexAttribArray(1);

			program.utextureAddColor.set(new Vector4f());
		}

		@Override
		public void cleanup() throws GameException {
			glDeleteBuffers(idxbuffer);
			glDeleteBuffers(posbuffer);
			glDeleteBuffers(texbuffer);
			glDeleteVertexArrays(vao);

			for (GlyphKey key : this.texMapIds) {
				releaseGlyphKey(key);
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

	private int getId(GlyphKey key) {
		return key.hashCode();
	}

	private GlyphEntry requireGlyphEntry(Font font, STBTTFontinfo finfo, char ch, int pixelHeight, float scale)
			throws GameException {
		GlyphKey key = new GlyphKey(scale, ch);
		if (entries.containsKey(key)) {
			GlyphEntry e = entries.get(key);
			e.key.required.incrementAndGet();
			return e;
		}
		GlyphEntry e = createEntry(key, font, finfo, ch, pixelHeight, scale);
		key.required.incrementAndGet();
		entries.put(key, e);
		if (!textures.addGlyph(getId(key), e)) {
			memFree(e.abuffer);
			throw new GameException("Problem while adding glyph to dynamic size TextureAtlasArray");
		}
		memFree(e.abuffer);
		return e;
	}

	private void releaseGlyphKey(GlyphKey key) throws GameException {
		if (key.required.decrementAndGet() == 0) {
			entries.remove(key);
			textures.removeGlyph(getId(key));
		}
	}

	private GlyphEntry createEntry(GlyphKey key, Font font, STBTTFontinfo finfo, char ch, int pixelHeight,
			float scale) {
		int gindex = stbtt_FindGlyphIndex(finfo, ch);
		int codepoint = ch;
		IntBuffer x0 = memAllocInt(1);
		IntBuffer y0 = memAllocInt(1);
		IntBuffer x1 = memAllocInt(1);
		IntBuffer y1 = memAllocInt(1);
		IntBuffer advanceWidth = memAllocInt(1);
		IntBuffer lsb = memAllocInt(1);
		GlyphData gdata = new GlyphData();
		stbtt_GetCodepointBitmapBox(finfo, codepoint, scale, scale, x0, y0, x1, y1);
		stbtt_GetCodepointHMetrics(finfo, codepoint, advanceWidth, lsb);
		gdata.advance = (int) (advanceWidth.get(0) * scale);
		gdata.bearingX = x0.get(0);
		gdata.bearingY = y1.get(0);
		gdata.width = x1.get(0) - x0.get(0);
		gdata.height = y1.get(0) - y0.get(0);
//		ByteBuffer buf = memAlloc(gdata.width * gdata.height);
//		stbtt_MakeCodepointBitmap(finfo, buf, gdata.width, gdata.height, gdata.width, scale, scale, codepoint);

		IntBuffer bw = memAllocInt(1);
		IntBuffer bh = memAllocInt(1);
		IntBuffer xoff = memAllocInt(1);
		IntBuffer yoff = memAllocInt(1);

		ByteBuffer buf = stbtt_GetCodepointBitmap(finfo, scale, scale, codepoint, bw, bh, xoff, yoff);
		byte[] apxls = null;

		if (buf != null) {
			apxls = new byte[buf.capacity()];
			buf.get(apxls, 0, apxls.length);
			buf.position(0);
			stbtt_FreeBitmap(buf);
		} else {
			apxls = new byte[0];
		}
		buf = memAlloc(apxls.length);
		buf.put(apxls);
		buf.position(0);

		memFree(bw);
		memFree(bh);
		memFree(xoff);
		memFree(yoff);
		memFree(advanceWidth);
		memFree(lsb);
		memFree(x0);
		memFree(x1);
		memFree(y0);
		memFree(y1);
		GlyphEntry entry = new GlyphEntry(key, font, gindex, pixelHeight, gdata, codepoint, buf);
		return entry;
	}

	public static class GlyphKey {
		public final float scale;
		public final int codepoint;
		public final AtomicInteger required = new AtomicInteger();

		public GlyphKey(float scale, int codepoint) {
			this.scale = scale;
			this.codepoint = codepoint;
		}

		@Override
		public int hashCode() {
			return Objects.hash(codepoint, scale);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			GlyphKey other = (GlyphKey) obj;
			return codepoint == other.codepoint && Float.floatToIntBits(scale) == Float.floatToIntBits(other.scale);
		}
	}
}
