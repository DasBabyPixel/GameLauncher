package gamelauncher.engine.file;

public interface Path {

	public static final String SEPERATOR = "/";

	Path resolve(String path);

	Path getParent();
	
	FileSystem getFileSystem();

	String getPath();

}
