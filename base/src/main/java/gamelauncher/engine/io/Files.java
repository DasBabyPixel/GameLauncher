package gamelauncher.engine.io;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.util.GameException;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author DasBabyPixel
 */
@SuppressWarnings("NewApi")
public class Files {

    /**
     * @return a new {@link InputStream} for the given {@link Path}
     */
    @Api public static InputStream newInputStream(Path path, OpenOption... options) throws GameException {
        return work(() -> java.nio.file.Files.newInputStream(path, options));
    }

    /**
     * @return a new {@link OutputStream} for the given {@link Path}
     */
    @Api public static OutputStream newOutputStream(Path path, OpenOption... options) throws GameException {
        return work(() -> java.nio.file.Files.newOutputStream(path, options));
    }

    /**
     * @return a new {@link SeekableByteChannel} for the given {@link Path}
     */
    @Api
    public static SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws GameException {
        return work(() -> java.nio.file.Files.newByteChannel(path, options, attrs));
    }

    /**
     * @return a new {@link SeekableByteChannel} for the given {@link Path}
     */
    @Api public static SeekableByteChannel newByteChannel(Path path, OpenOption... options) throws GameException {
        return work(() -> java.nio.file.Files.newByteChannel(path, options));
    }

    /**
     * @return a new {@link DirectoryStream} for the given {@link Path}
     */
    @Api public static DirectoryStream<Path> newDirectoryStream(Path dir) throws GameException {
        return work(() -> java.nio.file.Files.newDirectoryStream(dir));
    }

    /**
     * Create a file
     *
     * @return the file
     */
    @Api public static Path createFile(Path path, FileAttribute<?>... attrs) throws GameException {
        return work(() -> java.nio.file.Files.createFile(path, attrs));
    }

    /**
     * Creates a directory
     *
     * @return the directory
     */
    @Api public static Path createDirectory(Path path, FileAttribute<?>... attrs) throws GameException {
        return work(() -> java.nio.file.Files.createDirectory(path, attrs));
    }

    /**
     * Creates all directory
     *
     * @return the directory
     */
    @Api public static Path createDirectories(Path path, FileAttribute<?>... attrs) throws GameException {
        return work(() -> java.nio.file.Files.createDirectories(path, attrs));
    }

    /**
     * Deletes a {@link Path}
     */
    @Api public static void delete(Path path) throws GameException {
        work(() -> java.nio.file.Files.delete(path));
    }

    /**
     * Copies a file from one {@link Path} to another
     *
     * @return the target path
     */
    @Api public static Path copy(Path source, Path target, CopyOption... options) throws GameException {
        return work(() -> java.nio.file.Files.copy(source, target, options));
    }

    /**
     * Moves a file from one {@link Path} to another
     *
     * @return the target path
     */
    @Api public static Path move(Path source, Path target, CopyOption... options) throws GameException {
        return work(() -> java.nio.file.Files.move(source, target, options));
    }

    /**
     * @return true if the {@link Path} is a directory
     */
    @Api public static boolean isDirectory(Path path, LinkOption... options) throws GameException {
        return work(() -> java.nio.file.Files.isDirectory(path, options));
    }

    /**
     * @return true if a {@link Path} is a file
     */
    @Api public static boolean isRegularFile(Path path, LinkOption... options) throws GameException {
        return work(() -> java.nio.file.Files.isRegularFile(path, options));
    }

    /**
     * @return the size of a file
     */
    @Api public static long size(Path path) throws GameException {
        return work(() -> java.nio.file.Files.size(path));
    }

    /**
     * @return true if the given {@link Path} exists
     */
    @Api public static boolean exists(Path path, LinkOption... options) throws GameException {
        return work(() -> java.nio.file.Files.exists(path, options));
    }

    /**
     * @return all bytes of the {@link Path}
     */
    @Api public static byte[] readAllBytes(Path path) throws GameException {
        return work(() -> java.nio.file.Files.readAllBytes(path));
    }

    /**
     * @return all lines of the given {@link Path}
     */
    @Api public static List<String> readAllLines(Path path, Charset cs) throws GameException {
        return work(() -> java.nio.file.Files.readAllLines(path, cs));
    }

    /**
     * Writes to a path
     *
     * @return the path
     */
    @Api public static Path write(Path path, byte[] bytes, OpenOption... options) throws GameException {
        return work(() -> java.nio.file.Files.write(path, bytes, options));
    }

    /**
     * Walks through a file tree
     *
     * @return the fileTree, lazily populated
     */
    @Api public static Stream<Path> walk(Path path, FileVisitOption... options) throws GameException {
        return work(() -> java.nio.file.Files.walk(path, options));
    }

    /**
     * @return a UTF-8 {@link String} of the {@link Path}'s bytes
     */
    @Api public static String readUTF8(Path path) throws GameException {
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

    private interface ExceptionRunnable {
        void run() throws Exception;
    }

    private interface ExceptionCallable<T> {
        T get() throws Exception;
    }
}
