package gamelauncher.lwjgl.render.font;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.lwjgl.system.MemoryUtil;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.font.Font;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;

public class BasicFont extends AbstractGameResource implements Font {

	private volatile boolean done = false;

	private final CompletableFuture<Void> future;

	final AtomicInteger refcount = new AtomicInteger(0);

	private final Path path;

	private volatile ByteBuffer data;

	private final BasicFontFactory factory;

	final Lock lock = new ReentrantLock(true);

	BasicFont(BasicFontFactory factory, GameLauncher launcher, ResourceStream stream) {
		launcher.getLogger().info("create font " + stream);
		this.path = stream.getPath();
		this.factory = factory;
		this.future = launcher.getThreads().cached.submit(() -> {
			launcher.getLogger().info("cleanup fontstream " + stream);
			byte[] b = stream.readAllBytes();
			stream.cleanup();
			this.data = MemoryUtil.memAlloc(b.length);
			this.data.put(b).flip();
			this.done = true;
		});
	}

	@Override
	public ByteBuffer data() throws GameException {
		if (!this.done) {
			Threads.waitFor(this.future);
		}
		return this.data;
	}

	@Override
	public boolean isCleanedUp() {
		return this.refcount.get() == 0;
	}

	@Override
	public void cleanup0() throws GameException {
		try {
			this.lock.lock();
			if (this.refcount.decrementAndGet() <= 0) {
				if (this.path != null) {
					this.factory.fonts.remove(this.path);
				}
				if (!this.done) {
					Threads.waitFor(this.future);
				}
				MemoryUtil.memFree(this.data);
			}
		} finally {
			this.lock.unlock();
		}
	}

}
