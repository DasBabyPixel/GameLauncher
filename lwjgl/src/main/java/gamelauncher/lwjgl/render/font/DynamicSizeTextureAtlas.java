package gamelauncher.lwjgl.render.font;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import gamelauncher.engine.GameException;
import gamelauncher.engine.util.GameFunction;
import gamelauncher.lwjgl.render.GlStates;
import gamelauncher.lwjgl.render.LWJGLTexture;

public class DynamicSizeTextureAtlas extends TextureAtlas {

	public int size;
	private LWJGLTexture texture = null;
	public final Map<Integer, Rectangle> glyphBounds = new ConcurrentHashMap<>();
	private final int maxSize;
	private final GameFunction<DynamicSizeTextureAtlas, String> fileSuffixAppendix;

	public DynamicSizeTextureAtlas(int initialSize, GameFunction<DynamicSizeTextureAtlas, String> fileSuffixAppendix) throws GameException {
		this.fileSuffixAppendix = fileSuffixAppendix;
		maxSize = glGetInteger(GL_MAX_TEXTURE_SIZE);
		resizeTexture(initialSize);
	}

	private void resizeTexture(int newSize) throws GameException {
		lock.lock();
		int id = glGenTextures();
		GlStates.bindTexture(GL_TEXTURE_2D, id);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, newSize, newSize, 0, GL_ALPHA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
		if (texture != null) {
			glCopyImageSubData(texture.getTextureId(), GL_TEXTURE_2D, 0, 0, 0, 0, id, GL_TEXTURE_2D, 0, 0, 0, 0, size,
					size, 1);
			texture.cleanup();
		}
		texture = new LWJGLTexture(id);
		this.size = newSize;
		lock.unlock();
	}

	@Override
	public boolean addGlyph(int id, GlyphEntry glyph) throws GameException {
		Rectangle rect = new Rectangle(glyph.glyphData.width, glyph.glyphData.height);
		while (true) {
			boolean suc = findFit(rect);
			if (!suc) {
				if (!scaleUp()) {
					return false;
				}
				continue;
			}
			break;
		}
		if (!super.addGlyph(id, glyph)) {
			return false;
		}
		GlStates.bindTexture(GL_TEXTURE_2D, texture.getTextureId());
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glTexSubImage2D(GL_TEXTURE_2D, 0, rect.x, rect.y, rect.width, rect.height, GL_ALPHA, GL_UNSIGNED_BYTE,
				glyph.abuffer);
		glyphBounds.put(id, rect);
		return true;
	}

	public LWJGLTexture getTexture() {
		return texture;
	}

	public BufferedImage getBufferedImage() {
		ByteBuffer pixels = getBufferedImageBuffer();
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TRANSLUCENT);
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				img.setRGB(x, y, pixels.get(y * size + x) << 24);
			}
		}
		memFree(pixels);
		return img;
	}

	public ByteBuffer getBufferedImageBuffer() {
		ByteBuffer pixels = memAlloc(size * size);
		GlStates.bindTexture(GL_TEXTURE_2D, texture.getTextureId());
		glGetTexImage(GL_TEXTURE_2D, 0, GL_ALPHA, GL_UNSIGNED_BYTE, pixels);
		return pixels;
	}

	@Override
	public void cleanup() throws GameException {

		ByteBuffer pixels = getBufferedImageBuffer();
		new Thread(() -> {
			try {
				BufferedImage img = new BufferedImage(size, size, BufferedImage.TRANSLUCENT);
				for (int y = 0; y < size; y++) {
					for (int x = 0; x < size; x++) {
						img.setRGB(x, y, pixels.get(y * size + x) << 24);
					}
				}
				memFree(pixels);
				ImageIO.write(img, "png",
						new File("tatlas-" + this.fileSuffixAppendix.apply(DynamicSizeTextureAtlas.this) + ".png"));
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (GameException ex) {
				ex.printStackTrace();
			}
		}).start();

		glyphBounds.clear();
		glyphs.clear();
		texture.cleanup();
	}

	private boolean scaleUp() throws GameException {
		lock.lock();
		int newSize = size * 2;
		if (newSize > maxSize) {
			lock.unlock();
			return false;
		}
		resizeTexture(newSize);
		lock.unlock();
		return true;
	}

	/**
	 * 
	 * @param rect
	 * @return true if success, if success then the rect will have different x and y
	 *         coordinates
	 */
	private boolean findFit(Rectangle rect) {
		int ox = rect.x;
		int oy = rect.y;
		rect.y = 0;
		boolean found = false;
		Collection<Rectangle> check = new HashSet<>(glyphBounds.values());
		Collection<Rectangle> remove = new HashSet<>();
		yl: for (; rect.y < size - rect.height; rect.y++) {
			rect.x = 0;
			for (Rectangle r : check) {
				if (r.y + r.height < rect.y) {
					remove.add(r);
				}
			}
			check.removeAll(remove);
			remove.clear();

			xl: for (; rect.x < size - rect.width; rect.x++) {
				for (Rectangle r : check) {
					if (r.intersects(rect)) {
						rect.x = r.x + r.width - 1;
						continue xl;
					}
				}
				found = true;
				break yl;
			}
		}
		if (!found) {
			rect.x = ox;
			rect.y = oy;
		}
		return found;
	}

	@Override
	public void removeGlyph(int id) throws GameException {
		glyphBounds.remove(id);
		super.removeGlyph(id);
	}
}
