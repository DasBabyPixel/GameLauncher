package gamelauncher.lwjgl.render.texture;

import static org.lwjgl.opengles.GLES20.*;
import static org.lwjgl.opengles.GLES30.*;
import static org.lwjgl.opengles.GLES32.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.imageio.ImageIO;

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
	private int width;
	private int height;
	private LWJGLTextureFormat internalFormat = LWJGLTextureFormat.RGBA;
	private final ExecutorThread owner;
	public final Logger logger = Logger.getLogger();
	private final ReadWriteLock modifyLock = new ReentrantReadWriteLock(true);
	private final CompletableFuture<Void> createFuture = new CompletableFuture<>();

	@Deprecated
	public LWJGLTexture(LWJGLTextureManager manager, ExecutorThread owner, int textureId) {
		this.manager = manager;
		this.owner = owner;
		this.textureId = textureId;
		if (this.textureId == -1) {
			submit(() -> {
				modifyLock.writeLock().lock();
				LWJGLTexture.this.textureId = glGenTextures();
				modifyLock.writeLock().unlock();
				createFuture.complete(null);
			});
		}
	}

	private LWJGLTexture(LWJGLTextureManager man, ExecutorThread owner, CompletableFuture<LWJGLTexture> f) {
		this.owner = owner;
		this.manager = man;
		submit(() -> {
			modifyLock.writeLock().lock();
			textureId = glGenTextures();
			modifyLock.writeLock().unlock();
			createFuture.complete(null);
		}).thenRun(() -> {
			f.complete(this);
		});
	}

	private final AtomicInteger it = new AtomicInteger();

	@Override
	public CompletableFuture<Void> resize(int width, int height) {
		return submit(() -> {
			Threads.waitFor(createFuture);
			modifyLock.writeLock().lock();
			int oldWidth = this.width;
			int oldHeight = this.height;
			int copyWidth = Math.min(oldWidth, width);
			int copyHeight = Math.min(oldHeight, height);
			System.out.printf("Resize %s %s %s %s %s %s%n", oldWidth, oldHeight, width, height, copyWidth, copyHeight);
			int oldId = this.textureId;
			this.textureId = glGenTextures();
			this.width = width;
			this.height = height;
			GlStates.current().bindTexture(GL_TEXTURE_2D, this.textureId);
			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexImage2D(GL_TEXTURE_2D, 0, internalFormat.glInternal, width, height, 0, internalFormat.gl,
					GL_UNSIGNED_BYTE, (ByteBuffer) null);
			GlStates.current().bindTexture(GL_TEXTURE_2D, 0);

			glCopyImageSubData(oldId, GL_TEXTURE_2D, 0, 0, 0, 0, this.textureId, GL_TEXTURE_2D, 0, 0, 0, 0, 1, 1, 1);

			try {

				ImageIO.write(getBufferedImage(oldId, oldWidth, oldHeight), "png",
						new File("img" + it.incrementAndGet() + ".png"));
				ImageIO.write(getBufferedImage(this.textureId, width, height), "png",
						new File("img" + it.incrementAndGet() + ".png"));
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			GlStates.current().deleteTextures(oldId);
			glFinish();
			modifyLock.writeLock().unlock();
		});
	}

	public int getWidth() {
		try {
			modifyLock.readLock().lock();
			return width;
		} finally {
			modifyLock.readLock().unlock();
		}
	}

	public int getHeight() {
		try {
			modifyLock.readLock().lock();
			return height;
		} finally {
			modifyLock.readLock().unlock();
		}
	}

	@Override
	public BufferedImage getBufferedImage() {
		try {
			modifyLock.readLock().lock();
			int width = this.width;
			int height = this.height;
			return getBufferedImage(textureId, width, height);
		} finally {
			modifyLock.readLock().unlock();
		}
	}

	private BufferedImage getBufferedImage(int texture, int width, int height) {
		ByteBuffer pixels = getBufferedImageBuffer(texture, width, height);
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

	private ByteBuffer getBufferedImageBuffer(int texture, int width, int height) {
		ByteBuffer pixels = memAlloc(4 * width * height);
		GlStates.current().bindTexture(GL_TEXTURE_2D, texture);
		int fbo = glGenFramebuffers();
		GlStates.current().bindFramebuffer(GL_FRAMEBUFFER, fbo);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);
		glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
		GlStates.current().bindFramebuffer(GL_FRAMEBUFFER, 0);
		glDeleteFramebuffers(fbo);
		GlStates.current().bindTexture(GL_TEXTURE_2D, 0);
		return pixels;
	}

	public ByteBuffer getBufferedImageBuffer() {
		try {
			modifyLock.readLock().lock();
			return getBufferedImageBuffer(textureId, width, height);
		} finally {
			modifyLock.readLock().unlock();
		}
	}

	public void setInternalFormat(LWJGLTextureFormat format) {
		modifyLock.writeLock().lock();
		this.internalFormat = format;
		modifyLock.writeLock().unlock();
	}

	@Override
	public CompletableFuture<Void> allocate(int width, int height) {
		return submit(() -> {
			modifyLock.writeLock().lock();
			this.width = width;
			this.height = height;
			bind();
			glPixelStorei(GL_UNPACK_ALIGNMENT, internalFormat.size);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexImage2D(GL_TEXTURE_2D, 0, internalFormat.glInternal, width, height, 0, internalFormat.gl,
					GL_UNSIGNED_BYTE, (ByteBuffer) null);
			unbind();
			modifyLock.writeLock().unlock();
		});
	}

	public void bind() {
		GlStates.current().bindTexture(GL_TEXTURE_2D, getTextureId());
	}

	public void unbind() {
		GlStates.current().bindTexture(GL_TEXTURE_2D, 0);
	}

	@Override
	public void cleanup() throws GameException {
		Threads.waitFor(submit(() -> {
			modifyLock.writeLock().lock();
			GlStates.current().deleteTextures(getTextureId());
			modifyLock.writeLock().unlock();
		}));
	}

	public int getTextureId() {
		try {
			Threads.waitFor(createFuture);
			modifyLock.readLock().lock();
			return textureId;
		} finally {
			modifyLock.readLock().unlock();
		}
	}

	private CompletableFuture<Void> submit(GameRunnable run) {
		return owner.submit(run);
	}

	@Override
	public CompletableFuture<Void> copyTo(Texture other) throws GameException {
		return submit(() -> {
			LWJGLTexture l = (LWJGLTexture) other;
			Threads.waitFor(createFuture, l.createFuture);
			modifyLock.readLock().lock();
			int copyw = Math.min(width, l.getWidth());
			int copyh = Math.min(height, l.getHeight());
			glCopyImageSubData(this.textureId, GL_TEXTURE_2D, 0, 0, 0, 0, l.textureId, GL_TEXTURE_2D, 0, 0, 0, 0, copyw,
					copyh, 1);
			modifyLock.readLock().unlock();
		});
	}

	public CompletableFuture<Void> uploadAsync(int x, int y, int width, int height, ByteBuffer bbuf) {
		CompletableFuture<Void> fut = new CompletableFuture<>();
		manager.service.submit(() -> {
			try {
				AtomicInteger aibuf = new AtomicInteger();
				AtomicReference<ByteBuffer> abuf = new AtomicReference<>();
				CompletableFuture<Void> fut2 = submit(() -> {
					final int buf = glGenBuffers();
					aibuf.set(buf);
					GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, buf);
					glBufferData(GL_PIXEL_UNPACK_BUFFER, width * height * internalFormat.size, GL_STATIC_DRAW);
					ByteBuffer buf1 = glMapBufferRange(GL_PIXEL_UNPACK_BUFFER, 0, width * height * internalFormat.size,
							GL_MAP_WRITE_BIT);
					GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);
					if (buf1 == null) {
						int error = glGetError();
						throw new GameException("Couldn't map buffer! (" + Integer.toHexString(error) + ")");
					}
					abuf.set(buf1);
				});
				Threads.waitFor(fut2);
				abuf.get().put(bbuf);
				bbuf.position(0);
				abuf.get().flip();
				CompletableFuture<Void> fut3 = submit(new GameRunnable() {
					@Override
					public void run() {
						int buf = aibuf.get();
						GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, buf);
						glUnmapBuffer(GL_PIXEL_UNPACK_BUFFER);
						bind();
						glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
						glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
						glTexSubImage2D(GL_TEXTURE_2D, 0, x, y, width, height, internalFormat.gl, GL_UNSIGNED_BYTE, 0);
						GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);
						glDeleteBuffers(buf);
						glFlush();
						glFinish();
						fut.complete(null);
					}
				});
				Threads.waitFor(fut3);
			} catch (Throwable ex) {
				fut.completeExceptionally(ex);
				manager.launcher.handleError(ex);
			}
		});
		return fut;
	}

	@Override
	public CompletableFuture<Void> uploadAsync(ResourceStream stream) throws GameException {
		CompletableFuture<Void> fut = new CompletableFuture<>();

		manager.service.submit(() -> {
			try {
				PNGDecoder decoder = stream.newPNGDecoder();
				int w = decoder.getWidth();
				int h = decoder.getHeight();
				AtomicInteger aibuf = new AtomicInteger();
				AtomicReference<ByteBuffer> abuf = new AtomicReference<>();
				CompletableFuture<Void> fut2 = submit(() -> {
					final int buf = glGenBuffers();
					aibuf.set(buf);
					GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, buf);
					modifyLock.readLock().lock();
					glBufferData(GL_PIXEL_UNPACK_BUFFER, w * h * internalFormat.size, GL_STATIC_DRAW);
					ByteBuffer buf1 = glMapBufferRange(GL_PIXEL_UNPACK_BUFFER, 0, w * h * internalFormat.size,
							GL_MAP_WRITE_BIT);
					modifyLock.readLock().unlock();
					GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);
					if (buf1 == null) {
						int error = glGetError();
						throw new GameException("Couldn't map buffer! (" + Integer.toHexString(error) + ")");
					}
					abuf.set(buf1);
				});
				Threads.waitFor(fut2);
				decoder.decode(abuf.get(), w * Integer.BYTES, PNGDecoder.Format.RGBA);
				stream.cleanup();
				abuf.get().flip();
				CompletableFuture<Void> fut3 = submit(new GameRunnable() {
					@Override
					public void run() {
						int buf = aibuf.get();
						GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, buf);
						glUnmapBuffer(GL_PIXEL_UNPACK_BUFFER);
						bind();
						glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
						glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
						glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w, h, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
						GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);
						glDeleteBuffers(buf);
						glFlush();
						glFinish();
						fut.complete(null);
					}
				});
				Threads.waitFor(fut3);
			} catch (Throwable ex) {
				manager.launcher.handleError(ex);
			}
		});
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
