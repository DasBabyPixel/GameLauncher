package gamelauncher.lwjgl;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengles.GLES;
import org.lwjgl.system.Configuration;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.event.EventHandler;
import gamelauncher.engine.event.events.LauncherInitializedEvent;
import gamelauncher.engine.render.BasicCamera;
import gamelauncher.engine.render.Camera;
import gamelauncher.engine.render.DrawContext;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.RenderMode;
import gamelauncher.engine.resource.SimpleResourceLoader;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.OperatingSystem;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.math.Math;
import gamelauncher.lwjgl.gui.LWJGLGuiManager;
import gamelauncher.lwjgl.render.LWJGLDrawContext;
import gamelauncher.lwjgl.render.LWJGLGameRenderer;
import gamelauncher.lwjgl.render.font.BasicGlyphProvider;
import gamelauncher.lwjgl.render.glfw.GLFWThread;
import gamelauncher.lwjgl.render.glfw.GWindow;
import gamelauncher.lwjgl.render.glfw.GWindow.CloseCallback;
import gamelauncher.lwjgl.render.glfw.LWJGLAsyncUploader;
import gamelauncher.lwjgl.render.modelloader.LWJGLModelLoader;
import gamelauncher.lwjgl.render.shader.LWJGLShaderLoader;
import gamelauncher.lwjgl.render.texture.LWJGLTextureManager;
import gamelauncher.lwjgl.settings.controls.MouseSensivityInsertion;
import gamelauncher.lwjgl.util.keybind.LWJGLKeybindManager;

/**
 * @author DasBabyPixel
 *
 */
public class LWJGLGameLauncher extends GameLauncher {

	private GWindow window;
	private boolean mouseMovement = false;
	private float mouseSensivity = 1.0F;
	private boolean ignoreNextMovement = false;
	private GLFWThread glfwThread;
	private Camera camera = new BasicCamera(() -> window.scheduleDraw());
	private LWJGLAsyncUploader asyncUploader;

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
		setTextureManager(new LWJGLTextureManager(this));
		setOperatingSystem(OperatingSystem.WINDOWS);
	}

	@Override
	protected void start0() throws GameException {
		this.glfwThread = new GLFWThread(this);
		this.glfwThread.start();
		Configuration.OPENGL_EXPLICIT_INIT.set(true);
		Configuration.OPENGLES_EXPLICIT_INIT.set(true);
		GL.create();
		GLES.create(GL.getFunctionProvider());

		window = new GWindow(this, NAME, 400, 400);
		asyncUploader  = new LWJGLAsyncUploader(this);
		setWindow(window);
		window.getRenderThread()
				.submit(() -> setGlyphProvider(new BasicGlyphProvider(asyncUploader)));
		window.setRenderMode(RenderMode.ON_UPDATE);
		window.swapBuffers(false);
		Threads.waitFor(window.createWindow());
		asyncUploader.start();
		window.getFrameCounter().addUpdateListener(fps -> {
			getLogger().debugf("FPS: %s", fps);
		});
		CloseCallback oldCloseCallback = window.getCloseCallback();
		window.setCloseCallback(new CloseCallback() {
			@Override
			public void close() throws GameException {
				oldCloseCallback.close();
				new Thread(() -> {
					try {
						LWJGLGameLauncher.this.stop();
					} catch (GameException ex) {
						ex.printStackTrace();
					}
				}).start();
			}
		});
		window.getRenderThread().start();

		getEventManager().registerListener(this);
	}

	@Override
	protected void stop0() throws GameException {
		Collection<CompletableFuture<?>> c = new ArrayList<>();
		c.add(window.closeWindow());
		getGlyphProvider().cleanup();
		getTextureManager().cleanup();
		asyncUploader.cleanup();
		Threads.waitFor(c.toArray(new CompletableFuture[0]));
		Threads.waitFor(this.glfwThread.exit());
	}

	@SuppressWarnings("javadoc")
	@EventHandler
	public void handle(LauncherInitializedEvent event) {
		window.swapBuffers(true);
		window.showAndEndFrame();

		glfwThread.submit(() -> glfwSetWindowAttrib(window.getGLFWId(), GLFW_FLOATING, GLFW_TRUE));
		mouseMovement(false);
	}

	@Override
	public DrawContext createContext(Framebuffer framebuffer) {
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
	public GWindow getWindow() {
		return window;
	}

	@Override
	protected void tick() throws GameException {
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

	@Override
	public LWJGLTextureManager getTextureManager() {
		return (LWJGLTextureManager) super.getTextureManager();
	}
}
