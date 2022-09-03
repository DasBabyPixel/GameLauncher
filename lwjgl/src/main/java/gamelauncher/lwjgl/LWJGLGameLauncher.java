package gamelauncher.lwjgl;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengles.GLES;
import org.lwjgl.system.Configuration;

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
import gamelauncher.engine.util.profiler.SectionHandler;
import gamelauncher.lwjgl.gui.LWJGLGuiManager;
import gamelauncher.lwjgl.render.GlThreadGroup;
import gamelauncher.lwjgl.render.LWJGLDrawContext;
import gamelauncher.lwjgl.render.LWJGLGameRenderer;
import gamelauncher.lwjgl.render.font.BasicFontFactory;
import gamelauncher.lwjgl.render.font.LWJGLGlyphProvider;
import gamelauncher.lwjgl.render.glfw.GLFWThread;
import gamelauncher.lwjgl.render.glfw.GLFWWindow;
import gamelauncher.lwjgl.render.glfw.GLUtil;
import gamelauncher.lwjgl.render.glfw.LWJGLAsyncUploader;
import gamelauncher.lwjgl.render.modelloader.LWJGLModelLoader;
import gamelauncher.lwjgl.render.shader.LWJGLShaderLoader;
import gamelauncher.lwjgl.render.texture.LWJGLTextureManager;
import gamelauncher.lwjgl.settings.controls.MouseSensivityInsertion;
import gamelauncher.lwjgl.util.keybind.LWJGLKeybindManager;
import gamelauncher.lwjgl.util.profiler.GLSectionHandler;

/**
 * @author DasBabyPixel
 *
 */
public class LWJGLGameLauncher extends GameLauncher {

	private GLFWWindow window;

	private boolean mouseMovement = false;

	private float mouseSensivity = 1.0F;

	private boolean ignoreNextMovement = false;

	private GLFWThread glfwThread;

	private Camera camera = new BasicCamera();

	private LWJGLAsyncUploader asyncUploader;

	private GlThreadGroup glThreadGroup;

	/**
	 * @throws GameException
	 */
	public LWJGLGameLauncher() throws GameException {
		setKeybindManager(new LWJGLKeybindManager(this));
		setResourceLoader(new SimpleResourceLoader());
		setShaderLoader(new LWJGLShaderLoader());
		setGameRenderer(new LWJGLGameRenderer(this));
		setModelLoader(new LWJGLModelLoader(this));
		setGuiManager(new LWJGLGuiManager(this));
		setFontFactory(new BasicFontFactory(this));
		setTextureManager(new LWJGLTextureManager(this));
		setOperatingSystem(OperatingSystem.WINDOWS);
		this.glThreadGroup = new GlThreadGroup();
	}

	@Override
	protected void start0() throws GameException {
		GLUtil.clinit(this);

		getProfiler().addHandler("render", new GLSectionHandler());
		this.glfwThread = new GLFWThread();
		this.glfwThread.start();
		Configuration.OPENGL_EXPLICIT_INIT.set(true);
		Configuration.OPENGLES_EXPLICIT_INIT.set(true);
		GL.create();
		GLES.create(GL.getFunctionProvider());
		GL.destroy();

		window = new GLFWWindow(this, NAME, 400, 400);
		asyncUploader = new LWJGLAsyncUploader(this);
		setWindow(window);
		window.getRenderThread().submit(() -> setGlyphProvider(new LWJGLGlyphProvider(this, asyncUploader)));
		window.setRenderMode(RenderMode.ON_UPDATE);
		window.getFrameCounter().limit(5);
		Threads.waitFor(window.createWindow());
		asyncUploader.start();
		window.getFrameCounter().addUpdateListener(fps -> {
			getLogger().debugf("FPS: %s", fps);
		});
		getProfiler().addHandler(null, new SectionHandler() {

			@Override
			public void handleEnd(String type, String section, long tookNanos) {
			}

			@Override
			public void handleBegin(String type, String section) {
			}

		});
		window.setCloseCallback(() -> {
			window.hide();
			LWJGLGameLauncher.this.stop();
		});
		window.getRenderThread().start();

		getEventManager().registerListener(this);
	}

	@Override
	protected void stop0() throws GameException {
		getGlyphProvider().cleanup();
		getTextureManager().cleanup();

		asyncUploader.cleanup();
		Threads.waitFor(window.destroy());
		Threads.waitFor(this.glfwThread.exit());
	}
	
	@SuppressWarnings("javadoc")
	@EventHandler
	public void handle(LauncherInitializedEvent event) {
		window.showAndEndFrame();

//		glfwThread.submit(() -> glfwSetWindowAttrib(window.getGLFWId(), GLFW_FLOATING, GLFW_TRUE));
		mouseMovement(false);
	}

	@Override
	@Deprecated
	public LWJGLDrawContext createContext(Framebuffer framebuffer) {
		LWJGLDrawContext ctx = new LWJGLDrawContext(framebuffer);
		return ctx;
	}

	@Override
	protected void registerSettingInsertions() {
		new MouseSensivityInsertion().register(this);
	}

	private void mouseMovement(boolean movement) {
		window.getMouse().grabbed(movement).thenRun(() -> {
			if (!movement) {
				glfwSetCursorPos(window.getGLFWId(), window.width().doubleValue() / 2,
						window.height().doubleValue() / 2);
			} else {
				glfwSetCursorPos(window.getGLFWId(), 0, 0);
				ignoreNextMovement = true;
			}
		});
		this.mouseMovement = movement;
	}

	/**
	 * @return the window
	 */
	public GLFWWindow getWindow() {
		return window;
	}

	@Override
	protected void tick0() throws GameException {
//		double avgNanos = getGameThread().getAverageTickTime();
//		long avgMillis = TimeUnit.NANOSECONDS.toMicros((long)avgNanos);
//		System.out.printf("%05d%n", avgMillis);
//		System.out.println("Tick");
		window.getInput().handleInput();
		mouse: if (mouseMovement) {
			Camera cam = camera;
			float dy = (float) (window.getMouse().getDeltaX() * 0.4) * mouseSensivity;
			float dx = (float) (window.getMouse().getDeltaY() * 0.4) * mouseSensivity;
			if ((dx != 0 || dy != 0) && ignoreNextMovement) {
				ignoreNextMovement = false;
				break mouse;
			}
			Vector3f rot = new Vector3f(cam.getRotX(), cam.getRotY(), cam.getRotZ());
			cam.setRotation(Math.clamp(rot.x + dx, -90F, 90F), rot.y + dy, rot.z);
		}
	}

	/**
	 * @return the GLFW thread
	 */
	public GLFWThread getGLFWThread() {
		return glfwThread;
	}

	/**
	 * @return the {@link LWJGLAsyncUploader}
	 */
	public LWJGLAsyncUploader getAsyncUploader() {
		return asyncUploader;
	}

	/**
	 * @return the {@link GlThreadGroup}
	 */
	public GlThreadGroup getGlThreadGroup() {
		return glThreadGroup;
	}

	@Override
	public LWJGLGuiManager getGuiManager() {
		return (LWJGLGuiManager) super.getGuiManager();
	}

	@Override
	public LWJGLTextureManager getTextureManager() {
		return (LWJGLTextureManager) super.getTextureManager();
	}

}
