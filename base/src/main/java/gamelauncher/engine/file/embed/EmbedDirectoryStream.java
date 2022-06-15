package gamelauncher.engine.file.embed;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Iterator;

public class EmbedDirectoryStream implements DirectoryStream<Path> {

	@Override
	public void close() throws IOException {
		
	}

	@Override
	public Iterator<Path> iterator() {
		return null;
	}
}
