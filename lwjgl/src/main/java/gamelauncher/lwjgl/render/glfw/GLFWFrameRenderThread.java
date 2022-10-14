package gamelauncher.lwjgl.render.glfw;

import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.lwjgl.opengles.GLES20;

import gamelauncher.engine.render.FrameRenderer;
import gamelauncher.engine.render.RenderMode;
import gamelauncher.engine.render.RenderThread;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.AbstractExecutorThread;
import gamelauncher.lwjgl.render.GlContext;
import gamelauncher.lwjgl.render.framebuffer.ManualQueryFramebuffer;

@SuppressWarnings("javadoc")
public class GLFWFrameRenderThread extends AbstractExecutorThread implements RenderThread {

	private static final AtomicInteger ids = new AtomicInteger();

	private final GLFWFrame frame;

	private volatile boolean draw = false;

	private volatile boolean drawRefresh = false;

	private volatile boolean refresh = false;

	private final AtomicBoolean modifying = new AtomicBoolean(false);

	private final Phaser phaser;

	private FrameRenderer frameRenderer = null;

	private final ManualQueryFramebuffer mqfb;

	private final Consumer<Long> nanoSleeper = nanos -> {
		this.waitForSignalTimeout(nanos);
	};

	public GLFWFrameRenderThread(GLFWFrame frame) {
		super(frame.launcher.getGlThreadGroup());
		this.phaser = new Phaser(1);
		this.mqfb = new ManualQueryFramebuffer(frame.framebuffer);
		this.frame = frame;
		this.setName("GLFWFrameRenderThread-" + GLFWFrameRenderThread.ids.incrementAndGet());
	}

	@Override
	protected void startExecuting() throws GameException {
		this.frame.context.makeCurrent();

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
		this.frame.launcher.getGlThreadGroup().terminated(this);
		this.frame.context.destroyCurrent();
	}

	@Override
	protected void workExecution() throws GameException {
		if (this.draw || this.frame.renderMode() == RenderMode.CONTINUOUSLY) {
			this.lock();
			boolean refresh = this.drawRefresh;
			if (refresh)
				this.drawRefresh = false;
			this.draw = false;
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
					this.frame.launcher.handleError(ex);
				}
				return;
			}
			if (this.frameRenderer != cfr) {
				if (this.frameRenderer != null) {
					try {
						this.frameRenderer.cleanup(this.frame);
					} catch (GameException ex) {
						this.frame.launcher.handleError(ex);
					}
				}
				this.frameRenderer = cfr;
				if (cfr != null) {
					try {
						cfr.init(this.frame);
					} catch (GameException ex) {
						this.frame.launcher.handleError(ex);
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
					this.frame.launcher.handleError(ex);
				}
			}
			frame: {
				if (this.mqfb.width().intValue() == 0 || this.mqfb.height().intValue() == 0) {
					break frame;
				}
				try {
					cfr.renderFrame(this.frame);
				} catch (GameException ex) {
					this.frame.launcher.handleError(ex);
				}
			}
		}
		this.phaser.arrive();
		this.frame.frameCounter().frame(this.nanoSleeper);
	}
	
	protected boolean hasWindow() {
		return false;
	}

	void scheduleDraw() {
		this.lock();
		this.draw = true;
		this.unlock();
		this.signal();
	}

	void scheduleDrawWaitForFrame() {
		int frame = this.phaser.getPhase();
		this.scheduleDraw();
		this.phaser.awaitAdvance(frame);
	}

	void waitForFrame() {
		this.phaser.awaitAdvance(this.phaser.getPhase());
	}

	void refresh() {
		this.lock();
		this.refresh = true;
		this.unlock();
		this.signal();
	}

	void scheduleDrawRefresh() {
		this.lock();
		this.drawRefresh = true;
		this.draw = true;
		this.unlock();
		this.signal();
	}

	void scheduleDrawRefreshWait() {
		int frame = this.phaser.getPhase();
		this.scheduleDrawRefresh();
		this.phaser.awaitAdvance(frame);
	}

	@Override
	public GLFWFrame getFrame() {
		return this.frame;
	}

	private void lock() {
		while (!this.modifying.compareAndSet(false, true))
			;
	}

	private void unlock() {
		this.modifying.set(false);
	}

	private static enum FrameType {
		REFRESH, RENDER
	}

}
