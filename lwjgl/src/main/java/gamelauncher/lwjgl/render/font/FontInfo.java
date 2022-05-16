package gamelauncher.lwjgl.render.font;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.joml.Vector4f;

import gamelauncher.engine.GameException;
import gamelauncher.lwjgl.render.LWJGLTexture;

public class FontInfo {

	private final Font font;
	private final int MAX_TEXTURE_SIZE;
	private LWJGLTexture texture;
	private BufferedImage dummyImage;
	private Graphics2D dummyGraphics;
	private FontRenderContext context;

	private final Map<Character, CharacterReserveInfo> characters = new ConcurrentHashMap<>();
	private final List<Bitmap> bitmaps = new CopyOnWriteArrayList<>();

	public FontInfo(Font font) {
		this.font = font;
		this.MAX_TEXTURE_SIZE = glGetInteger(GL_MAX_TEXTURE_SIZE);
		this.dummyImage = new BufferedImage(1, 1, BufferedImage.TRANSLUCENT);
		this.dummyGraphics = this.dummyImage.createGraphics();
		this.context = this.dummyGraphics.getFontRenderContext();
	}

	public void load(String text) {
		for (char c : text.toCharArray()) {
			CharacterReserveInfo ci = characters.get(c);
			if (ci == null) {
				ci = new CharacterReserveInfo();
				characters.put(c, ci);
			}
			ci.required.incrementAndGet();
			ci.bitmap.set(addToBitmap(c));
		}
	}

	public void release(String text) {
		for (char c : text.toCharArray()) {
			CharacterReserveInfo ci = characters.get(c);
			if (ci != null) {
				if (ci.required.decrementAndGet() == 0) {
					ci.bitmap.get().remove(c);
					ci.bitmap.set(null);
					characters.remove(c);
				}
			}
		}
	}

	private Bitmap addToBitmap(char c) throws GameException {
		Bitmap bitmap = null;
		for (Bitmap bm : bitmaps) {
			if (bm.tryAdd(c)) {
				bitmap = bm;
				break;
			}
		}
		if (bitmap == null) {
			bitmap = new Bitmap();
			if (bitmap.tryAdd(c)) {
				throw new GameException(new UnsupportedOperationException());
			}
			bitmaps.add(bitmap);
		}
		return bitmap;
	}

	private class Bitmap {
		private final int id;
		private final ConcurrentHashMap<Character, Vector4f> chars = new ConcurrentHashMap<>();

		public Bitmap(int id) {
			this.id = id;
		}

		public boolean tryAdd(char c) {
			
			for(int x = 0; x < width)
			return false;
		}

		public Bitmap() {
			this(glGenTextures());
		}

		public void remove(char c) {
			chars.remove(c);
		}
	}

	private class CharacterInfo {

		private final GlyphVector vector;
		private final int index;

		public CharacterInfo(GlyphVector vector, int index) {
			this.vector = vector;
			this.index = index;
			GlyphMetrics metrics = vector.getGlyphMetrics(index);
			Shape shape = vector.getGlyphOutline(index);
			
		}
	}

	private class CharacterReserveInfo {
		private final AtomicInteger required = new AtomicInteger();
		private final AtomicReference<Bitmap> bitmap = new AtomicReference<>(null);
	}

	private Bitmap[] bitmaps(int count) {
		Bitmap[] b = new Bitmap[count];
		int[] tids = new int[count];
		glGenTextures(tids);
		for (int i = 0; i < count; i++) {
			b[i] = new Bitmap(tids[i]);
		}
		return b;
	}
}
