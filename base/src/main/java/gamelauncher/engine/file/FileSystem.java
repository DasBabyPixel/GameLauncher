package gamelauncher.engine.file;

import gamelauncher.engine.GameException;

public interface FileSystem {

	Path getPath(String path);

	void createDirectories(Path path) throws GameException;

	boolean exists(Path path) throws GameException;

	void createFile(Path path) throws GameException;
	
	byte[] readAllBytes(Path path) throws GameException;
	
	void write(Path path, byte[] bytes) throws GameException;
	
	void move(Path path, Path to) throws GameException;

}
