package gamelauncher.engine.file.embed.url;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

public class EmbedURLConnection extends URLConnection {

	private final ClassLoader cl;

	protected EmbedURLConnection(URL url) {
		super(url);
		cl = Thread.currentThread().getContextClassLoader();
	}

	@Override
	public void connect() throws IOException {
		if (!connected) {
			connected = true;
		}
	}

	@Override
	public long getContentLengthLong() {
		URL ourl = cl.getResource(url.getPath());
		try {
			URLConnection con = ourl.openConnection();
			return con.getContentLengthLong();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return super.getContentLengthLong();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		try {
			InputStream in = Files.newInputStream(Paths.get(url.toURI()));
			return in;
		} catch (IOException ex) {
			throw ex;
		} catch (URISyntaxException ex) {
			throw new IOException(ex);
		}
	}
}
