package gamelauncher.lwjgl.render.font.deprecated;

import static org.lwjgl.opengles.GLES20.*;

import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.lwjgl.LWJGLGameLauncher;

@SuppressWarnings("javadoc")
@Deprecated
public class DynamicSizeTextureAtlasArray extends TextureAtlas {

	public final Collection<DynamicSizeTextureAtlas> atlases = new CopyOnWriteArrayList<>();
	public final Map<Integer, DynamicSizeTextureAtlas> map = new ConcurrentHashMap<>();
	private final Random random = new Random(163478519);
	private final ExecutorThread owner;
	private final LWJGLGameLauncher launcher;

	public DynamicSizeTextureAtlasArray(LWJGLGameLauncher launcher, ExecutorThread owner) {
		this.launcher = launcher;
		this.owner = owner;
	}

	@Override
	public CompletableFuture<Boolean> addGlyph(int id, GlyphEntry glyph) {
		CompletableFuture<Boolean> fut = new CompletableFuture<>();
		launcher.getThreads().cached.execute(() -> {
			try {
				if (map.containsKey(id)) {
					fut.complete(true);
					return;
				}
				for (DynamicSizeTextureAtlas a : atlases) {
					if (Threads.waitFor(a.addGlyph(id, glyph))) {
						map.put(id, a);
						Threads.waitFor(super.addGlyph(id, glyph));
						fut.complete(true);
						return;
					}
				}
				DynamicSizeTextureAtlas a = new DynamicSizeTextureAtlas(launcher, owner,
						Threads.waitFor(owner.submit(() -> glGetInteger(GL_MAX_TEXTURE_SIZE))), 8,
						atlas -> Integer.toString(random.nextInt()));
				if (!Threads.waitFor(a.addGlyph(id, glyph))) {
					fut.complete(false);
					return;
				}
				atlases.add(a);
				map.put(id, a);
				Threads.waitFor(super.addGlyph(id, glyph));
				fut.complete(true);
			} catch (GameException ex) {
				fut.completeExceptionally(ex);
				launcher.handleError(ex);
			}
		});
		return fut;
	}

	@Override
	public void removeGlyph(int id) throws GameException {
		DynamicSizeTextureAtlas a = map.remove(id);
		a.removeGlyph(id);
		if (a.isEmpty()) {
			atlases.remove(a);
			a.cleanup();
		}
		super.removeGlyph(id);
	}

	@Override
	public void cleanup() throws GameException {
		for (DynamicSizeTextureAtlas atlas : atlases) {
			atlas.cleanup();
		}
	}
}
