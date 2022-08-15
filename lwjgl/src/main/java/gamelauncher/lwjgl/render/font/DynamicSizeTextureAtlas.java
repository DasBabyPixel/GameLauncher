package gamelauncher.lwjgl.render.font;

import static org.lwjgl.opengles.GLES20.*;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.ByteBufferBackedInputStream;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.function.GameResource;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.texture.LWJGLTexture;

@SuppressWarnings("javadoc")
public class DynamicSizeTextureAtlas implements GameResource {

	private final Logger logger = Logger.getLogger();

	private final Map<Integer, Entry> glyphs = new HashMap<>();

	private final Map<LWJGLTexture, Collection<Entry>> byTexture = new HashMap<>();

	private final Lock lock = new ReentrantLock(true);

	private final LWJGLGameLauncher launcher;

	private final ExecutorThread owner;

	volatile int maxTextureSize;

	public DynamicSizeTextureAtlas(LWJGLGameLauncher launcher, ExecutorThread owner) {
		this.launcher = launcher;
		this.owner = owner;
		this.owner.submit(() -> {
			this.maxTextureSize = glGetInteger(GL_MAX_TEXTURE_SIZE);
		});
	}

	public Entry getGlyph(int id) {
		try {
			lock.lock();
			return glyphs.get(id);
		} finally {
			lock.unlock();
		}
	}

	public CompletableFuture<Void> removeGlyph(int glyphId) {
		return launcher.getThreads().cached.submit(() -> {
			try {
				lock.lock();
				Entry entry = glyphs.remove(glyphId);
				Collection<Entry> col = byTexture.get(entry.texture);
				col.remove(entry);
				if (col.isEmpty()) {
					byTexture.remove(entry.texture);
					entry.texture.cleanup();
				}
			} finally {
				lock.unlock();
			}
		});
	}

	public CompletableFuture<Boolean> addGlyph(int glyphId, GlyphEntry entry) {
		return launcher.getThreads().cached.submit(() -> {
			try {
				lock.lock();
				if (glyphs.containsKey(glyphId)) {
					return true;
				}
				Entry e = new Entry(null, entry, new Rectangle(entry.data.width, entry.data.height));
				for (LWJGLTexture texture : byTexture.keySet()) {
					e.texture = texture;
					if (add(glyphId, e)) {
						break;
					}
					e.texture = null;
				}
				if (e.texture == null) {
					e.texture = Threads.waitFor(launcher.getTextureManager().createTexture(owner));
//					e.texture.setInternalFormat(LWJGLTextureFormat.ALPHA);
					Threads.waitFor(e.texture.allocate(8, 8));
					byTexture.put(e.texture, new HashSet<>());
					add(glyphId, e);
				}
				return true;
			} finally {
				lock.unlock();
			}
		});
	}

	private boolean add(int glyphId, Entry e) throws GameException {
		try {
//			Thread.dumpStack();
			lock.lock();
			Rectangle textureBounds = new Rectangle(e.texture.getWidth(), e.texture.getHeight());
			Rectangle currentBounds = textureBounds;
			boolean glyphTooLarge = false;
			while (true) {
				if (findFit(e, currentBounds)) {
					break;
				}
				Rectangle newBounds = scaledBounds(currentBounds);
				if (!newBounds.equals(currentBounds)) {
					currentBounds = newBounds;
				} else {
					glyphTooLarge = true;
					break;
				}
			}
			if (!textureBounds.equals(currentBounds)) {
				Threads.waitFor(e.texture.resize(currentBounds.width, currentBounds.height));
			}
			if (glyphTooLarge) {
				if (byTexture.get(e.texture).isEmpty()) {
					logger.warnf("Glyph too large: ID: %s, CodePoint: %s, Scale: %s, Width: %s, Height: %s", glyphId,
							e.entry.key.codepoint, e.entry.key.scale, e.bounds.width, e.bounds.height);
				} else {
					return false;
				}
			}
			ResourceStream stream = new ResourceStream(null, false, new ByteBufferBackedInputStream(e.entry.buffer),
					null);
			Threads.waitFor(e.texture.uploadSubAsync(stream, e.bounds.x, e.bounds.y));
//			Threads.waitFor(
//					e.texture.uploadAsync(e.bounds.x, e.bounds.y, e.bounds.width, e.bounds.height, e.entry.buffer));
			byTexture.get(e.texture).add(e);
			glyphs.put(glyphId, e);
			return true;
		} finally {
			lock.unlock();
		}
	}

	private Rectangle scaledBounds(Rectangle textureBounds) {
		boolean same = textureBounds.width == textureBounds.height;
		int newWidth = same ? textureBounds.width * 2 : textureBounds.width;
		if (newWidth > maxTextureSize) {
			return textureBounds;
		}
		int newHeight = same ? textureBounds.height : textureBounds.height * 2;
		return new Rectangle(newWidth, newHeight);
	}

	private boolean findFit(Entry entry, Rectangle bounds) {
		Rectangle rect = entry.bounds;
		int ox = rect.x;
		int oy = rect.y;
		rect.y = 0;
		boolean found = false;
		Collection<Rectangle> check = byTexture.get(entry.texture)
				.stream()
				.map(e -> e.bounds)
				.collect(Collectors.toSet());
		Collection<Rectangle> remove = new HashSet<>();
		yl: for (; rect.y < bounds.height - rect.height; rect.y++) {
			rect.x = 0;
			for (Rectangle r : check) {
				if (r.y + r.height < rect.y) {
					remove.add(r);
				}
			}
			check.removeAll(remove);
			remove.clear();

			xl: for (; rect.x < bounds.width - rect.width; rect.x++) {
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
	public void cleanup() throws GameException {
		Threads.waitFor(owner.submit(() -> {
			lock.lock();
			for (LWJGLTexture texture : byTexture.keySet()) {
				texture.cleanup();
			}
			glyphs.clear();
			byTexture.clear();
			lock.unlock();
		}));
	}

	public static class Entry {

		private LWJGLTexture texture;

		private GlyphEntry entry;

		private Rectangle bounds;

		public Entry(LWJGLTexture texture, GlyphEntry entry, Rectangle bounds) {
			this.texture = texture;
			this.entry = entry;
			this.bounds = bounds;
		}

		public Rectangle getBounds() {
			return bounds;
		}

		public GlyphEntry getEntry() {
			return entry;
		}

		public LWJGLTexture getTexture() {
			return texture;
		}

	}

}
