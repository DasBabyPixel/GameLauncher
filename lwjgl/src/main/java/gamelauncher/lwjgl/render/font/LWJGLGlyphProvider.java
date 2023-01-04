package gamelauncher.lwjgl.render.font;

import java.awt.Rectangle;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import de.dasbabypixel.api.property.BooleanValue;
import de.dasbabypixel.api.property.NumberInvalidationListener;
import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.render.shader.ShaderProgram;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;

import gamelauncher.engine.render.GameItem;
import gamelauncher.engine.render.GameItem.GameItemModel;
import gamelauncher.engine.render.font.Font;
import gamelauncher.engine.render.font.GlyphProvider;
import gamelauncher.engine.render.model.CombinedModelsModel;
import gamelauncher.engine.render.model.GlyphStaticModel;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.math.Math;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.glfw.GLFWFrame;
import gamelauncher.lwjgl.render.model.LWJGLCombinedModelsModel;
import gamelauncher.lwjgl.render.model.Texture2DModel;
import gamelauncher.lwjgl.render.texture.LWJGLTexture;

public class LWJGLGlyphProvider extends AbstractGameResource implements GlyphProvider {

	private final GLFWFrame frame;

	private final DynamicSizeTextureAtlas textureAtlas;

	public LWJGLGlyphProvider(LWJGLGameLauncher launcher) throws GameException {
		this.frame = launcher.getMainFrame().newFrame();
		this.textureAtlas = new DynamicSizeTextureAtlas(launcher, this.frame.renderThread());
	}

	@Override
	public GlyphStaticModel loadStaticModel(Font font, String text, int pixelHeight)
			throws GameException {
		STBTTFontinfo finfo = STBTTFontinfo.malloc();
		if (!STBTruetype.stbtt_InitFont(finfo, font.data())) {
			throw new GameException("Failed to initialize font");
		}
		float scale = STBTruetype.stbtt_ScaleForPixelHeight(finfo, pixelHeight);
		int[] aascent = new int[1];
		int[] adescent = new int[1];
		int[] alinegap = new int[1];
		STBTruetype.stbtt_GetFontVMetrics(finfo, aascent, adescent, alinegap);
		float descent = adescent[0] * scale;
		float ascent = aascent[0] * scale;
		char[] ar = text.toCharArray();
		Map<LWJGLTexture, Collection<AtlasEntry>> entries = new HashMap<>();
		for (char ch : ar) {
			GlyphKey key = new GlyphKey(scale, ch);
			AtlasEntry entry = this.requireGlyphKey(key, finfo, ch, pixelHeight, scale);
			Collection<AtlasEntry> e =
					entries.computeIfAbsent(entry.getTexture(), k -> new ArrayList<>());
			e.add(entry);
		}
		finfo.free();
		Collection<Model> meshes = new ArrayList<>();
		int mwidth = 0;
		int mheight = 0;
		int xpos = 0;
		float z = 0;
		for (Map.Entry<LWJGLTexture, Collection<AtlasEntry>> entry : entries.entrySet()) {
			for (AtlasEntry e : entry.getValue()) {
				Rectangle bd = e.getBounds();

				NumberValue tw = e.getTexture().getWidth();
				NumberValue th = e.getTexture().getHeight();
				NumberValue tl = NumberValue.constant(bd.x).divide(tw);
				NumberValue tb = NumberValue.constant(bd.y).divide(th);
				NumberValue tr = tl.add(bd.width).divide(tw);
				NumberValue tt = tb.add(bd.height).divide(th);

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
				mwidth = Math.max(mwidth, Math.ceil(xpos + width));
				DynamicModel m = new DynamicModel(e, tl, tr, tt, tb);

				GameItem gi = new GameItem(m);
				gi.setPosition(x, y, z);
				gi.setScale(width, height, 1);
				meshes.add(gi.createModel());
				xpos += e.getEntry().data.advance;
			}
		}

		//		entries.keySet().stream().findAny().get().write();
		CombinedModelsModel cmodel = new LWJGLCombinedModelsModel(meshes.toArray(new Model[0]));
		GameItem gi = new GameItem(cmodel);
		gi.setAddColor(1, 1, 1, 0);
		GameItemModel gim = gi.createModel();
		return new GlyphModelWrapper(gim, mwidth, mheight, ascent, descent);
	}

	private class DynamicModel extends AbstractGameResource implements Model {

		private final AtlasEntry e;
		private final NumberValue tl;
		private final NumberValue tr;
		private final NumberValue tt;
		private final NumberValue tb;
		private final BooleanValue invalid = BooleanValue.trueValue();

		private Texture2DModel texture2DModel;

		public DynamicModel(AtlasEntry e, NumberValue tl, NumberValue tr, NumberValue tt,
				NumberValue tb) {
			this.e = e;
			this.tl = tl;
			this.tr = tr;
			this.tt = tt;
			this.tb = tb;
			NumberInvalidationListener invalidationListener = property -> invalid.setValue(true);
			tl.addListener(invalidationListener);
			tr.addListener(invalidationListener);
			tt.addListener(invalidationListener);
			tb.addListener(invalidationListener);
		}

		@Override
		public void render(ShaderProgram program) throws GameException {
			if (invalid.booleanValue()) {
				invalid.setValue(false);
				if (texture2DModel != null) {
					texture2DModel.cleanup();
				}
				System.out.println("regenerate");
				texture2DModel =
						new Texture2DModel(e.getTexture(), tl.floatValue(), 1 - tb.floatValue(),
								tr.floatValue(), 1 - tt.floatValue());
			}
			texture2DModel.render(program);
		}

		@Override
		protected void cleanup0() throws GameException {
			Threads.waitFor(releaseGlyphKey(e.getEntry().key));
			if (texture2DModel != null)
				texture2DModel.cleanup();
		}
	}

	public CompletableFuture<Void> releaseGlyphKey(GlyphKey key) {
		if (key.required.decrementAndGet() == 0) {
			return this.textureAtlas.removeGlyph(this.getId(key));
		}
		return CompletableFuture.completedFuture(null);
	}

	public AtlasEntry requireGlyphKey(GlyphKey key, STBTTFontinfo finfo, char ch, int pixelHeight,
			float scale) throws GameException {
		int id = this.getId(key);
		AtlasEntry entry = this.textureAtlas.getGlyph(id);
		ret:
		{
			if (entry != null) {
				break ret;
			}
			int gindex = STBTruetype.stbtt_FindGlyphIndex(finfo, id);
			IntBuffer x0 = MemoryUtil.memAllocInt(1);
			IntBuffer y0 = MemoryUtil.memAllocInt(1);
			IntBuffer x1 = MemoryUtil.memAllocInt(1);
			IntBuffer y1 = MemoryUtil.memAllocInt(1);
			IntBuffer advance = MemoryUtil.memAllocInt(1);
			IntBuffer lsb = MemoryUtil.memAllocInt(1);
			GlyphData gdata = new GlyphData();
			STBTruetype.stbtt_GetCodepointBitmapBox(finfo, ch, scale, scale, x0, y0, x1, y1);
			STBTruetype.stbtt_GetCodepointHMetrics(finfo, ch, advance, lsb);
			gdata.advance = (int) (advance.get(0) * scale);
			gdata.bearingX = x0.get(0);
			gdata.bearingY = y1.get(0);
			gdata.width = x1.get(0) - x0.get(0);
			gdata.height = y1.get(0) - y0.get(0);
			IntBuffer bw = MemoryUtil.memAllocInt(1);
			IntBuffer bh = MemoryUtil.memAllocInt(1);
			IntBuffer xoff = MemoryUtil.memAllocInt(1);
			IntBuffer yoff = MemoryUtil.memAllocInt(1);
			ByteBuffer buf =
					STBTruetype.stbtt_GetCodepointBitmap(finfo, scale, scale, ch, bw, bh, xoff,
							yoff);
			byte[] apxls;

			if (buf != null) {
				apxls = new byte[buf.capacity()];
				buf.get(apxls, 0, apxls.length);
				buf.position(0);
				STBTruetype.stbtt_FreeBitmap(buf);
			} else {
				apxls = new byte[0];
			}
			// Setup for usage by LWJGLTexture
			buf = MemoryUtil.memCalloc(Integer.BYTES * apxls.length + Integer.BYTES * 2
					+ LWJGLTexture.SIGNATURE_RAW.length);
			buf.put(LWJGLTexture.SIGNATURE_RAW);
			buf.putInt(gdata.width);
			buf.putInt(gdata.height);
			//			buf.put(apxls);
			for (byte apxl : apxls) {
				buf.position(buf.position() + 3);
				buf.put(apxl);
			}
			buf.flip();

			MemoryUtil.memFree(bw);
			MemoryUtil.memFree(bh);
			MemoryUtil.memFree(xoff);
			MemoryUtil.memFree(yoff);
			MemoryUtil.memFree(advance);
			MemoryUtil.memFree(lsb);
			MemoryUtil.memFree(x0);
			MemoryUtil.memFree(y0);
			MemoryUtil.memFree(x1);
			MemoryUtil.memFree(y1);
			GlyphEntry e = new GlyphEntry(gdata, gindex, pixelHeight, key, buf);
			if (!Threads.waitFor(this.textureAtlas.addGlyph(id, e))) {
				throw new GameException("Could not add glyph to texture atlas");
			}
			entry = this.textureAtlas.getGlyph(id);
		}
		entry.getEntry().key.required.incrementAndGet();
		return entry;
	}

	@Override
	public void cleanup0() throws GameException {
		this.textureAtlas.cleanup();
		this.frame.cleanup();
	}

	public int getId(GlyphKey key) {
		return key.hashCode();
	}

}
