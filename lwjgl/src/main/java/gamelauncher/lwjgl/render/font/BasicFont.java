package gamelauncher.lwjgl.render.font;

import static org.lwjgl.system.MemoryUtil.*;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.font.Font;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.concurrent.Threads;

@SuppressWarnings("javadoc")
public class BasicFont implements Font {

	private volatile boolean done = false;

	private final CompletableFuture<Void> future;

	final AtomicInteger refcount = new AtomicInteger();

	private final Path path;

	private volatile ByteBuffer data;

	private final BasicFontFactory factory;

	final Lock lock = new ReentrantLock(true);

	BasicFont(BasicFontFactory factory, GameLauncher launcher, ResourceStream stream) {
		this.path = stream.getPath();
		this.factory = factory;
		future = launcher.getThreads().cached.submit(() -> {
			byte[] b = stream.readAllBytes();
			data = memAlloc(b.length);
			data.put(b).flip();
			done = true;
		});
	}

	@Override
	public ByteBuffer data() {
		if (!done) {
			Threads.waitFor(future);
		}
		return data;
	}

	@Override
	public void cleanup() {
		try {
			lock.lock();
			if (refcount.decrementAndGet() <= 0) {
				if (path != null) {
					factory.fonts.remove(path);
				}
				if (!done) {
					Threads.waitFor(future);
				}
				memFree(data);
			}
		} finally {
			lock.unlock();
		}
	}

}
