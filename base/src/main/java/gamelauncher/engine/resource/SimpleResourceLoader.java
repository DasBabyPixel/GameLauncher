package gamelauncher.engine.resource;

import java.io.InputStream;

import gamelauncher.engine.GameException;
import gamelauncher.engine.file.Path;
import gamelauncher.engine.util.GameSupplier;

public class SimpleResourceLoader extends ResourceLoader {

	@Override
	protected boolean canLoadResource(Path path) throws GameException {
		return path.getFileSystem().exists(path);
	}

	@Override
	protected Resource loadResource(Path path) {
		String p = path.getPath();
		if (p.endsWith("/")) {
			return new SimpleResource(() -> null, path, true);
		}
		return new SimpleResource(() -> path.getFileSystem().createInputStream(path), path, false);
	}

	private static class SimpleResource implements Resource {

		private final GameSupplier<InputStream> sup;
		private final Path path;
		private final boolean directory;

		public SimpleResource(GameSupplier<InputStream> sup, Path path, boolean directory) {
			this.sup = sup;
			this.path = path;
			this.directory = directory;
		}

		@Override
		public ResourceStream newResourceStream() throws GameException {
			return new ResourceStream(path, directory, sup.get(), null);
		}
	}
}
