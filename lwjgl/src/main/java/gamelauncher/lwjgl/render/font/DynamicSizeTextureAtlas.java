package gamelauncher.lwjgl.render.font;

import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.ByteBufferBackedInputStream;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.texture.LWJGLTexture;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static org.lwjgl.opengles.GLES20.GL_MAX_TEXTURE_SIZE;
import static org.lwjgl.opengles.GLES20.glGetInteger;

public class DynamicSizeTextureAtlas extends AbstractGameResource {

	final Map<LWJGLTexture, Collection<AtlasEntry>> byTexture = new HashMap<>();
	private final Logger logger = Logger.logger();
	private final Map<Integer, AtlasEntry> glyphs = new HashMap<>();
	private final Lock lock = new ReentrantLock(true);
	private final LWJGLGameLauncher launcher;
	private final ExecutorThread owner;
	volatile int maxTextureSize;
	private CompletableFuture<?> last = CompletableFuture.completedFuture(null);

	public DynamicSizeTextureAtlas(LWJGLGameLauncher launcher, ExecutorThread owner) {
		this.launcher = launcher;
		this.owner = owner;
		this.owner.submit(() -> {
			this.maxTextureSize = glGetInteger(GL_MAX_TEXTURE_SIZE);
		});
	}

	public AtlasEntry getGlyph(int id) {
		try {
			lock.lock();
			return glyphs.get(id);
		} finally {
			lock.unlock();
		}
	}

	public CompletableFuture<Void> removeGlyph(int glyphId) {
		return launcher.threads().cached.submit(() -> {
			try {
				lock.lock();
				AtlasEntry entry = glyphs.remove(glyphId);
				if (entry != null) {
					Collection<AtlasEntry> col = byTexture.get(entry.texture);
					col.remove(entry);
					if (col.isEmpty()) {
						byTexture.remove(entry.texture);
						entry.texture.cleanup();
					}
				} else {
					logger.error("Already cleaned up glyph " + glyphId);
					GameException ex = Threads.buildStacktrace();
					ex.initCause(new GameException());
					logger.error(ex);
				}
			} finally {
				lock.unlock();
			}
		});
	}

	public boolean addGlyph(int glyphId, GlyphEntry entry) {
		try {
			lock.lock();
			if (cleanedUp()) {
				return false;
			}
			if (glyphs.containsKey(glyphId)) {
				return true;
			}
			AtlasEntry e =
					new AtlasEntry(null, entry, new Rectangle(entry.data.width, entry.data.height));
			for (LWJGLTexture texture : byTexture.keySet()) {
				e.texture = texture;
				if (add(glyphId, e)) {
					break;
				}
				e.texture = null;
			}
			if (e.texture == null) {
				e.texture = launcher.textureManager().createTexture(owner);
				e.texture.allocate(64, 64);
				byTexture.put(e.texture, new HashSet<>());
				add(glyphId, e);
			}
			return true;
		} catch (GameException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	private boolean add(int glyphId, AtlasEntry e) throws GameException {
		try {
			//			Thread.dumpStack();
			lock.lock();
			Rectangle textureBounds = new Rectangle(e.texture.width().intValue(),
					e.texture.height().intValue());
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
				e.texture.resize(currentBounds.width, currentBounds.height);
			}
			if (glyphTooLarge) {
				if (byTexture.get(e.texture).isEmpty()) {
					logger.warnf(
							"Glyph too large: ID: %s, CodePoint: %s, Scale: %s, Width: %s, Height: %s",
							glyphId, e.entry.key.codepoint, e.entry.key.scale, e.bounds.width,
							e.bounds.height);
				} else {
					return false;
				}
			}
			ResourceStream stream =
					new ResourceStream(null, false, new ByteBufferBackedInputStream(e.entry.buffer),
							null);
			last = last.thenRunAsync(() -> {
				try {
					Threads.waitFor(e.texture.uploadSubAsync(stream, e.bounds.x, e.bounds.y)
							.thenRun(launcher.guiManager()::redraw));
//					e.texture.write();
				} catch (GameException ex) {
					throw new RuntimeException(ex);
				}
			});
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

	private boolean findFit(AtlasEntry entry, Rectangle bounds) {
		Rectangle rect = entry.bounds;
		rect = new Rectangle(rect.x, rect.y, rect.width + 2, rect.height + 2);
		rect.y = 0;
		boolean found = false;
		Collection<Rectangle> check = byTexture.get(entry.texture).stream().map(e -> e.bounds)
				.map(r -> new Rectangle(r.x - 1, r.y - 1, r.width + 2, r.height + 2))
				.collect(Collectors.toSet());
		Collection<Rectangle> remove = new HashSet<>();
		yl:
		for (; rect.y < bounds.height - rect.height; rect.y++) {
			rect.x = 0;
			for (Rectangle r : check) {
				if (r.y + r.height < rect.y) {
					remove.add(r);
				}
			}
			check.removeAll(remove);
			remove.clear();

			xl:
			for (; rect.x < bounds.width - rect.width; rect.x++) {
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
		if (found) {
			entry.bounds.x = rect.x + 1;
			entry.bounds.y = rect.y + 1;
		}
		return found;
	}

	@Override
	public void cleanup0() throws GameException {
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

}
