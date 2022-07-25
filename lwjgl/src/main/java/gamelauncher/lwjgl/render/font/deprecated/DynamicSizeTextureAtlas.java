package gamelauncher.lwjgl.render.font.deprecated;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.function.GameFunction;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.texture.LWJGLTexture;

@SuppressWarnings("javadoc")
@Deprecated
public class DynamicSizeTextureAtlas extends TextureAtlas {

	public int size;
	private final AtomicReference<LWJGLTexture> texture = new AtomicReference<>(null);
	public final Map<Integer, Rectangle> glyphBounds = new ConcurrentHashMap<>();
	private final int maxSize;
	private final ExecutorThread owner;
	private final LWJGLGameLauncher launcher;
	private final GameFunction<DynamicSizeTextureAtlas, String> fileSuffixAppendix;

	public DynamicSizeTextureAtlas(LWJGLGameLauncher launcher, ExecutorThread owner, int maxTextureSize,
			int initialSize, GameFunction<DynamicSizeTextureAtlas, String> fileSuffixAppendix) {
		this.launcher = launcher;
		this.fileSuffixAppendix = fileSuffixAppendix;
		this.owner = owner;
		this.maxSize = maxTextureSize;
		resizeTexture(initialSize);
	}

	private CompletableFuture<Void> resizeTexture(int newSize) {
		CompletableFuture<LWJGLTexture> cfut = launcher.getTextureManager().createTexture(owner);
		LWJGLTexture texture = this.texture.get();
		this.size = newSize;
		return cfut.thenAccept(tex -> {
			try {
				Threads.waitFor(tex.allocate(newSize, newSize));
				if (texture != null) {
					Threads.waitFor(texture.copyTo(tex));
					texture.cleanup();
				}
				DynamicSizeTextureAtlas.this.texture.set(tex);
			} catch (GameException ex) {
				ex.printStackTrace();
			}
		});
	}

	@Override
	public CompletableFuture<Boolean> addGlyph(int id, GlyphEntry glyph) throws GameException {
		CompletableFuture<Boolean> fut = new CompletableFuture<>();
		launcher.getThreads().workStealing.execute(() -> {
			try {
				Rectangle rect = new Rectangle(glyph.glyphData.width, glyph.glyphData.height);
				while (true) {
					boolean suc = findFit(rect);
					if (!suc) {
						if (!Threads.waitFor(scaleUp())) {
							fut.complete(false);
							return;
						}
						continue;
					}
					break;
				}
				if (!Threads.waitFor(super.addGlyph(id, glyph))) {
					fut.complete(false);
					return;
				}
//			texture.bind();
//			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
//			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
//			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
//			glTexSubImage2D(GL_TEXTURE_2D, 0, rect.x, rect.y, rect.width, rect.height, GL_ALPHA, GL_UNSIGNED_BYTE,
//					glyph.abuffer);
				glyphBounds.put(id, rect);
				texture.get()
						.uploadAsync(rect.x, rect.y, rect.width, rect.height, glyph.abuffer)
						.thenRun(() -> fut.complete(true));
			} catch (GameException ex) {
				fut.completeExceptionally(ex);
				launcher.handleError(ex);
			}
		});
		return fut;
	}

	public LWJGLTexture getTexture() {
		return texture.get();
	}

//	public BufferedImage getBufferedImage() {
//		ByteBuffer pixels = getBufferedImageBuffer();
//		BufferedImage img = new BufferedImage(size, size, BufferedImage.TRANSLUCENT);
//		for (int y = 0; y < size; y++) {
//			for (int x = 0; x < size; x++) {
//				img.setRGB(x, y, pixels.get(y * size + x) << 24);
//			}
//		}
//		memFree(pixels);
//		return img;
//	}
//
//	public ByteBuffer getBufferedImageBuffer() {
//		ByteBuffer pixels = memAlloc(size * size);
//		GlStates.bindTexture(GL_TEXTURE_2D, texture.getTextureId());
//		glGetTexImage(GL_TEXTURE_2D, 0, GL_ALPHA, GL_UNSIGNED_BYTE, pixels);
//		return pixels;
//	}

	@Override
	public void cleanup() throws GameException {

//		ByteBuffer pixels = getBufferedImageBuffer();
//		new Thread(() -> {
//			try {
//				BufferedImage img = new BufferedImage(size, size, BufferedImage.TRANSLUCENT);
//				for (int y = 0; y < size; y++) {
//					for (int x = 0; x < size; x++) {
//						img.setRGB(x, y, pixels.get(y * size + x) << 24);
//					}
//				}
//				memFree(pixels);
//				System.out.println("Writing texture atlas to working directory");
//				ImageIO.write(img, "png",
//						new File("tatlas-" + this.fileSuffixAppendix.apply(DynamicSizeTextureAtlas.this) + ".png"));
//			} catch (IOException ex) {
//				ex.printStackTrace();
//			} catch (GameException ex) {
//				ex.printStackTrace();
//			}
//		}).start();

		glyphBounds.clear();
		glyphs.clear();
		texture.getAndSet(null).cleanup();
	}

	private CompletableFuture<Boolean> scaleUp() {
		int newSize = size * 2;
		CompletableFuture<Boolean> fut;
		if (newSize > maxSize) {
			fut = CompletableFuture.completedFuture(false);
		} else {
			fut = resizeTexture(newSize).thenApply(n -> true);
		}
		return fut;
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
