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

import gamelauncher.engine.GameException;

public class Files {

	public static InputStream newInputStream(Path path, OpenOption... options) throws GameException {
		return work(() -> java.nio.file.Files.newInputStream(path, options));
	}

	public static OutputStream newOutputStream(Path path, OpenOption... options) throws GameException {
		return work(() -> java.nio.file.Files.newOutputStream(path, options));
	}

	public static SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options,
			FileAttribute<?>... attrs) throws GameException {
		return work(() -> java.nio.file.Files.newByteChannel(path, options, attrs));
	}

	public static SeekableByteChannel newByteChannel(Path path, OpenOption... options) throws GameException {
		return work(() -> java.nio.file.Files.newByteChannel(path, options));
	}

	public static DirectoryStream<Path> newDirectoryStream(Path dir) throws GameException {
		return work(() -> java.nio.file.Files.newDirectoryStream(dir));
	}

	public static Path createFile(Path path, FileAttribute<?>... attrs) throws GameException {
		return work(() -> java.nio.file.Files.createFile(path, attrs));
	}

	public static Path createDirectory(Path path, FileAttribute<?>... attrs) throws GameException {
		return work(() -> java.nio.file.Files.createDirectory(path, attrs));
	}

	public static Path createDirectories(Path path, FileAttribute<?>... attrs) throws GameException {
		return work(() -> java.nio.file.Files.createDirectories(path, attrs));
	}

	public static void delete(Path path) throws GameException {
		work(() -> java.nio.file.Files.delete(path));
	}

	public static Path copy(Path source, Path target, CopyOption... options) throws GameException {
		return work(() -> java.nio.file.Files.copy(source, target, options));
	}

	public static Path move(Path source, Path target, CopyOption... options) throws GameException {
		return work(() -> java.nio.file.Files.move(source, target, options));
	}

	public static boolean isDirectory(Path path, LinkOption... options) throws GameException {
		return work(() -> java.nio.file.Files.isDirectory(path, options));
	}

	public static boolean isRegularFile(Path path, LinkOption... options) throws GameException {
		return work(() -> java.nio.file.Files.isRegularFile(path, options));
	}

	public static long size(Path path) throws GameException {
		return work(() -> java.nio.file.Files.size(path));
	}

	public static boolean exists(Path path, LinkOption... options) throws GameException {
		return work(() -> java.nio.file.Files.exists(path, options));
	}

	public static byte[] readAllBytes(Path path) throws GameException {
		return work(() -> java.nio.file.Files.readAllBytes(path));
	}

	public static List<String> readAllLines(Path path, Charset cs) throws GameException {
		return work(() -> java.nio.file.Files.readAllLines(path, cs));
	}

	public static Path write(Path path, byte[] bytes, OpenOption... options) throws GameException {
		return work(() -> java.nio.file.Files.write(path, bytes, options));
	}

	public static Stream<Path> walk(Path path, FileVisitOption... options) throws GameException {
		return work(() -> java.nio.file.Files.walk(path, options));
	}

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
