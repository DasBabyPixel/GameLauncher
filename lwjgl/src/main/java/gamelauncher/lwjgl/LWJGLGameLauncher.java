package gamelauncher.lwjgl;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector3f;

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
import gamelauncher.engine.util.math.Math;
import gamelauncher.lwjgl.gui.LWJGLGuiManager;
import gamelauncher.lwjgl.render.LWJGLDrawContext;
import gamelauncher.lwjgl.render.LWJGLGameRenderer;
import gamelauncher.lwjgl.render.LWJGLWindow;
import gamelauncher.lwjgl.render.LWJGLWindow.CloseCallback;
import gamelauncher.lwjgl.render.font.BasicGlyphProvider;
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

	private LWJGLWindow window;
	private boolean mouseMovement = false;
	private float mouseSensivity = 1.0F;
	private boolean ignoreNextMovement = false;
	private Camera camera = new BasicCamera(() -> window.scheduleDraw());

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
		window = new LWJGLWindow(this, 400, 400, NAME);
		setWindow(window);
//		setCamera(new BasicCamera(() -> window.scheduleDraw()));
		window.renderLater(() -> setGlyphProvider(new BasicGlyphProvider()));
		window.setRenderMode(RenderMode.ON_UPDATE);
		window.createWindow();
		window.swapBuffers(false);
		window.startRendering();
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

		getEventManager().registerListener(this);
	}

	@SuppressWarnings("javadoc")
	@EventHandler
	public void handle(LauncherInitializedEvent event) {
		window.swapBuffers(true);
		window.showAndEndFrame();

		window.setFloating(true);
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
				glfwSetCursorPos(window.getId(), window.width().doubleValue() / 2, window.height().doubleValue() / 2);
			} else {
				glfwSetCursorPos(window.getId(), 0, 0);
				ignoreNextMovement = true;
			}
		});
		this.mouseMovement = movement;
	}

	/**
	 * @return the window
	 */
	public LWJGLWindow getWindow() {
		return window;
	}

	@Override
	protected void tick() throws GameException {
		System.out.println("Tick");
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

	@Override
	public LWJGLTextureManager getTextureManager() {
		return (LWJGLTextureManager) super.getTextureManager();
	}
}
