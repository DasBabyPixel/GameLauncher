package gamelauncher.engine.io.embed.url;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class EmbedURLConnection extends URLConnection {

	private final ClassLoader cl;

	protected EmbedURLConnection(URL url) {
		super(url);
		cl = Thread.currentThread().getContextClassLoader();
	}

	@Override
	public void connect() {
		if (!connected) {
			connected = true;
		}
	}

	@Override
	public long getContentLengthLong() {
		URL ourl = cl.getResource(url.getPath());
		try {
			URLConnection con = Objects.requireNonNull(ourl).openConnection();
			return con.getContentLengthLong();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return super.getContentLengthLong();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		try {
			return Files.newInputStream(Paths.get(url.toURI()));
		} catch (URISyntaxException ex) {
			throw new IOException(ex);
		}
	}
}
