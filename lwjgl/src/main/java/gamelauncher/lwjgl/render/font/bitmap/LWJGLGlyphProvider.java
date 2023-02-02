package gamelauncher.lwjgl.render.font.bitmap;

import de.dasbabypixel.api.property.BooleanValue;
import de.dasbabypixel.api.property.NumberInvalidationListener;
import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.render.GameItem;
import gamelauncher.engine.render.GameItem.GameItemModel;
import gamelauncher.engine.render.font.Font;
import gamelauncher.engine.render.font.GlyphProvider;
import gamelauncher.engine.render.model.CombinedModelsModel;
import gamelauncher.engine.render.model.GlyphStaticModel;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.Key;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.math.Math;
import gamelauncher.engine.util.text.Component;
import gamelauncher.engine.util.text.serializer.PlainTextComponentSerializer;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.glfw.GLFWFrame;
import gamelauncher.lwjgl.render.model.LWJGLCombinedModelsModel;
import gamelauncher.lwjgl.render.model.Texture2DModel;
import gamelauncher.lwjgl.render.texture.LWJGLTexture;
import org.joml.Vector4i;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class LWJGLGlyphProvider extends AbstractGameResource implements GlyphProvider {

	private final GLFWFrame frame;
	private final DynamicSizeTextureAtlas textureAtlas;

	public LWJGLGlyphProvider(LWJGLGameLauncher launcher) throws GameException {
		this.frame = launcher.mainFrame().newFrame();
		this.textureAtlas = new DynamicSizeTextureAtlas(launcher, this.frame.renderThread());
	}

	@Override
	public GlyphStaticModel loadStaticModel(Component text, int pixelHeight) throws GameException {
		Key fkey = text.style().font();
		if (fkey == null)
			fkey = new Key("fonts/calibri.ttf");
		Font font = frame.launcher().fontFactory().createFont(frame.launcher().resourceLoader()
				.resource(
						fkey.toPath(frame.launcher().embedFileSystem().getPath("a").getParent())));
		STBTTFontinfo finfo = STBTTFontinfo.malloc();
		if (!STBTruetype.stbtt_InitFont(finfo, font.data())) {
			font.cleanup();
			throw new GameException("Failed to initialize font");
		}
		float scale = STBTruetype.stbtt_ScaleForPixelHeight(finfo, pixelHeight);
		int[] aascent = new int[1];
		int[] adescent = new int[1];
		int[] alinegap = new int[1];
		STBTruetype.stbtt_GetFontVMetrics(finfo, aascent, adescent, alinegap);
		float descent = adescent[0] * scale;
		float ascent = aascent[0] * scale;
		char[] ar = PlainTextComponentSerializer.serialize(text).toCharArray();
		Collection<CompletableFuture<AtlasEntry>> futures = new ArrayList<>();
		Map<LWJGLTexture, Collection<AtlasEntry>> entries = new HashMap<>();
		for (char ch : ar) {
			GlyphKey key = new GlyphKey(scale, ch);
			CompletableFuture<AtlasEntry> fentry =
					this.requireGlyphKey(key, finfo, ch, pixelHeight, scale);
			futures.add(fentry);
		}
		Threads.waitFor(futures.toArray(new CompletableFuture[0]));
		for (CompletableFuture<AtlasEntry> f : futures) {
			Collection<AtlasEntry> e =
					entries.computeIfAbsent(f.getNow(null).texture, k -> new ArrayList<>());
			e.add(f.getNow(null));
		}
		finfo.free();
		Collection<Model> meshes = new ArrayList<>();
		int mwidth = 0;
		int mheight = 0;
		int xpos = 0;
		float z = 0;
		for (Map.Entry<LWJGLTexture, Collection<AtlasEntry>> entry : entries.entrySet()) {
			for (AtlasEntry e : entry.getValue()) {
				Vector4i bd = e.bounds;

				NumberValue tw = e.texture.width();
				NumberValue th = e.texture.height();
				NumberValue tl = NumberValue.constant(bd.x + 0.5).divide(tw);
				NumberValue tb = NumberValue.constant(bd.y + 0.5).divide(th);
				NumberValue tr = NumberValue.constant(bd.x + 0.5).add(bd.z - 0.5).divide(tw);
				NumberValue tt = NumberValue.constant(bd.y + 0.5).add(bd.w - 0.5).divide(th);

				GlyphData data = e.entry.data;
				int pb = -data.bearingY - data.height;
				int pt = pb + data.height;
				//				float pl = xpos + data.bearingX;
				int pl = xpos;
				int pr = pl + data.width;
				int width = pr - pl;
				int height = pt - pb;
				int x = pl + width / 2;
				int y = pt + height / 2;

				mheight = Math.max(mheight, height);
				mwidth = Math.max(mwidth, xpos + width);
				DynamicModel m = new DynamicModel(e, tl, tr, tt, tb);

				GameItem gi = new GameItem(m);
				gi.position(x, y, z);
				gi.scale(width, height, 1);
				meshes.add(gi.createModel());
				xpos += e.entry.data.advance;

				//				e.getTexture().write();
			}
		}

		//		entries.keySet().stream().findAny().get().write();
		CombinedModelsModel cmodel = new LWJGLCombinedModelsModel(meshes.toArray(new Model[0]));
		GameItem gi = new GameItem(cmodel);
		gi.addColor(1, 1, 1, 0);
		GameItemModel gim = gi.createModel();
		return new GlyphModelWrapper(font, gim, mwidth, mheight, ascent, descent);
	}

	public CompletableFuture<Void> releaseGlyphKey(GlyphKey key) {
		if (key.required.decrementAndGet() == 0) {
			return this.textureAtlas.removeGlyph(this.getId(key));
		}
		return CompletableFuture.completedFuture(null);
	}

	public CompletableFuture<AtlasEntry> requireGlyphKey(GlyphKey key, STBTTFontinfo finfo, char ch,
			int pixelHeight, float scale) throws GameException {
		return this.frame.launcher().threads().cached.submit(()->{
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
				gdata.advance = Math.round(advance.get(0) * scale);
				gdata.bearingX = x0.get(0);
				gdata.bearingY = y1.get(0);
				gdata.width = x1.get(0) - x0.get(0);
				gdata.height = y1.get(0) - y0.get(0);
				IntBuffer bw = MemoryUtil.memAllocInt(1);
				IntBuffer bh = MemoryUtil.memAllocInt(1);
				ByteBuffer output = MemoryUtil.memCalloc((gdata.width + 2) * (gdata.height + 2));
				STBTruetype.stbtt_MakeCodepointBitmap(finfo, output, gdata.width, gdata.height,
						gdata.width + 2, scale, scale, ch);

				// Setup for usage by LWJGLTexture
				ByteBuffer buf = MemoryUtil.memCalloc(
						Integer.BYTES * output.capacity() + Integer.BYTES * 2
								+ LWJGLTexture.SIGNATURE_RAW.length);
				buf.put(LWJGLTexture.SIGNATURE_RAW);
				buf.putInt(gdata.width + 2);
				buf.putInt(gdata.height + 2);
				//			buf.put(apxls);
				for (int i = 0; i < output.capacity(); i++) {
					buf.position(buf.position() + 3);
					buf.put(output.get(i));
				}
				buf.flip();

				MemoryUtil.memFree(output);
				MemoryUtil.memFree(bw);
				MemoryUtil.memFree(bh);
				MemoryUtil.memFree(advance);
				MemoryUtil.memFree(lsb);
				MemoryUtil.memFree(x0);
				MemoryUtil.memFree(y0);
				MemoryUtil.memFree(x1);
				MemoryUtil.memFree(y1);
				GlyphEntry e = new GlyphEntry(gdata, gindex, pixelHeight, key, buf);

				if (!this.textureAtlas.addGlyph(id, e).suc()) {
					throw new GameException("Could not add glyph to texture atlas");
				}
				entry = this.textureAtlas.getGlyph(id);

				//			if (!Threads.waitFor(this.textureAtlas.addGlyph(id, e))) {
				//				throw new GameException("Could not add glyph to texture atlas");
				//			}
				//			entry = this.textureAtlas.getGlyph(id);
			}
			entry.entry.key.required.incrementAndGet();
			return entry;
		});
	}

	@Override
	public void cleanup0() throws GameException {
		this.textureAtlas.cleanup();
		this.frame.cleanup();
	}

	public int getId(GlyphKey key) {
		return key.hashCode();
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
				texture2DModel = new Texture2DModel(e.texture, tl.floatValue(), 1 - tb.floatValue(),
						tr.floatValue(), 1 - tt.floatValue());
			}
			texture2DModel.render(program);
		}

		@Override
		protected void cleanup0() throws GameException {
			Threads.waitFor(releaseGlyphKey(e.entry.key));
			if (texture2DModel != null)
				texture2DModel.cleanup();
		}
	}

}
