package gamelauncher.lwjgl.render.texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import de.matthiasmann.twl.utils.PNGDecoder;
import gamelauncher.engine.render.texture.Texture;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.function.GameRunnable;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.lwjgl.render.GlStates;

@SuppressWarnings("javadoc")
public class LWJGLTexture implements Texture {

	private final LWJGLTextureManager manager;
	private final int textureId;
	public int width;
	public int height;
	public final Logger logger = Logger.getLogger();

//	public LWJGLTexture(BufferedImage img) {
//		textureId = glGenTextures();
//		bind();
//		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
//
//		ByteBuffer buf = memAlloc(4 * img.getWidth() * img.getHeight());
//		int[] rgb = new int[img.getWidth() * img.getHeight()];
//		img.getRGB(0, 0, img.getWidth(), img.getHeight(), rgb, 0, img.getWidth());
//		buf.asIntBuffer().put(rgb);
//		buf.flip();
//		this.width = img.getWidth();
//		this.height = img.getHeight();
//		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, img.getWidth(), img.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
//		memFree(buf);
//		glGenerateMipmap(GL_TEXTURE_2D);
//		unbind();
//	}

	LWJGLTexture(LWJGLTextureManager manager) {
		this(manager, glGenTextures());
	}

	public LWJGLTexture(LWJGLTextureManager manager, int textureId) {
		this.manager = manager;
		this.textureId = textureId;
	}

	@Override
	public BufferedImage getBufferedImage() {
		ByteBuffer pixels = getBufferedImageBuffer();
		IntBuffer ipixels = pixels.asIntBuffer();
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				img.setRGB(x, y, ipixels.get(y * width + x));
			}
		}
		memFree(pixels);
		return img;
	}

	public ByteBuffer getBufferedImageBuffer() {
		ByteBuffer pixels = memAlloc(4 * width * height);
		bind();
		glGetTexImage(GL_TEXTURE_2D, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
		return pixels;
	}

	@Override
	public void allocate(int width, int height) {
		bind();
		this.width = width;
		this.height = height;
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
	}

	public void bind() {
		GlStates.bindTexture(GL_TEXTURE_2D, textureId);
	}

	public void unbind() {
		GlStates.bindTexture(GL_TEXTURE_2D, 0);
	}

	@Override
	public void cleanup() throws GameException {
		GlStates.deleteTextures(textureId);
	}

	public int getTextureId() {
		return textureId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(textureId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LWJGLTexture other = (LWJGLTexture) obj;
		return textureId == other.textureId;
	}

	@Override
	public CompletableFuture<Void> uploadAsync(ResourceStream stream) throws GameException {
		final int buf = glGenBuffers();
		CompletableFuture<Void> fut = new CompletableFuture<>();

		AtomicLong nanos = new AtomicLong();
		manager.service.submit(() -> {
			try {
				PNGDecoder decoder = stream.newPNGDecoder();
				int w = decoder.getWidth();
				int h = decoder.getHeight();
				System.out.printf("%s %s%n", w, h);
				AtomicReference<ByteBuffer> abuf = new AtomicReference<>();
				CompletableFuture<Void> fut2 = manager.launcher.getWindow().renderLater(() -> {
					long t1 = System.nanoTime();
					GlStates.bindBuffer(GL_PIXEL_UNPACK_BUFFER, buf);
					glBufferData(GL_PIXEL_UNPACK_BUFFER, w * h * Integer.BYTES, GL_STATIC_DRAW);
					ByteBuffer buf1 = glMapBuffer(GL_PIXEL_UNPACK_BUFFER, GL_WRITE_ONLY);
					GlStates.bindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);
					if (buf1 == null) {
						int error = glGetError();
						throw new GameException("Couldn't map buffer! (" + Integer.toHexString(error) + ")");
					}
					abuf.set(buf1);
					nanos.addAndGet(System.nanoTime() - t1);
				});
				Threads.waitFor(fut2);
				decoder.decode(abuf.get(), w * Integer.BYTES, PNGDecoder.Format.RGBA);
				stream.cleanup();
				abuf.get().flip();
				CompletableFuture<Void> fut3 = manager.launcher.getWindow().renderLater(new GameRunnable() {
					@Override
					public void run() {
						long t1 = System.nanoTime();
						GlStates.bindBuffer(GL_PIXEL_UNPACK_BUFFER, buf);
						glUnmapBuffer(GL_PIXEL_UNPACK_BUFFER);
						bind();
						glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
						glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
						glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
						nanos.addAndGet(System.nanoTime() - t1);
						GlStates.bindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);
						glDeleteBuffers(buf);
						fut.complete(null);
						System.out.println(
								"Took " + nanos.get() + "ns - " + TimeUnit.NANOSECONDS.toMillis(nanos.get()) + "ms");
					}
				});
				Threads.waitFor(fut3);
			} catch (Throwable ex) {
				manager.launcher.handleError(ex);
			}
		});
		Threads.waitFor(fut);
		return fut;
	}

	@Override
	public CompletableFuture<Void> uploadAsync(BufferedImage image) throws GameException {
		throw new UnsupportedOperationException();
	}

//	@Override
//	public void upload(BufferedImage image) {
//	}

//	@Override
//	public void upload(ResourceStream stream) throws GameException {
//		bind();
//		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
//		PNGDecoder decoder = stream.newPNGDecoder();
//		ByteBuffer buf = memAlloc(4 * decoder.getWidth() * decoder.getHeight());
//		try {
//			decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
//		} catch (IOException ex) {
//			throw new GameException(ex);
//		}
//		buf.flip();
//		this.width = decoder.getWidth();
//		this.height = decoder.getHeight();
//
//		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
//		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
//
//		long time1 = System.nanoTime();
//		int buf1 = glGenBuffers();
//		int buf2 = glGenBuffers();
//		GlStates.bindBuffer(GL_PIXEL_UNPACK_BUFFER, buf1);
//		glBufferData(GL_PIXEL_UNPACK_BUFFER, 4 * 4, GL_STATIC_DRAW);
//		ByteBuffer bb1 = glMapBuffer(GL_PIXEL_UNPACK_BUFFER, GL_READ_WRITE);
//		GlStates.bindBuffer(GL_PIXEL_UNPACK_BUFFER, buf1);
//		glBufferData(GL_PIXEL_UNPACK_BUFFER, 4 * 4, GL_STATIC_DRAW);
//		ByteBuffer bb2 = glMapBuffer(GL_PIXEL_UNPACK_BUFFER, GL_READ_WRITE);
//		long time2 = System.nanoTime();
//
//		GlStates.bindBuffer(GL_PIXEL_UNPACK_BUFFER, buf1);
//		glUnmapBuffer(GL_PIXEL_UNPACK_BUFFER);
//		GlStates.bindBuffer(GL_PIXEL_UNPACK_BUFFER, buf2);
//		glUnmapBuffer(GL_PIXEL_UNPACK_BUFFER);
//
//		GlStates.bindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);
//
//		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE,
//				buf);
//		memFree(buf);
//		glGenerateMipmap(GL_TEXTURE_2D);
//		stream.cleanup();
//		unbind();
//		long took = time2 - time1;
//		System.out.println("Took " + took + " nanos, " + TimeUnit.NANOSECONDS.toMillis(took) + "ms");
//	}
}
