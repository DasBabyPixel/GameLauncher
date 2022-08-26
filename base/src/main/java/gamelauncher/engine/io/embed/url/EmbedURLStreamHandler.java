package gamelauncher.engine.io.embed.url;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

@SuppressWarnings("javadoc")
public class EmbedURLStreamHandler extends URLStreamHandler {

	@Override
	protected URLConnection openConnection(URL u) throws IOException {
		return new EmbedURLConnection(u);
	}
}
