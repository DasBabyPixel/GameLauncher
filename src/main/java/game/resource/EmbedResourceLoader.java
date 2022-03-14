package game.resource;

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
		return new EmbedResource(() -> cl.getResourceAsStream(path.getPath()));
	}

	private static class EmbedResource implements Resource {

		private final Supplier<InputStream> sup;

		public EmbedResource(Supplier<InputStream> sup) {
			this.sup = sup;
		}

		@Override
		public ResourceStream newResourceStream() {
			return new ResourceStream(sup.get(), null);
		}
	}
}
