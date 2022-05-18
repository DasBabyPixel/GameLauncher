package gamelauncher.lwjgl.render;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL43;

import de.dasbabypixel.waveclient.wrappedloader.api.WaveClientAPI;

public class FontInfo {

	public final Font font;
	public final FontMetrics fontMetrics;
	public final GraphicsConfiguration defaultConfiguration;
	public final Map<Integer, Glyph> glyphMap = new ConcurrentHashMap<>();
	public final BitmapStack stack;

	public FontInfo(Font font) {
		this.defaultConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		this.font = font;
		long startedCreation = System.nanoTime();
		BufferedImage dummyImage = defaultConfiguration.createCompatibleImage(1, 1, BufferedImage.OPAQUE);
		Graphics2D dummyGraphics = dummyImage.createGraphics();
		dummyGraphics.setFont(this.font);
		this.fontMetrics = dummyGraphics.getFontMetrics(this.font);
		dummyGraphics.dispose();
		stack = new BitmapStack();

		long finishedCreation = System.nanoTime();
		long creationTime = finishedCreation - startedCreation;

		System.out.printf("Created font in %sms%n", TimeUnit.NANOSECONDS.toMillis(creationTime));
	}

//	public void drawString(float x, float y, de.dasbabypixel.waveclient.wrappedloader.api.util.Color color, String text,
//			float scale) {
//		drawGlyphVectors(x, y, color, toVectors(text), scale);
//	}
//
//	public void drawGlyphVectors(float x, float y, de.dasbabypixel.waveclient.wrappedloader.api.util.Color color,
//			GlyphVector[] lines, float scale) {
//
//		double fheight = getHeight();
//		for (GlyphVector vector : lines) {
//			int numGlyphs = vector.getNumGlyphs();
//			for (int index = 0; index < numGlyphs; index++) {
//				Point2D glyphPos = vector.getGlyphPosition(index);
//				drawGlyph(x + (float) glyphPos.getX(), y + (float) glyphPos.getY(), color, vector, index, scale);
//			}
//			y += fheight;
//		}
//	}
//
//	private void drawGlyph(float drawAtX, float drawAtY, de.dasbabypixel.waveclient.wrappedloader.api.util.Color color,
//			GlyphVector vector, int glyphIndex, float scale) {
//		int code = vector.getGlyphCode(glyphIndex);
//		SingleBitmap map = stack.bind(vector, glyphIndex);
//		SingleBitmap.Entry entry = map.entries.get(code);
//		Rectangle bo = entry.bounds;
//		if (color == null) {
//			color = de.dasbabypixel.waveclient.wrappedloader.api.util.Color.white;
//		}
//
//		float tl = bo.x / (float) map.width;
//		float tt = bo.y / (float) map.height;
//		float tr = tl + (bo.width) / (float) map.width;
//		float tb = tt + (bo.height) / (float) map.height;
//		float fl = drawAtX * scale;
//		float fr = fl + bo.width * scale;
//		float ft = drawAtY * scale;
//		float fb = ft + bo.height * scale;
//
//		float r = color.getRedFloat();
//		float g = color.getGreenFloat();
//		float b = color.getBlueFloat();
//		float a = color.getAlphaFloat();
//
//		Tessellator t = Tessellator.getInstance();
//		WorldRenderer wr = t.getWorldRenderer();
//
//		wr.begin(GL11.GL_TRIANGLE_STRIP, VertexFormats.POSITION_TEX_COLOR);
//		wr.pos(fl, ft, 0).tex(tl, tt).color(r, g, b, a).endVertex();
//		wr.pos(fl, fb, 0).tex(tl, tb).color(r, g, b, a).endVertex();
//		wr.pos(fr, ft, 0).tex(tr, tt).color(r, g, b, a).endVertex();
//		wr.pos(fr, fb, 0).tex(tr, tb).color(r, g, b, a).endVertex();
//		t.draw();
//	}

	public double getHeight() {
		return fontMetrics.getAscent() + fontMetrics.getDescent();
	}

	public double stringWidth(String text) {
		String[] lines = splitLines(text);
		double w = 0;
		for (int i = 0; i < lines.length; i++) {
			String component = lines[i];
			GlyphVector vector = createVector(component);
			w = Math.max(w, vector.getPixelBounds(fontMetrics.getFontRenderContext(), 0, 0).getWidth());
		}
		return w;
	}

	public double stringHeight(String text) {
		double h = 0;
		double fheight = getHeight();
		String[] lines = splitLines(text);
		for (int i = 0; i < lines.length; i++) {
			if (i == lines.length - 1 || i == 0) {
				String component = lines[i];
				GlyphVector vector = createVector(component);
				h += vector.getPixelBounds(fontMetrics.getFontRenderContext(), 0, 0).getHeight();
			} else {
				h += fheight;
				h += fontMetrics.getLeading();
			}
		}
		if (lines.length > 1) {
			h += fontMetrics.getLeading();
		}
		return h;
	}

	public String[] splitLines(String text) {
		return text.split("(\\r\\n|\\r|\\n)");
	}

	public void require(String text) {
		require(toVectors(text));
	}

	public void require(GlyphVector... vectors) {
		for (GlyphVector vector : vectors) {
			for (int index = 0; index < vector.getNumGlyphs(); index++) {
				requireGlyph(vector, index);
			}
			stack.require(vector);
		}
	}

	public void release(String text) {
		release(toVectors(text));
	}

	public void release(GlyphVector... vectors) {
		for (GlyphVector vector : vectors) {
			stack.release(vector);
			for (int index = 0; index < vector.getNumGlyphs(); index++) {
				releaseGlyph(vector, index);
			}
		}
	}

	private void releaseGlyph(GlyphVector vector, int index) {
		int code = vector.getGlyphCode(index);
		Glyph glyph = glyphMap.get(code);
		if (glyph == null)
			throw new NullPointerException(
							"Cant release glyph: glyph not found!");
		if (glyph.requiredBy.decrementAndGet() <= 0) {
			glyphMap.remove(code);
		}
	}

	private Glyph requireGlyph(GlyphVector vector, int index) {
		int code = vector.getGlyphCode(index);
		Glyph glyph = glyphMap.get(code);
		if (glyph == null) {
			glyph = new Glyph(vector, index);
			glyphMap.put(code, glyph);
		}
		glyph.requiredBy.incrementAndGet();
		return glyph;
	}

	private Glyph findGlyph(GlyphVector vector, int index) {
		int code = vector.getGlyphCode(index);
		Glyph glyph = glyphMap.get(code);
		if (glyph == null) {
			System.err.println("Memory Leak!!! Looking for glyph without allocating/freeing it");
			System.err.println("Glyph: \""
							+ (char) vector.getGlyphCharIndex(index) + "\" ("
							+ code + ")");
			Thread.dumpStack();
			glyph = new Glyph(vector, index);
			glyphMap.put(code, glyph);
		}
		return glyph;
	}

	public GlyphVector[] toVectors(String text) {
		String[] lines = splitLines(text);
		GlyphVector[] vectors = new GlyphVector[lines.length];
		for (int i = 0; i < vectors.length; i++) {
			vectors[i] = createVector(lines[i]);
		}
		return vectors;
	}

	public void delete() {
		stack.delete();
		glyphMap.clear();
	}

	private GlyphVector createVector(String text) {
		return font.createGlyphVector(fontMetrics.getFontRenderContext(), text);
	}

	public Font getFont() {
		return font;
	}

	public FontMetrics getFontMetrics() {
		return fontMetrics;
	}

	public class Glyph {
		public final int glyphCode;
		public final float size;
		public final GlyphMetrics metrics;
		public final int width;
		public final int height;
		public final int hash;
		public final Shape shape;
		public final AtomicInteger requiredBy = new AtomicInteger(0);

		public Glyph(GlyphVector vector, int vectorIndex) {
			this.glyphCode = vector.getGlyphCode(vectorIndex);
			this.hash = font.hashCode() * this.glyphCode;
			this.metrics = vector.getGlyphMetrics(vectorIndex);
			this.size = font.getSize2D();
			Point2D glyphPos = vector.getGlyphPosition(vectorIndex);
			float glyphX = (float) glyphPos.getX();
			float glyphY = (float) glyphPos.getY();
			Rectangle bounds = vector.getGlyphPixelBounds(vectorIndex, null, 0, 0);
			this.width = Math.max(bounds.width, 1);
			this.height = Math.max(bounds.height, 1);
			float left = 0;
			float top = 0;
			float x = left - metrics.getLSB();
			float localAscent = -bounds.y;
			float y = localAscent + top;
			this.shape = vector.getGlyphOutline(vectorIndex, x - glyphX, y
							- glyphY);
		}

		public BufferedImage createImage() {
			BufferedImage image = defaultConfiguration.createCompatibleImage(width, height, BufferedImage.TRANSLUCENT);
			Graphics2D g = image.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
			g.setColor(Color.WHITE);
			g.fill(shape);
			g.dispose();
			return image;
		}

		@Override
		public int hashCode() {
			return hash;
		}
	}

	public class BitmapStack implements Bitmap {

		public final int maxSize;
		public final int initialSize;
		public final int sizeMultiplier = 2;
		public final List<SingleBitmap> bitmaps = new CopyOnWriteArrayList<>();
		public final Map<Integer, Entry> lookup = new ConcurrentHashMap<>();
//		public final RenderHelper renderer;

		public BitmapStack() {
			maxSize = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
			initialSize = maxSize / (int) Math.pow(sizeMultiplier, 5);
//			renderer = WaveClientAPI.getInstance().getRenderHelper();
		}

		public SingleBitmap findBitmap(GlyphVector vector, int vectorindex) {
			SingleBitmap map = null;
			Glyph glyph = findGlyph(vector, vectorindex);
			for (int index = 0; index < bitmaps.size(); index++) {
				map = bitmaps.get(index);
				if (addRecursion(map, glyph)) {
					return map;
				}
			}
			map = new SingleBitmap(initialSize, initialSize);
			bitmaps.add(map);
			while (!map.add(glyph)) {
				if (map.hasMaxSize()) {
					map.forceAdd(glyph, new Rectangle(0, 0, glyph.width,
									glyph.height));
					break;
				}
				map.scaleUp(sizeMultiplier);
			}
			return map;
		}

		public boolean addRecursion(SingleBitmap map, Glyph glyph) {
			if (map.add(glyph)) {
				return true;
			}
			if (!map.hasMaxSize()) {
				map.scaleUp(sizeMultiplier);
				return addRecursion(map, glyph);
			}
			return false;
		}

		public void require(GlyphVector vector) {
			for (int index = 0; index < vector.getNumGlyphs(); index++) {
				int code = vector.getGlyphCode(index);
				Entry entry = lookup.get(code);
				if (entry == null) {
					entry = new Entry(findBitmap(vector, index));
					lookup.put(code, entry);
				}
				entry.requiredBy.incrementAndGet();
			}
		}

		public void release(GlyphVector vector) {
			for (int index = 0; index < vector.getNumGlyphs(); index++) {
				int code = vector.getGlyphCode(index);
				Entry entry = lookup.get(code);
				if (entry != null) {
					if (entry.requiredBy.decrementAndGet() == 0) {
						entry.bitmap.remove(findGlyph(vector, index));
						lookup.remove(code);
						if (entry.bitmap.isEmpty()) {
							bitmaps.remove(entry.bitmap);
							entry.bitmap.delete();
						}
					}
				}
			}
		}

		public SingleBitmap bind(GlyphVector vector, int index) {
			int code = vector.getGlyphCode(index);
			Entry entry = lookup.get(code);
			if (entry == null) {
				SingleBitmap found = findBitmap(vector, index);
				entry = new Entry(found);
				lookup.put(code, entry);
			}
			entry.bitmap.bind();
			return entry.bitmap;
		}

		@Override
		public void delete() {
			for (SingleBitmap bitmap : bitmaps) {
				bitmap.delete();
			}
			bitmaps.clear();
			lookup.clear();
		}

		@Override
		public void bind() {
			throw new UnsupportedOperationException();
		}

		public class Entry {

			public final SingleBitmap bitmap;
			public final AtomicInteger requiredBy = new AtomicInteger(0);

			public Entry(SingleBitmap bitmap) {
				this.bitmap = bitmap;
			}
		}

		public class SingleBitmap implements Bitmap {

			public int width, height;
			public int id;
			public final Map<Integer, Entry> entries = new ConcurrentHashMap<>();

			public BufferedImage image;

			public SingleBitmap(int width, int height) {
				this.width = width;
				this.height = height;
				this.id = glGenTextures();
//				WaveClientAPI.getInstance().executeOnGLThread(() -> this.id = renderer.generateTexture());
				image = new BufferedImage(width, height,
								BufferedImage.TRANSLUCENT);
//				WaveClientAPI.getInstance().executeOnGLThread(() -> renderer.allocateTexture(id, width, height));
			}

			public boolean hasMaxSize() {
				return width == maxSize || height == maxSize;
			}

			public void scaleUp(int modifier) {
				int newId = glGenTextures();
				int newW = width * modifier;
				int newH = height * modifier;
				BufferedImage old = image;
				image = new BufferedImage(newW, newH,
								BufferedImage.TRANSLUCENT);
				Graphics2D g = image.createGraphics();
				g.drawRenderedImage(old, null);
				g.dispose();

				renderer.allocateTexture(newId, newW, newH);
				renderer.bindTexture(newId);
				GL43.glCopyImageSubData(id, GL11.GL_TEXTURE_2D, 0, 0, 0, 0, newId, GL11.GL_TEXTURE_2D, 0, 0, 0, 0, width, height, 1);

				width = newW;
				height = newH;
				glDeleteTextures(id);
				id = newId;
			}

			public boolean isEmpty() {
				return entries.isEmpty();
			}

			public void forceAdd(Glyph glyph, Rectangle rect) {
				Entry entry = new Entry(glyph, rect);
				entries.put(glyph.glyphCode, entry);
				BufferedImage img = glyph.createImage();
				renderer.uploadTextureSub(id, img, rect.x, rect.y);
				Graphics2D g = image.createGraphics();
				g.drawRenderedImage(img, null);
				g.dispose();

				try {
					ImageIO.write(image, "png", new File("bitmap.png"));
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}

			public void remove(Glyph glyph) {
				entries.remove(glyph.glyphCode);
			}

			public boolean add(Glyph glyph) {
				Rectangle found = findRect(glyph);
				if (found == null) {
					return false;
				}
				forceAdd(glyph, found);
				return true;
			}

			public Rectangle findRect(Glyph glyph) {
				for (int x = 0; x < width - glyph.width; x++) {
					for (int y = 0; y < height - glyph.height; y++) {
						boolean free = true;
						for (Entry entry : entries.values()) {
							if (entry.bounds.intersects(x, y, glyph.width, glyph.height)) {
								free = false;
								break;
							}
						}
						if (free) {
							return new Rectangle(x, y, glyph.width,
											glyph.height);
						}
					}
				}
				return null;
			}

			@Override
			public void delete() {
				WaveClientAPI.getInstance().executeOnGLThread(() -> renderer.deleteTexture(id));
			}

			@Override
			public void bind() {
				WaveClientAPI.getInstance().executeOnGLThread(() -> renderer.bindTexture(id));
			}

			public class Entry {
				public final Glyph glyph;
				public final Rectangle bounds;

				public Entry(Glyph glyph, Rectangle bounds) {
					this.glyph = glyph;
					this.bounds = bounds;
				}
			}
		}
	}

	public interface Bitmap {

		void bind();

		void delete();

	}
}
