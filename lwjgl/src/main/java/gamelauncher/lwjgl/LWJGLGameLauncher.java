package gamelauncher.lwjgl;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.event.EventHandler;
import gamelauncher.engine.event.events.LauncherInitializedEvent;
import gamelauncher.engine.render.BasicCamera;
import gamelauncher.engine.render.Camera;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.RenderMode;
import gamelauncher.engine.resource.SimpleResourceLoader;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.OperatingSystem;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.math.Math;
import gamelauncher.lwjgl.gui.LWJGLGuiManager;
import gamelauncher.lwjgl.render.GlThreadGroup;
import gamelauncher.lwjgl.render.LWJGLDrawContext;
import gamelauncher.lwjgl.render.LWJGLGameRenderer;
import gamelauncher.lwjgl.render.font.BasicFontFactory;
import gamelauncher.lwjgl.render.font.LWJGLGlyphProvider;
import gamelauncher.lwjgl.render.glfw.GLFWFrame;
import gamelauncher.lwjgl.render.glfw.GLFWThread;
import gamelauncher.lwjgl.render.glfw.GLUtil;
import gamelauncher.lwjgl.render.modelloader.LWJGLModelLoader;
import gamelauncher.lwjgl.render.shader.LWJGLShaderLoader;
import gamelauncher.lwjgl.render.texture.LWJGLTextureManager;
import gamelauncher.lwjgl.settings.controls.MouseSensivityInsertion;
import gamelauncher.lwjgl.util.keybind.LWJGLKeybindManager;
import gamelauncher.lwjgl.util.profiler.GLSectionHandler;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengles.GLES;
import org.lwjgl.system.Configuration;

/**
 * @author DasBabyPixel
 */
public class LWJGLGameLauncher extends GameLauncher {

	private final Camera camera = new BasicCamera();
	private final GlThreadGroup glThreadGroup;
	private GLFWFrame mainFrame;
	private boolean mouseMovement = false;
	private float mouseSensivity = 1.0F;
	private boolean ignoreNextMovement = false;
	private GLFWThread glfwThread;

	public LWJGLGameLauncher() throws GameException {
		this.setKeybindManager(new LWJGLKeybindManager(this));
		this.setResourceLoader(new SimpleResourceLoader());
		this.setShaderLoader(new LWJGLShaderLoader());
		this.setGameRenderer(new LWJGLGameRenderer(this));
		this.setModelLoader(new LWJGLModelLoader(this));
		this.setGuiManager(new LWJGLGuiManager(this));
		this.setFontFactory(new BasicFontFactory(this));
		this.setTextureManager(new LWJGLTextureManager(this));
		this.setOperatingSystem(OperatingSystem.WINDOWS);
		this.glThreadGroup = new GlThreadGroup();
	}

	@EventHandler
	public void handle(LauncherInitializedEvent event) {
		try {
			Threads.waitFor(this.mainFrame.showWindow());
		} catch (GameException ex) {
			ex.printStackTrace();
		}

		// glfwThread.submit(() -> glfwSetWindowAttrib(window.getGLFWId(), GLFW_FLOATING,
		// GLFW_TRUE));
		this.mouseMovement(false);
	}

	@Override
	@Deprecated
	public LWJGLDrawContext createContext(Framebuffer framebuffer) {
		return new LWJGLDrawContext(framebuffer);
	}

	@Override
	protected void tick0() throws GameException {
		// double avgNanos = getGameThread().getAverageTickTime();
		// long avgMillis = TimeUnit.NANOSECONDS.toMicros((long)avgNanos);
		// System.out.printf("%05d%n", avgMillis);
		// System.out.println("Tick");
		this.mainFrame.getInput().handleInput();
		mouse:
		if (this.mouseMovement) {
			Camera cam = this.camera;
			float dy = (float) (this.mainFrame.getMouse().getDeltaX() * 0.4) * this.mouseSensivity;
			float dx = (float) (this.mainFrame.getMouse().getDeltaY() * 0.4) * this.mouseSensivity;
			if ((dx != 0 || dy != 0) && this.ignoreNextMovement) {
				this.ignoreNextMovement = false;
				break mouse;
			}
			Vector3f rot = new Vector3f(cam.getRotX(), cam.getRotY(), cam.getRotZ());
			cam.setRotation(Math.clamp(rot.x + dx, -90F, 90F), rot.y + dy, rot.z);
		}
	}

	@Override
	protected void start0() throws GameException {
		GLUtil.clinit(this);

		this.getProfiler().addHandler("render", new GLSectionHandler());
		this.glfwThread = new GLFWThread();
		this.glfwThread.start();
		Configuration.OPENGL_EXPLICIT_INIT.set(true);
		Configuration.OPENGLES_EXPLICIT_INIT.set(true);
		GL.create();
		GLES.create(GL.getFunctionProvider());
		GL.destroy();

		this.mainFrame = new GLFWFrame(this);
		// this.asyncUploader = new LWJGLAsyncOpenGL(this, this.mainFrame);
		this.setFrame(this.mainFrame);
		this.mainFrame.framebuffer().renderThread()
				.submit(() -> this.setGlyphProvider(new LWJGLGlyphProvider(this)));
		this.mainFrame.renderMode(RenderMode.ON_UPDATE);
		// this.mainFrame.frameCounter().limit(500);
		// Threads.waitFor(this.mainFrame.createWindow());
		// this.asyncUploader.start();
		//		this.mainFrame.frameCounter().addUpdateListener(fps -> {
		//			this.getLogger().infof("FPS: %s", fps);
		//		});
		this.mainFrame.closeCallback().setValue(frame -> {
			this.mainFrame.hideWindow();
			try {
				LWJGLGameLauncher.this.stop();
			} catch (GameException ex) {
				ex.printStackTrace();
			}
		});
		// this.mainFrame.getRenderThread().start();

		this.getEventManager().registerListener(this);
	}

	@Override
	protected void stop0() throws GameException {

		this.getGlyphProvider().cleanup();
		this.getTextureManager().cleanup();

		// this.asyncUploader.cleanup();
		// Threads.waitFor(this.mainFrame.destroy());
		this.mainFrame.cleanup();
		Threads.waitFor(this.glfwThread.exit());
	}

	@Override
	protected void registerSettingInsertions() {
		new MouseSensivityInsertion().register(this);
	}

	@Override
	public LWJGLTextureManager getTextureManager() {
		return (LWJGLTextureManager) super.getTextureManager();
	}

	@Override
	public LWJGLGuiManager getGuiManager() {
		return (LWJGLGuiManager) super.getGuiManager();
	}

	@Override
	public LWJGLGameRenderer getGameRenderer() {
		return (LWJGLGameRenderer) super.getGameRenderer();
	}
	//
	// /**
	// * @return the {@link LWJGLAsyncOpenGL}
	// */
	// public LWJGLAsyncOpenGL getAsyncUploader() {
	// return this.asyncUploader;
	// }

	private void mouseMovement(boolean movement) {
		this.mainFrame.getMouse().grabbed(movement).thenRun(() -> {
			if (!movement) {
				GLFW.glfwSetCursorPos(this.mainFrame.getGLFWId(),
						this.mainFrame.framebuffer().width().doubleValue() / 2,
						this.mainFrame.framebuffer().height().doubleValue() / 2);
			} else {
				GLFW.glfwSetCursorPos(this.mainFrame.getGLFWId(), 0, 0);
				this.ignoreNextMovement = true;
			}
		});
		this.mouseMovement = movement;
	}

	/**
	 * @return the main frame
	 */
	public GLFWFrame getMainFrame() {
		return this.mainFrame;
	}

	/**
	 * @return the GLFW thread
	 */
	public GLFWThread getGLFWThread() {
		return this.glfwThread;
	}

	/**
	 * @return the {@link GlThreadGroup}
	 */
	public GlThreadGroup getGlThreadGroup() {
		return this.glThreadGroup;
	}

}
