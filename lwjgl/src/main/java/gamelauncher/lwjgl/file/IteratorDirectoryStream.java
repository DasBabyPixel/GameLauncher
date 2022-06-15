package gamelauncher.lwjgl.file;

import java.util.Iterator;

import gamelauncher.engine.file.DirectoryStream;
import gamelauncher.engine.file.Path;

public class IteratorDirectoryStream implements DirectoryStream {

	public final Iterator<Path> iterator;
	
	public IteratorDirectoryStream(Iterator<Path> iterator) {
		this.iterator = iterator;
	}

	@Override
	public Iterator<Path> iterator() {
		return iterator;
	}
}
