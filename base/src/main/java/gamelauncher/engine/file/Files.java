package gamelauncher.engine.file;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 *
 */
public class Files {

	/**
	 * @param path
	 * @param options
	 * @return a new {@link InputStream} for the given {@link Path}
	 * @throws GameException
	 */
	public static InputStream newInputStream(Path path, OpenOption... options) throws GameException {
		return work(() -> java.nio.file.Files.newInputStream(path, options));
	}

	/**
	 * @param path
	 * @param options
	 * @return a new {@link OutputStream} for the given {@link Path}
	 * @throws GameException
	 */
	public static OutputStream newOutputStream(Path path, OpenOption... options) throws GameException {
		return work(() -> java.nio.file.Files.newOutputStream(path, options));
	}

	/**
	 * @param path
	 * @param options
	 * @param attrs
	 * @return a new {@link SeekableByteChannel} for the given {@link Path}
	 * @throws GameException
	 */
	public static SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options,
			FileAttribute<?>... attrs) throws GameException {
		return work(() -> java.nio.file.Files.newByteChannel(path, options, attrs));
	}

	/**
	 * @param path
	 * @param options
	 * @return a new {@link SeekableByteChannel} for the given {@link Path}
	 * @throws GameException
	 */
	public static SeekableByteChannel newByteChannel(Path path, OpenOption... options) throws GameException {
		return work(() -> java.nio.file.Files.newByteChannel(path, options));
	}

	/**
	 * @param dir
	 * @return a new {@link DirectoryStream} for the given {@link Path}
	 * @throws GameException
	 */
	public static DirectoryStream<Path> newDirectoryStream(Path dir) throws GameException {
		return work(() -> java.nio.file.Files.newDirectoryStream(dir));
	}

	/**
	 * Create a file
	 * 
	 * @param path
	 * @param attrs
	 * @return the file
	 * @throws GameException
	 */
	public static Path createFile(Path path, FileAttribute<?>... attrs) throws GameException {
		return work(() -> java.nio.file.Files.createFile(path, attrs));
	}

	/**
	 * Creates a directory
	 * 
	 * @param path
	 * @param attrs
	 * @return the directory
	 * @throws GameException
	 */
	public static Path createDirectory(Path path, FileAttribute<?>... attrs) throws GameException {
		return work(() -> java.nio.file.Files.createDirectory(path, attrs));
	}

	/**
	 * Creates all directory
	 * 
	 * @param path
	 * @param attrs
	 * @return the directory
	 * @throws GameException
	 */
	public static Path createDirectories(Path path, FileAttribute<?>... attrs) throws GameException {
		return work(() -> java.nio.file.Files.createDirectories(path, attrs));
	}

	/**
	 * Deletes a {@link Path}
	 * 
	 * @param path
	 * @throws GameException
	 */
	public static void delete(Path path) throws GameException {
		work(() -> java.nio.file.Files.delete(path));
	}

	/**
	 * Copies a file from one {@link Path} to another
	 * 
	 * @param source
	 * @param target
	 * @param options
	 * @return the target path
	 * @throws GameException
	 */
	public static Path copy(Path source, Path target, CopyOption... options) throws GameException {
		return work(() -> java.nio.file.Files.copy(source, target, options));
	}

	/**
	 * Moves a file from one {@link Path} to another
	 * 
	 * @param source
	 * @param target
	 * @param options
	 * @return the target path
	 * @throws GameException
	 */
	public static Path move(Path source, Path target, CopyOption... options) throws GameException {
		return work(() -> java.nio.file.Files.move(source, target, options));
	}

	/**
	 * @param path
	 * @param options
	 * @return true if the {@link Path} is a directory
	 * @throws GameException
	 */
	public static boolean isDirectory(Path path, LinkOption... options) throws GameException {
		return work(() -> java.nio.file.Files.isDirectory(path, options));
	}

	/**
	 * @param path
	 * @param options
	 * @return true if a {@link Path} is a file
	 * @throws GameException
	 */
	public static boolean isRegularFile(Path path, LinkOption... options) throws GameException {
		return work(() -> java.nio.file.Files.isRegularFile(path, options));
	}

	/**
	 * @param path
	 * @return the size of a file
	 * @throws GameException
	 */
	public static long size(Path path) throws GameException {
		return work(() -> java.nio.file.Files.size(path));
	}

	/**
	 * @param path
	 * @param options
	 * @return true if the given {@link Path} exists
	 * @throws GameException
	 */
	public static boolean exists(Path path, LinkOption... options) throws GameException {
		return work(() -> java.nio.file.Files.exists(path, options));
	}

	/**
	 * @param path
	 * @return all bytes of the {@link Path}
	 * @throws GameException
	 */
	public static byte[] readAllBytes(Path path) throws GameException {
		return work(() -> java.nio.file.Files.readAllBytes(path));
	}

	/**
	 * @param path
	 * @param cs
	 * @return all lines of the given {@link Path}
	 * @throws GameException
	 */
	public static List<String> readAllLines(Path path, Charset cs) throws GameException {
		return work(() -> java.nio.file.Files.readAllLines(path, cs));
	}

	/**
	 * Writes to a path
	 * 
	 * @param path
	 * @param bytes
	 * @param options
	 * @return the path
	 * @throws GameException
	 */
	public static Path write(Path path, byte[] bytes, OpenOption... options) throws GameException {
		return work(() -> java.nio.file.Files.write(path, bytes, options));
	}

	/**
	 * Walks through a file tree
	 * 
	 * @param path
	 * @param options
	 * @return the fileTree, lazily populated
	 * @throws GameException
	 */
	public static Stream<Path> walk(Path path, FileVisitOption... options) throws GameException {
		return work(() -> java.nio.file.Files.walk(path, options));
	}

	/**
	 * @param path
	 * @return a UTF-8 {@link String} of the {@link Path}'s bytes
	 * @throws GameException
	 */
	public static String readUTF8(Path path) throws GameException {
		return new String(readAllBytes(path), StandardCharsets.UTF_8);
	}

	private static void work(ExceptionRunnable run) throws GameException {
		try {
			run.run();
		} catch (Exception ex) {
			throw new GameException(ex);
		}
	}

	private static <T> T work(ExceptionCallable<T> callable) throws GameException {
		try {
			return callable.get();
		} catch (Exception ex) {
			throw new GameException(ex);
		}
	}

	private static interface ExceptionRunnable {
		void run() throws Exception;
	}

	private static interface ExceptionCallable<T> {
		T get() throws Exception;
	}
}
