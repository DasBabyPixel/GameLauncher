package gamelauncher.lwjgl.render.font;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.font.Font;
import gamelauncher.engine.render.font.FontFactory;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.GameException;

public class BasicFontFactory implements FontFactory {

	private final GameLauncher launcher;

	final Map<Path, BasicFont> fonts = new ConcurrentHashMap<>();

	public BasicFontFactory(GameLauncher launcher) {
		this.launcher = launcher;
	}

	@Override
	public Font createFont(ResourceStream stream) throws GameException {
		Path path = stream.getPath();
		BasicFont font;
		if (path == null) {
			font = new BasicFont(this, this.launcher, stream);
		} else {
			font = this.fonts.compute(path, (p, f) -> {
				if (f == null) {
					return new BasicFont(this, this.launcher, stream);
				}
				try {
					stream.cleanup();
				} catch (GameException ex) {
					this.launcher.handleError(ex);
				}
				return f;
			});
		}
		font.lock.lock();
		font.refcount.incrementAndGet();
		font.lock.unlock();
		return font;
	}

}
