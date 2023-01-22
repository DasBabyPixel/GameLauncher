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
import org.joml.Vector4i;

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

	private static boolean intersects(Vector4i v1, Vector4i v2) {
		int tw = v1.z;
		int th = v1.w;
		int rw = v2.z;
		int rh = v2.w;
		if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
			return false;
		}
		int tx = v1.x;
		int ty = v1.y;
		int rx = v2.x;
		int ry = v2.y;
		rw += rx;
		rh += ry;
		tw += tx;
		th += ty;
		//      overflow || intersect
		return ((rw < rx || rw > tx) && (rh < ry || rh > ty) && (tw < tx || tw > rx) && (th < ty
				|| th > ry));
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
			AtlasEntry e = new AtlasEntry(null, entry,
					new Vector4i(0, 0, entry.data.width, entry.data.height));
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
			Vector4i textureBounds =
					new Vector4i(0, 0, e.texture.width().intValue(), e.texture.height().intValue());
			Vector4i currentBounds = textureBounds;
			boolean glyphTooLarge = false;
			while (true) {
				if (findFit(e, currentBounds)) {
					break;
				}
				Vector4i newBounds = scaledBounds(currentBounds);
				if (!newBounds.equals(currentBounds)) {
					currentBounds = newBounds;
				} else {
					glyphTooLarge = true;
					break;
				}
			}
			if (!textureBounds.equals(currentBounds)) {
				e.texture.resize(currentBounds.z, currentBounds.w);
			}
			if (glyphTooLarge) {
				if (byTexture.get(e.texture).isEmpty()) {
					logger.warnf(
							"Glyph too large: ID: %s, CodePoint: %s, Scale: %s, Width: %s, Height: %s",
							glyphId, e.entry.key.codepoint, e.entry.key.scale, e.bounds.z,
							e.bounds.w);
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

	private Vector4i scaledBounds(Vector4i textureBounds) {
		boolean same = textureBounds.z == textureBounds.w;
		int newWidth = same ? textureBounds.z * 2 : textureBounds.z;
		if (newWidth > maxTextureSize) {
			return textureBounds;
		}
		int newHeight = same ? textureBounds.w : textureBounds.w * 2;
		return new Vector4i(0, 0, newWidth, newHeight);
	}

	private boolean findFit(AtlasEntry entry, Vector4i bounds) {
		Vector4i rect = entry.bounds;
		rect = new Vector4i(rect.x, rect.y, rect.z + 2, rect.w + 2);
		rect.y = 0;
		boolean found = false;
		Collection<Vector4i> check = byTexture.get(entry.texture).stream().map(e -> e.bounds)
				.map(r -> new Vector4i(r.x - 1, r.y - 1, r.z + 2, r.w + 2))
				.collect(Collectors.toSet());
		Collection<Vector4i> remove = new HashSet<>();
		yl:
		for (; rect.y < bounds.w - rect.w; rect.y++) {
			rect.x = 0;
			for (Vector4i r : check) {
				if (r.y + r.w < rect.y) {
					remove.add(r);
				}
			}
			check.removeAll(remove);
			remove.clear();

			xl:
			for (; rect.x < bounds.z - rect.z; rect.x++) {
				for (Vector4i r : check) {
					if (intersects(r, rect)) {
						rect.x = r.x + r.z - 1;
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
