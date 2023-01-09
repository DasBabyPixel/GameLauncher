package gamelauncher.lwjgl.render.texture;

import de.dasbabypixel.api.property.NumberValue;
import de.matthiasmann.twl.utils.PNGDecoder;
import gamelauncher.engine.render.texture.Texture;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.engine.util.concurrent.ExecutorThreadService;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.math.Math;
import gamelauncher.engine.util.profiler.Profiler;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.states.GlStates;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.lwjgl.opengles.GLES20.*;
import static org.lwjgl.opengles.GLES30.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengles.GLES30.GL_READ_FRAMEBUFFER;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

public class LWJGLTexture extends AbstractGameResource implements Texture {

	public static final byte[] SIGNATURE_RAW = new byte[] {0x45, (byte) 0xFF, 0x61, 0x19};
	private static final AtomicInteger tid = new AtomicInteger();
	private final ExecutorThread owner;
	private final ExecutorThreadService service;
	private final AtomicReference<LWJGLTextureFormat> format =
			new AtomicReference<>(LWJGLTextureFormat.RGBA);
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
	private final AtomicInteger textureId = new AtomicInteger(0);
	private final NumberValue width = NumberValue.zero();
	private final NumberValue height = NumberValue.zero();
	private final Profiler profiler;
	private final LWJGLTextureManager manager;
	private int cwidth = 0;
	private int cheight = 0;

	public LWJGLTexture(LWJGLGameLauncher launcher, ExecutorThread owner,
			ExecutorThreadService service) {
		this.manager = launcher.getTextureManager();
		this.owner = owner;
		this.service = service;
		this.profiler = launcher.getProfiler();
	}

	private static BufferedImage getBufferedImage(int texture, int width, int height) {
		ByteBuffer pixels = getBufferedImageBuffer(texture, width, height);
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int r = pixels.get((y * width + x) * Integer.BYTES);
				int g = pixels.get((y * width + x) * Integer.BYTES + 1);
				int b = pixels.get((y * width + x) * Integer.BYTES + 2);
				int a = pixels.get((y * width + x) * Integer.BYTES + 3);

				int argb = a << 24 | r << 16 | g << 8 | b;
				img.setRGB(x, y, argb);
			}
		}
		memFree(pixels);
		return img;
	}

	private static ByteBuffer getBufferedImageBuffer(int texture, int width, int height) {
		GlStates cur = GlStates.current();
		ByteBuffer pixels = memAlloc(4 * width * height);
		cur.bindTexture(GL_TEXTURE_2D, texture);
		int fbo = cur.genFramebuffers();
		cur.bindFramebuffer(GL_FRAMEBUFFER, fbo);
		cur.framebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);
		cur.readPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
		cur.bindFramebuffer(GL_FRAMEBUFFER, 0);
		cur.deleteFramebuffers(fbo);
		cur.bindTexture(GL_TEXTURE_2D, 0);
		return pixels;
	}

	private void upload0(ByteBuffer buffer, int x, int y, int width, int height)
			throws InvalidSizeException {
		profiler.begin("render", "upload");
		lock.writeLock().lock();
		int mintw = x + width;
		int minth = y + height;
		if (cwidth < mintw || cheight < minth) {
			throw new InvalidSizeException();
		}
		GlStates cur = GlStates.current();
		cur.bindTexture(GL_TEXTURE_2D, textureId.get());
		profiler.check();
		cur.texSubImage2D(GL_TEXTURE_2D, 0, x, y, width, height, format.get().gl, GL_UNSIGNED_BYTE,
				buffer);
		profiler.check();
		cur.bindTexture(GL_TEXTURE_2D, 0);
		profiler.check();
		lock.writeLock().unlock();
		profiler.end();
	}

	public CompletableFuture<Void> write() {
		return owner.submit(() -> {
			try {
				lock.readLock().lock();
				ImageIO.write(getBufferedImage(textureId.get(), cwidth, cheight), "png",
						new File("img" + tid.incrementAndGet() + ".png"));
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				lock.readLock().unlock();
			}
		});
	}

	private boolean isRaw(byte[] data) {
		for (int i = 0; i < SIGNATURE_RAW.length; i++) {
			if (data[i] != SIGNATURE_RAW[i]) {
				return false;
			}
		}
		return true;
	}

	private void resize0(int width, int height) {
		profiler.begin("render", "resize");
		try {
			lock.writeLock().lock();
			int owidth = cwidth;
			int oheight = cheight;
			int oid = textureId.get();
			if (oid == 0) {
				safeCreate();
				oid = textureId.get();
			}

			if (oheight == 0 || owidth == 0) {
				allocateCreated(width, height);
				return;
			}

			int nid = createTexture();
			cwidth = width;
			cheight = height;

			int copyw = Math.min(owidth, width);
			int copyh = Math.min(oheight, height);

			LWJGLTextureFormat format = this.format.get();

			GlStates cur = GlStates.current();

			cur.bindTexture(GL_TEXTURE_2D, nid);
			cur.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			cur.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

			cur.texImage2D(GL_TEXTURE_2D, 0, format.glInternal, width, height, 0, format.gl,
					GL_UNSIGNED_BYTE, null);
			cur.bindTexture(GL_TEXTURE_2D, 0);

			CLTextureUtility u = manager.clTextureUtility.get();
			GlStates c = GlStates.current();
			u.framebuffer1.bind();
			c.framebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, nid, 0);
			u.framebuffer1.unbind();
			u.framebuffer2.bind();
			c.framebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, oid, 0);
			u.framebuffer2.unbind();

			c.bindFramebuffer(GL_READ_FRAMEBUFFER, u.framebuffer2.getId());
			c.bindFramebuffer(GL_DRAW_FRAMEBUFFER, u.framebuffer1.getId());
			c.blitFramebuffer(0, 0, copyw, copyh, 0, 0, copyw, copyh, GL_COLOR_BUFFER_BIT,
					GL_NEAREST);
			c.bindFramebuffer(GL_READ_FRAMEBUFFER, 0);
			c.bindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);

			textureId.set(nid);
			cur.deleteTextures(oid);
			cur.flush();
			cur.finish();
		} finally {
			lock.writeLock().unlock();
			profiler.end();
		}
	}

	private void safeCreate() {
		lock.writeLock().lock();
		if (textureId.get() == 0) {
			textureId.set(createTexture());
		}
		lock.writeLock().unlock();
	}

	private void allocateCreated(int width, int height) {
		profiler.begin("render", "allocate_created");
		lock.writeLock().lock();
		cwidth = width;
		cheight = height;
		LWJGLTextureFormat format = this.format.get();
		GlStates cur = GlStates.current();
		cur.bindTexture(GL_TEXTURE_2D, textureId.get());
		cur.pixelStorei(GL_UNPACK_ALIGNMENT, 1);
		cur.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		cur.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		cur.texImage2D(GL_TEXTURE_2D, 0, format.glInternal, cwidth, cheight, 0, format.gl,
				GL_UNSIGNED_BYTE, null);
		cur.bindTexture(GL_TEXTURE_2D, 0);
		lock.writeLock().unlock();
		profiler.end();
	}

	@Override
	public void cleanup0() throws GameException {
		Threads.waitFor(owner.submit(() -> {
			lock.writeLock().lock();
			int id = textureId.getAndSet(0);
			if (id != 0) {
				GlStates.current().deleteTextures(id);
			}
			lock.writeLock().unlock();
		}));
	}

	@Override
	public CompletableFuture<BufferedImage> getBufferedImage() {
		return owner.submit(() -> {
			lock.readLock().lock();
			BufferedImage img = getBufferedImage(this.textureId.get(), cwidth, cheight);
			lock.readLock().unlock();
			return img;
		});
	}

	@Override
	public CompletableFuture<Void> allocate(int width, int height) {
		this.width.setValue(width);
		this.height.setValue(height);
		return owner.submit(() -> {
			lock.writeLock().lock();
			safeCreate();
			allocateCreated(width, height);
			lock.writeLock().unlock();
		});
	}

	@Override
	public CompletableFuture<Void> resize(int width, int height) {
		this.width.setValue(width);
		this.height.setValue(height);
		return owner.submit(() -> {
			try {
				lock.writeLock().lock();
				resize0(width, height);
			} finally {
				lock.writeLock().unlock();
			}
		});
	}

	@Override
	public NumberValue getWidth() {
		return width;
	}

	@Override
	public NumberValue getHeight() {
		return height;
	}

	@Override
	public CompletableFuture<Void> uploadAsync(BufferedImage image) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CompletableFuture<Void> uploadAsync(ResourceStream stream) {
		return uploadSubAsync(stream, 0, 0);
	}

	@Override
	public CompletableFuture<Void> uploadSubAsync(ResourceStream stream, int x, int y) {
		CompletableFuture<Void> fut = new CompletableFuture<>();
		service.submit(() -> {
			profiler.begin("render", "upload_texture_worker");
			try {
				byte[] data = stream.readAllBytes();
				stream.cleanup();
				ByteArrayInputStream bin = new ByteArrayInputStream(data);

				int width;
				int height;
				ByteBuffer buff;
				if (isRaw(data)) {
					bin.skip(SIGNATURE_RAW.length);
					ByteBuffer tbbuf = memAlloc(2 * Integer.BYTES);
					tbbuf.put(data, SIGNATURE_RAW.length, 2 * Integer.BYTES);
					tbbuf.flip();
					width = tbbuf.getInt();
					height = tbbuf.getInt();
					memFree(tbbuf);
					buff = memAlloc(width * height * format.get().size);
					buff.put(data, SIGNATURE_RAW.length + 2 * Integer.BYTES,
							data.length - SIGNATURE_RAW.length - 2 * Integer.BYTES);
					buff.flip();
				} else {
					PNGDecoder dec = new PNGDecoder(bin);
					width = dec.getWidth();
					height = dec.getHeight();
					buff = memAlloc(4 * width * height);
					dec.decode(buff, width * 4, PNGDecoder.Format.RGBA);
					buff.flip();
				}
				bin.close();

				//				Threads.sleep(10);

				owner.submit(() -> {
					GlStates cur = GlStates.current();
					profiler.begin("render", "upload_texture");
					lock.writeLock().lock();

					if (textureId.get() == 0) {
						safeCreate();
						allocateCreated(x + width, y + height);
					}

					upload0(buff, x, y, width, height);
					memFree(buff);
					cur.flush();
					cur.finish();
					lock.writeLock().unlock();
					profiler.end();
				}).thenRun(() -> fut.complete(null));

			} catch (GameException | IOException ex) {
				fut.completeExceptionally(ex);
			} finally {
				profiler.end();
			}
		});
		return fut;
	}

	@Override
	public CompletableFuture<Void> copyTo(Texture other, int srcX, int srcY, int dstX, int dstY,
			int width, int height) throws GameException {
		if (!(other instanceof final LWJGLTexture lwjgl)) {
			ClassCastException cause = new ClassCastException("Texture passed is no LWJLTexture");
			GameException ge = new GameException(cause);
			ge.setStackTrace(new StackTraceElement[0]);
			throw ge;
		}
		return owner.submit(() -> {
			lock.readLock().lock();
			lwjgl.lock.writeLock().lock();
			int id = textureId.get();
			int oid = lwjgl.textureId.get();

			int maxcopyw = Math.min(cwidth - srcX, lwjgl.cwidth - dstX);
			int copyw = Math.min(width, maxcopyw);
			int maxcopyh = Math.min(cheight - srcY, lwjgl.cheight - dstY);
			int copyh = Math.min(width, maxcopyh);

			if (copyw >= 1 && copyh >= 1) {
				GlStates.current()
						.copyImageSubData(id, GL_TEXTURE_2D, 0, srcX, srcY, 0, oid, GL_TEXTURE_2D,
								0, dstX, dstY, 0, copyw, copyh, 1);
			}

			lwjgl.lock.writeLock().unlock();
			lock.readLock().unlock();
		});
	}

	private int createTexture() {
		return GlStates.current().genTextures();
	}

	public void setInternalFormat(LWJGLTextureFormat format) {
		lock.writeLock().lock();
		this.format.set(format);
		lock.writeLock().unlock();
	}

	public int getTextureId() {
		return textureId.get();
	}

	/*
	 * f public CompletableFuture<Void> uploadAsync(int x, int y, int width, int
	 * height, ByteBuffer bbuf) { CompletableFuture<Void> fut = new
	 * CompletableFuture<>(); manager.service.submit(() -> { try { AtomicInteger
	 * aibuf = new AtomicInteger(); AtomicReference<ByteBuffer> abuf = new
	 * AtomicReference<>(); CompletableFuture<Void> fut2 = submit(() -> { final int
	 * buf = glGenBuffers(); aibuf.set(buf);
	 * GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, buf);
	 * glBufferData(GL_PIXEL_UNPACK_BUFFER, width * height * internalFormat.size,
	 * GL_STATIC_DRAW); ByteBuffer buf1 = glMapBufferRange(GL_PIXEL_UNPACK_BUFFER,
	 * 0, width * height * internalFormat.size, GL_MAP_WRITE_BIT);
	 * GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, 0); if (buf1 == null) {
	 * int error = glGetError(); throw new GameException("Couldn't map buffer! (" +
	 * Integer.toHexString(error) + ")"); } abuf.set(buf1); });
	 * Threads.waitFor(fut2); abuf.get().put(bbuf); bbuf.position(0);
	 * abuf.get().flip(); CompletableFuture<Void> fut3 = submit(new GameRunnable() {
	 *
	 * @Override public void run() { int buf = aibuf.get();
	 * GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, buf);
	 * glUnmapBuffer(GL_PIXEL_UNPACK_BUFFER); bind(); glTexParameteri(GL_TEXTURE_2D,
	 * GL_TEXTURE_MIN_FILTER, GL_LINEAR); glTexParameteri(GL_TEXTURE_2D,
	 * GL_TEXTURE_MAG_FILTER, GL_LINEAR); glTexSubImage2D(GL_TEXTURE_2D, 0, x, y,
	 * width, height, internalFormat.gl, GL_UNSIGNED_BYTE, 0);
	 * GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);
	 * glDeleteBuffers(buf); glFlush(); glFinish(); fut.complete(null); } });
	 * Threads.waitFor(fut3); } catch (Throwable ex) {
	 * fut.completeExceptionally(ex); manager.launcher.handleError(ex); } }); return
	 * fut; }
	 *
	 * @Override public CompletableFuture<Void> uploadAsync(ResourceStream stream)
	 * throws GameException { CompletableFuture<Void> fut = new
	 * CompletableFuture<>();
	 *
	 * manager.service.submit(() -> { try { PNGDecoder decoder =
	 * stream.newPNGDecoder(); int w = decoder.getWidth(); int h =
	 * decoder.getHeight(); AtomicInteger aibuf = new AtomicInteger();
	 * AtomicReference<ByteBuffer> abuf = new AtomicReference<>();
	 * CompletableFuture<Void> fut2 = submit(() -> { final int buf = glGenBuffers();
	 * aibuf.set(buf); GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, buf);
	 * modifyLock.readLock().lock(); glBufferData(GL_PIXEL_UNPACK_BUFFER, w * h *
	 * internalFormat.size, GL_STATIC_DRAW); ByteBuffer buf1 =
	 * glMapBufferRange(GL_PIXEL_UNPACK_BUFFER, 0, w * h * internalFormat.size,
	 * GL_MAP_WRITE_BIT); modifyLock.readLock().unlock();
	 * GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, 0); if (buf1 == null) {
	 * int error = glGetError(); throw new GameException("Couldn't map buffer! (" +
	 * Integer.toHexString(error) + ")"); } abuf.set(buf1); });
	 * Threads.waitFor(fut2); decoder.decode(abuf.get(), w * Integer.BYTES,
	 * PNGDecoder.Format.RGBA); stream.cleanup(); abuf.get().flip();
	 * CompletableFuture<Void> fut3 = submit(new GameRunnable() {
	 *
	 * @Override public void run() { int buf = aibuf.get();
	 * GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, buf);
	 * glUnmapBuffer(GL_PIXEL_UNPACK_BUFFER); bind(); glTexParameteri(GL_TEXTURE_2D,
	 * GL_TEXTURE_MIN_FILTER, GL_LINEAR); glTexParameteri(GL_TEXTURE_2D,
	 * GL_TEXTURE_MAG_FILTER, GL_LINEAR); glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w,
	 * h, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
	 * GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);
	 * glDeleteBuffers(buf); glFlush(); glFinish(); fut.complete(null); } });
	 * Threads.waitFor(fut3); } catch (Throwable ex) {
	 * manager.launcher.handleError(ex); } }); return fut; }
	 */


	public static class InvalidSizeException extends GameException {

		public InvalidSizeException() {
			super();
		}
	}
}
