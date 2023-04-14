package gamelauncher.gles.font.bitmap;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.font.Font;
import gamelauncher.engine.render.font.FontFactory;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.GameException;
import gamelauncher.gles.GLES;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BasicFontFactory implements FontFactory {

    final Map<Path, BasicFont> fonts = new ConcurrentHashMap<>();
    private final GameLauncher launcher;
    private final GLES gles;

    public BasicFontFactory(GLES gles, GameLauncher launcher) {
        this.gles = gles;
        this.launcher = launcher;
    }

    @Override
    public Font createFont(ResourceStream stream, boolean close) {
        Path path = stream.getPath();
        BasicFont font;
        if (path == null) {
            font = new BasicFont(gles, this, this.launcher, stream);
        } else {
            font = this.fonts.compute(path, (p, f) -> {
                if (f == null) {
                    return new BasicFont(gles, this, this.launcher, stream);
                }
                if (close) {
                    try {
                        stream.cleanup();
                    } catch (GameException ex) {
                        this.launcher.handleError(ex);
                    }
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
