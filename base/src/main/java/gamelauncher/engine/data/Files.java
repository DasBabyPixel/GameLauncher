package gamelauncher.engine.data;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.util.GameException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author DasBabyPixel
 */
public class Files {
    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;
    // buffer size used for reading and writing
    private static final int BUFFER_SIZE = 8192;

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
    @Api public static SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws GameException {
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
     * @return the size of a file. Will be -1 if the size is not known
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
        try (SeekableByteChannel sbc = newByteChannel(path)) {
            InputStream in = Channels.newInputStream(sbc);
            long size = sbc.size();
            if (size > (long) MAX_BUFFER_SIZE) throw new OutOfMemoryError("Required array size too large");
            return read(in, (int) size);
        } catch (IOException e) {
            throw new GameException(e);
        }
    }

    private static byte[] read(InputStream source, int initialSize) throws IOException {
        int capacity = Math.max(0, initialSize);
        byte[] buf = new byte[capacity];
        int nread = 0;
        int n;
        for (; ; ) {
            // read to EOF which may read more or less than initialSize (eg: file
            // is truncated while we are reading)
            while ((n = source.read(buf, nread, capacity - nread)) > 0) nread += n;

            // if last call to source.read() returned -1, we are done
            // otherwise, try to read one more byte; if that failed we're done too
            if (n < 0 || (n = source.read()) < 0) break;

            // one more byte was read; need to allocate a larger buffer
            if (capacity <= MAX_BUFFER_SIZE - capacity) {
                capacity = Math.max(capacity << 1, BUFFER_SIZE);
            } else {
                if (capacity == MAX_BUFFER_SIZE) throw new OutOfMemoryError("Required array size too large");
                capacity = MAX_BUFFER_SIZE;
            }
            buf = Arrays.copyOf(buf, capacity);
            buf[nread++] = (byte) n;
        }
        return (capacity == nread) ? buf : Arrays.copyOf(buf, nread);
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
