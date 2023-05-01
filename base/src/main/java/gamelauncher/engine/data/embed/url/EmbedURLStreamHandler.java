package gamelauncher.engine.data.embed.url;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class EmbedURLStreamHandler extends URLStreamHandler {

    @Override protected URLConnection openConnection(URL u) {
        return new EmbedURLConnection(u);
    }
}
