package gamelauncher.lwjgl.render.font;

import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.awt.Rectangle;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.lwjgl.stb.STBTTFontinfo;

import gamelauncher.engine.render.GameItem;
import gamelauncher.engine.render.GameItem.GameItemModel;
import gamelauncher.engine.render.font.Font;
import gamelauncher.engine.render.font.GlyphProvider;
import gamelauncher.engine.render.model.CombinedModelsModel;
import gamelauncher.engine.render.model.GlyphStaticModel;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.math.Math;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.model.LWJGLCombinedModelsModel;
import gamelauncher.lwjgl.render.model.Texture2DModel;
import gamelauncher.lwjgl.render.texture.LWJGLTexture;

@SuppressWarnings("javadoc")
public class LWJGLGlyphProvider extends AbstractGameResource implements GlyphProvider {

	private final LWJGLGameLauncher launcher;

	private final ExecutorThread owner;

	private final DynamicSizeTextureAtlas textureAtlas;

	public LWJGLGlyphProvider(LWJGLGameLauncher launcher, ExecutorThread owner) {
		this.launcher = launcher;
		this.owner = owner;
		this.textureAtlas = new DynamicSizeTextureAtlas(this.launcher, this.owner);
	}

	@Override
	public GlyphStaticModel loadStaticModel(Font font, String text, int pixelHeight) throws GameException {
		STBTTFontinfo finfo = STBTTFontinfo.malloc();
		if (!stbtt_InitFont(finfo, font.data())) {
			throw new GameException("Failed to initialize font");
		}
		float scale = stbtt_ScaleForPixelHeight(finfo, pixelHeight);
		int[] aascent = new int[1];
		int[] adescent = new int[1];
		int[] alinegap = new int[1];
		stbtt_GetFontVMetrics(finfo, aascent, adescent, alinegap);
		float descent = adescent[0] * scale;
		float ascent = aascent[0] * scale;
		char[] ar = text.toCharArray();
		Map<LWJGLTexture, Collection<AtlasEntry>> entries = new HashMap<>();
		for (int i = 0; i < ar.length; i++) {
			char ch = ar[i];
			GlyphKey key = new GlyphKey(scale, ch);
			AtlasEntry entry = requireGlyphKey(key, finfo, ch, pixelHeight, scale);
			Collection<AtlasEntry> e = entries.get(entry.getTexture());
			if (e == null) {
				e = new ArrayList<>();
				entries.put(entry.getTexture(), e);
			}
			e.add(entry);
		}
		Collection<Model> meshes = new ArrayList<>();
		int mwidth = 0;
		int mheight = 0;
		int xpos = 0;
		float z = 0;
		for (Map.Entry<LWJGLTexture, Collection<AtlasEntry>> entry : entries.entrySet()) {
			for (AtlasEntry e : entry.getValue()) {
				Rectangle bd = e.getBounds();

				float tw = e.getTexture().getWidth();
				float th = e.getTexture().getHeight();
				float tl = (bd.x) / (tw);
				float tb = (bd.y) / (th);
				float tr = tl + (bd.width) / (tw);
				float tt = tb + (bd.height) / (th);

				GlyphData data = e.getEntry().data;
				float pb = -data.bearingY - data.height;
				float pt = pb + data.height;
//				float pl = xpos + data.bearingX;
				float pl = xpos;
				float pr = pl + data.width;
				float width = pr - pl;
				float height = pt - pb;
				float x = pl + width / 2F;
				float y = pt + height / 2F;

				mheight = Math.max(mheight, Math.ceil(height));
				mwidth = Math.ceil(xpos + width);

				Model m = new Texture2DModel(e.getTexture(), tl, 1 - tb, tr, 1 - tt) {

					@Override
					public void cleanup0() throws GameException {
						Threads.waitFor(releaseGlyphKey(e.getEntry().key));
						super.cleanup0();
					}

				};

				GameItem gi = new GameItem(m);
				gi.setPosition(x, y, z);
				gi.setScale(width, height, 1);
				m = gi.createModel();
				meshes.add(m);
				xpos += e.getEntry().data.advance;
			}
		}
//		entries.keySet().stream().findAny().get().write();
		CombinedModelsModel cmodel = new LWJGLCombinedModelsModel(meshes.toArray(new Model[meshes.size()]));
		GameItem gi = new GameItem(cmodel);
		gi.setAddColor(1, 1, 1, 0);
		GameItemModel gim = gi.createModel();
		return new GlyphModelWrapper(gim, mwidth, mheight, ascent, descent);
	}

	public CompletableFuture<Void> releaseGlyphKey(GlyphKey key) {
		if (key.required.decrementAndGet() == 0) {
			return textureAtlas.removeGlyph(getId(key));
		}
		return CompletableFuture.completedFuture(null);
	}

	public AtlasEntry requireGlyphKey(GlyphKey key, STBTTFontinfo finfo, char ch, int pixelHeight, float scale)
			throws GameException {
		int id = getId(key);
		AtlasEntry entry = textureAtlas.getGlyph(id);
		ret: {
			if (entry != null) {
				break ret;
			}
			int gindex = stbtt_FindGlyphIndex(finfo, id);
			int codepoint = ch;
			IntBuffer x0 = memAllocInt(1);
			IntBuffer y0 = memAllocInt(1);
			IntBuffer x1 = memAllocInt(1);
			IntBuffer y1 = memAllocInt(1);
			IntBuffer advance = memAllocInt(1);
			IntBuffer lsb = memAllocInt(1);
			GlyphData gdata = new GlyphData();
			stbtt_GetCodepointBitmapBox(finfo, codepoint, scale, scale, x0, y0, x1, y1);
			stbtt_GetCodepointHMetrics(finfo, codepoint, advance, lsb);
			gdata.advance = (int) (advance.get(0) * scale);
			gdata.bearingX = x0.get(0);
			gdata.bearingY = y1.get(0);
			gdata.width = x1.get(0) - x0.get(0);
			gdata.height = y1.get(0) - y0.get(0);
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
			// Setup for usage by LWJGLTexture
			buf = memCalloc(Integer.BYTES * apxls.length + Integer.BYTES * 2 + LWJGLTexture.SIGNATURE_RAW.length);
			buf.put(LWJGLTexture.SIGNATURE_RAW);
			buf.putInt(gdata.width);
			buf.putInt(gdata.height);
//			buf.put(apxls);
			for (int i = 0; i < apxls.length; i++) {
				buf.position(buf.position() + 3);
				buf.put(apxls[i]);
			}
			buf.flip();

			memFree(bw);
			memFree(bh);
			memFree(xoff);
			memFree(yoff);
			memFree(advance);
			memFree(lsb);
			memFree(x0);
			memFree(y0);
			memFree(x1);
			memFree(y1);
			GlyphEntry e = new GlyphEntry(gdata, gindex, pixelHeight, key, buf);
			if (!Threads.waitFor(textureAtlas.addGlyph(id, e))) {
				throw new GameException("Could not add glyph to texture atlas");
			}
			entry = textureAtlas.getGlyph(id);
		}
		entry.getEntry().key.required.incrementAndGet();
		return entry;
	}

	@Override
	public void cleanup0() throws GameException {
		textureAtlas.cleanup();
	}

	public int getId(GlyphKey key) {
		return key.hashCode();
	}

}
