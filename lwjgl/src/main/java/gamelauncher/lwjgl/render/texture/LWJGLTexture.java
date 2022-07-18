package gamelauncher.lwjgl.render.texture;

import static org.lwjgl.opengles.GLES20.*;
import static org.lwjgl.opengles.GLES30.*;
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
import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.function.GameRunnable;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.lwjgl.render.states.GlStates;

@SuppressWarnings("javadoc")
public class LWJGLTexture implements Texture {

	private final LWJGLTextureManager manager;
	private int textureId;
	public int width;
	public int height;
	private final ExecutorThread owner;
	public final Logger logger = Logger.getLogger();

//	LWJGLTexture(LWJGLTextureManager manager, ExecutorThread owner) {
//		this(manager, owner, -1);
//	}
//
	@Deprecated
	public LWJGLTexture(LWJGLTextureManager manager, ExecutorThread owner, int textureId) {
		this.manager = manager;
		this.owner = owner;
		this.textureId = textureId;
		if (this.textureId == -1) {
			submit(() -> LWJGLTexture.this.textureId = glGenTextures());
		}
	}

	private LWJGLTexture(LWJGLTextureManager man, ExecutorThread owner, CompletableFuture<LWJGLTexture> f) {
		this.owner = owner;
		this.manager = man;
		submit(() -> textureId = glGenTextures()).thenRun(() -> {
			f.complete(this);
		});
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
		int fbo = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.getTextureId(), 0);
		glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glDeleteFramebuffers(fbo);
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
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
	}

	public void bind() {
		GlStates.current().bindTexture(GL_TEXTURE_2D, getTextureId());
	}

	public void unbind() {
		GlStates.current().bindTexture(GL_TEXTURE_2D, 0);
	}

	@Override
	public void cleanup() throws GameException {
		GlStates.current().deleteTextures(getTextureId());
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

	private CompletableFuture<Void> submit(GameRunnable run) {
		return owner.submit(() -> {
			run.run();
		});
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
				AtomicReference<ByteBuffer> abuf = new AtomicReference<>();
				CompletableFuture<Void> fut2 = submit(() -> {
					long t1 = System.nanoTime();
					GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, buf);
					glBufferData(GL_PIXEL_UNPACK_BUFFER, w * h * Integer.BYTES, GL_STATIC_DRAW);
					ByteBuffer buf1 = glMapBufferRange(GL_PIXEL_UNPACK_BUFFER, 0, w * h * Integer.BYTES,
							GL_MAP_WRITE_BIT);
					GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);
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
				CompletableFuture<Void> fut3 = submit(new GameRunnable() {
					@Override
					public void run() {
						long t1 = System.nanoTime();
						GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, buf);
						glUnmapBuffer(GL_PIXEL_UNPACK_BUFFER);
						bind();
						glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
						glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
						glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
						nanos.addAndGet(System.nanoTime() - t1);
						GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);
						glDeleteBuffers(buf);
						glFinish();
						fut.complete(null);
						System.out.println(glIsTexture(textureId));
						manager.launcher.getWindow().getRenderThread().submit(() -> {
							System.out.println(glIsTexture(textureId));
						});
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
		System.out.println("loaded texture");
		return fut;
	}

	@Override
	public CompletableFuture<Void> uploadAsync(BufferedImage image) throws GameException {
// 		bind();
//		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
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
		throw new UnsupportedOperationException();
	}

	public static CompletableFuture<LWJGLTexture> newTexture(LWJGLTextureManager man, ExecutorThread owner) {
		CompletableFuture<LWJGLTexture> f = new CompletableFuture<>();
		new LWJGLTexture(man, owner, f);
		return f;
	}
}
