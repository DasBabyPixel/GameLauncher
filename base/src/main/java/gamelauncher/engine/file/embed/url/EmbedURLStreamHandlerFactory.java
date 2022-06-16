package gamelauncher.engine.file.embed.url;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class EmbedURLStreamHandlerFactory implements URLStreamHandlerFactory {

	@Override
	public URLStreamHandler createURLStreamHandler(String protocol) {
		if ("embed".equals(protocol)) {
			return new EmbedURLStreamHandler();
		}
		return null;
	}
}
