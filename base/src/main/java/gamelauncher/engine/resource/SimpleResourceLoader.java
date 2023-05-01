package gamelauncher.engine.resource;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.data.Files;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameSupplier;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * @author DasBabyPixel
 */
public class SimpleResourceLoader extends ResourceLoader {

    public SimpleResourceLoader(GameLauncher launcher) {
        super(launcher);
    }

    @Override protected boolean canLoadResource(Path path) throws GameException {
        return Files.exists(path);
    }

    @Override protected Resource loadResource(Path path) throws GameException {
        boolean directory = Files.isDirectory(path);
        if (directory) {
            return new SimpleResource(() -> null, path, true);
        }
        return new SimpleResource(() -> Files.newInputStream(path), path, false);
    }

    private static class SimpleResource extends AbstractGameResource implements Resource {

        private final GameSupplier<InputStream> sup;
        private final Path path;
        private final boolean directory;

        public SimpleResource(GameSupplier<InputStream> sup, Path path, boolean directory) {
            this.sup = sup;
            this.path = path;
            this.directory = directory;
        }

        @Override public ResourceStream newResourceStream() throws GameException {
            return new ResourceStream(path, directory, sup.get(), null);
        }

        @Override protected void cleanup0() throws GameException {
        }
    }
}
