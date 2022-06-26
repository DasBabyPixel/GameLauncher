package gamelauncher.engine.file.embed;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;

@SuppressWarnings("javadoc")
public class EmbedFileStore extends FileStore {

	private final EmbedFileSystem efs;

	public EmbedFileStore(EmbedFileSystem efs) {
		this.efs = efs;
	}

	@Override
	public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> type) {
		if (type == null) {
			throw new NullPointerException();
		}
		return null;
	}

	@Override
	public Object getAttribute(String attribute) throws IOException {
		if (attribute.equals("totalSpace")) {
			return this.getTotalSpace();
		} else if (attribute.equals("usableSpace")) {
			return this.getUsableSpace();
		} else if (attribute.equals("unallocatedSpace")) {
			return this.getUnallocatedSpace();
		} else {
			throw new UnsupportedOperationException("does not support the given attribute");
		}
	}

	@Override
	public String name() {
		return efs.getEmbedPath().toString() + "/";
	}

	@Override
	public String type() {
		return "embedfs";
	}

	@Override
	public boolean supportsFileAttributeView(Class<? extends FileAttributeView> type) {
		return type == BasicFileAttributeView.class || type == EmbedFileAttributeView.class;
	}

	@Override
	public boolean supportsFileAttributeView(String name) {
		return name.equals("basic") || name.equals("embed");
	}

	@Override
	public boolean isReadOnly() {
		return efs.isReadOnly();
	}

	@Override
	public long getTotalSpace() throws IOException {
		return 0;
	}

	@Override
	public long getUsableSpace() throws IOException {
		return 0;
	}

	@Override
	public long getUnallocatedSpace() throws IOException {
		return 0;
	}

}
