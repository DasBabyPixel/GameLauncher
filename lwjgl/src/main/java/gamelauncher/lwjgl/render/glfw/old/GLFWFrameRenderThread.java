package gamelauncher.lwjgl.render.glfw.old;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.lwjgl.opengles.GLES20;

import gamelauncher.engine.render.Frame;
import gamelauncher.engine.render.FrameRenderer;
import gamelauncher.engine.render.RenderMode;
import gamelauncher.engine.render.RenderThread;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.lwjgl.render.GlContext;
import gamelauncher.lwjgl.render.framebuffer.ManualQueryFramebuffer;

/**
 * @author DasBabyPixel
 */
@SuppressWarnings("javadoc")
public class GLFWFrameRenderThread extends AsyncOpenGL implements RenderThread {

//	private static final Logger logger = Logger.getLogger();

	private static final AtomicInteger ids = new AtomicInteger();

	private final ManualQueryFramebuffer mqfb;

	private final AtomicBoolean modifying = new AtomicBoolean(false);

	final CompletableFuture<Void> createFuture = new CompletableFuture<>();

	private volatile boolean shouldDraw = false;

	private volatile boolean refresh = false;

	private volatile boolean refreshAfterDraw = false;

	private final Phaser phaser = new Phaser(1);

	private final GLFWFrame frame;

	private FrameRenderer frameRenderer;

	private final Consumer<Long> nanoSleeper = nanos -> {
		this.waitForSignalTimeout(nanos);
	};

	GLFWFrameRenderThread(GLFWFrame frame) throws GameException {
		super(frame);
		this.mqfb = new ManualQueryFramebuffer(frame.framebuffer = new HeapFramebuffer(frame, this));
		this.frame = frame;
		this.setName("GLFWRenderThread-" + GLFWFrameRenderThread.ids.incrementAndGet());
	}

	@Override
	protected void startExecuting() throws GameException {
		this.frame.framebuffer = new WindowFramebuffer(this.frame);
		super.startExecuting();
		this.shouldDraw = true;
		this.refreshAfterDraw = true;
		this.createFuture.complete(null);

		GlContext glContext = new GlContext();
		glContext.depth.enabled.value.set(true);
		glContext.depth.depthFunc.set(GLES20.GL_LEQUAL);
		glContext.blend.enabled.value.set(true);
		glContext.blend.srcrgb.set(GLES20.GL_SRC_ALPHA);
		glContext.blend.dstrgb.set(GLES20.GL_ONE_MINUS_SRC_ALPHA);
		glContext.replace(null);
	}

	@Override
	protected void stopExecuting() throws GameException {
		if (this.frameRenderer != null) {
			try {
				this.frameRenderer.cleanup(this.frame);
			} catch (GameException ex) {
				this.parent.launcher.handleError(ex);
			}
		}
		super.stopExecuting();
	}

	@Override
	protected void workExecution() throws GameException {
		super.workExecution();
		if (this.shouldDraw || this.frame.renderMode() == RenderMode.CONTINUOUSLY) {
			this.lock();
			boolean refresh = this.refreshAfterDraw;
			if (refresh) {
				this.refreshAfterDraw = false;
			}
			this.shouldDraw = false;
			this.unlock();
			this.frame(FrameType.RENDER);
			if (refresh)
				this.frame(FrameType.REFRESH);
		}
		if (this.refresh) {
			this.lock();
			this.refresh = false;
			this.unlock();
			this.frame(FrameType.REFRESH);
		}
	}

	private void frame(FrameType type) {
		FrameRenderer cfr = this.frame.frameRenderer();
		if (cfr != null) {
			if (type == FrameType.REFRESH) {
				this.mqfb.query();
				try {
					cfr.refreshDisplay(this.frame);
				} catch (GameException ex) {
					this.parent.launcher.handleError(ex);
				}
				return;
			}
			if (this.frameRenderer != cfr) {
				if (this.frameRenderer != null) {
					try {
						this.frameRenderer.cleanup(this.frame);
					} catch (GameException ex) {
						this.parent.launcher.handleError(ex);
					}
				}
				this.frameRenderer = cfr;
				if (cfr != null) {
					try {
						cfr.init(this.frame);
					} catch (GameException ex) {
						this.parent.launcher.handleError(ex);
					}
				}
			}

			vp: if (this.mqfb.newValue().booleanValue()) {
				this.mqfb.query();
				if (this.mqfb.width().intValue() == 0 || this.mqfb.height().intValue() == 0) {
					break vp;
				}
				try {
					cfr.windowSizeChanged(this.frame);
				} catch (GameException ex) {
					this.parent.launcher.handleError(ex);
				}
			}
			frame: {
				if (this.mqfb.width().intValue() == 0 || this.mqfb.height().intValue() == 0) {
					break frame;
				}
				try {
					cfr.renderFrame(this.frame);
				} catch (GameException ex) {
					this.parent.launcher.handleError(ex);
				}
			}
		}
		this.phaser.arrive();
		this.frame.frameCounter().frame(this.nanoSleeper);
	}

	public void refresh() {
		this.refresh = true;
		this.signal();
	}

	public void resize() {
		try {
			Threads.waitFor(this.submit(() -> {
				this.frame(FrameType.RENDER);
				this.frame(FrameType.REFRESH);
				this.shouldDraw = true;
			}));
		} catch (GameException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public Frame getFrame() {
		return this.parent;
	}

	public void scheduleDraw() {
		this.lock();
		this.shouldDraw = true;
		this.unlock();
		this.signal();
	}

	public void waitForFrame() {
		this.phaser.awaitAdvance(this.phaser.getPhase());
	}

	public void scheduleDrawWaitForFrame() {
		int phase = this.phaser.getPhase();
		this.lock();
		this.shouldDraw = true;
		this.unlock();
		this.signal();
		this.phaser.awaitAdvance(phase);
	}

	private void lock() {
		while (!this.modifying.compareAndSet(false, true))
			;
	}

	private void unlock() {
		this.modifying.set(false);
	}

	@Override
	protected boolean hasWindow() {
		return true;
	}

	private static enum FrameType {
		REFRESH, RENDER
	}

}
