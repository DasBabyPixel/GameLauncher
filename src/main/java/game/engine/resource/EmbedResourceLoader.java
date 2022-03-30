package game.engine.resource;

import java.io.InputStream;
import java.util.function.Supplier;

public class EmbedResourceLoader extends ResourceLoader {

	private final ClassLoader cl;

	public EmbedResourceLoader() {
		cl = getClass().getClassLoader();
	}

	@Override
	protected boolean canLoadResource(ResourcePath path) {
		return cl.getResource(path.getPath()) != null;
	}

	@Override
	protected Resource loadResource(ResourcePath path) {
		if (path.getPath().endsWith("/")) {
			return new EmbedResource(() -> null, path, true);
		}
		return new EmbedResource(() -> cl.getResourceAsStream(path.getPath()), path, false);
	}

	private static class EmbedResource implements Resource {

		private final Supplier<InputStream> sup;
		private final ResourcePath path;
		private final boolean directory;

		public EmbedResource(Supplier<InputStream> sup, ResourcePath path, boolean directory) {
			this.sup = sup;
			this.path = path;
			this.directory = directory;
		}

		@Override
		public ResourceStream newResourceStream() {
			return new ResourceStream(path, directory, sup.get(), null);
		}
	}
}
