package gamelauncher.engine.plugins;

import java.nio.file.spi.FileSystemProvider;

public class EmbedZipClassLoader {

	public EmbedZipClassLoader() {
		FileSystemProvider.installedProviders();
	}
	
}
