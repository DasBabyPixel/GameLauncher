package gamelauncher.lwjgl;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.event.EventHandler;
import gamelauncher.engine.event.events.LauncherInitializedEvent;
import gamelauncher.engine.render.BasicCamera;
import gamelauncher.engine.render.Camera;
import gamelauncher.engine.render.RenderMode;
import gamelauncher.engine.resource.SimpleResourceLoader;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.OperatingSystem;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.keybind.Keybind;
import gamelauncher.engine.util.keybind.KeyboardKeybindEntry;
import gamelauncher.engine.util.keybind.KeyboardKeybindEntry.Type;
import gamelauncher.engine.util.math.Math;
import gamelauncher.lwjgl.gui.LWJGLGuiManager;
import gamelauncher.lwjgl.render.GlThreadGroup;
import gamelauncher.lwjgl.render.LWJGLContextProvider;
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
		this.contextProvider(new LWJGLContextProvider(this));
		this.keybindManager(new LWJGLKeybindManager(this));
		this.resourceLoader(new SimpleResourceLoader());
		this.shaderLoader(new LWJGLShaderLoader());
		this.gameRenderer(new LWJGLGameRenderer(this));
		this.modelLoader(new LWJGLModelLoader(this));
		this.guiManager(new LWJGLGuiManager(this));
		this.fontFactory(new BasicFontFactory(this));
		this.textureManager(new LWJGLTextureManager(this));
		this.operatingSystem(OperatingSystem.WINDOWS);
		this.glThreadGroup = new GlThreadGroup();
	}

	@EventHandler
	public void handle(LauncherInitializedEvent event) {
		try {
			Threads.waitFor(this.mainFrame.showWindow());
		} catch (GameException ex) {
			ex.printStackTrace();
		}

		this.mouseMovement(false);
	}

	@Override
	protected void tick0() throws GameException {
		this.mainFrame.input().handleInput();
		mouse:
		if (this.mouseMovement) {
			Camera cam = this.camera;
			float dy = (float) (this.mainFrame.mouse().deltaX() * 0.4) * this.mouseSensivity;
			float dx = (float) (this.mainFrame.mouse().deltaY() * 0.4) * this.mouseSensivity;
			if ((dx != 0 || dy != 0) && this.ignoreNextMovement) {
				this.ignoreNextMovement = false;
				break mouse;
			}
			Vector3f rot = new Vector3f(cam.rotX(), cam.rotY(), cam.rotZ());
			cam.rotation(Math.clamp(rot.x + dx, -90F, 90F), rot.y + dy, rot.z);
		}
	}

	@Override
	protected void start0() throws GameException {
		GLUtil.clinit(this);

		this.profiler().addHandler("render", new GLSectionHandler());
		this.glfwThread = new GLFWThread();
		this.glfwThread.start();
		Configuration.OPENGL_EXPLICIT_INIT.set(true);
		Configuration.OPENGLES_EXPLICIT_INIT.set(true);
		GL.create();
		GLES.create(GL.getFunctionProvider());
		GL.destroy();

		this.mainFrame = new GLFWFrame(this);
		this.frame(this.mainFrame);
		this.mainFrame.framebuffer().renderThread()
				.submit(() -> this.glyphProvider(new LWJGLGlyphProvider(this)));
		this.mainFrame.renderMode(RenderMode.ON_UPDATE);
		this.mainFrame.closeCallback().setValue(frame -> {
			this.mainFrame.hideWindow();
			try {
				LWJGLGameLauncher.this.stop();
			} catch (GameException ex) {
				ex.printStackTrace();
			}
		});

		this.eventManager().registerListener(this);

		Keybind keybind = keybindManager().createKeybind(GLFW.GLFW_KEY_F11);
		keybind.addHandler(entry -> {
			if (entry instanceof KeyboardKeybindEntry) {
				KeyboardKeybindEntry e = (KeyboardKeybindEntry) entry;
				if (e.type() == Type.PRESS)
					mainFrame.fullscreen().setValue(!mainFrame.fullscreen().booleanValue());
			}
		});
	}

	@Override
	protected void stop0() throws GameException {

		this.glyphProvider().cleanup();
		this.textureManager().cleanup();

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
	public LWJGLTextureManager textureManager() {
		return (LWJGLTextureManager) super.textureManager();
	}

	@Override
	public LWJGLGuiManager guiManager() {
		return (LWJGLGuiManager) super.guiManager();
	}

	@Override
	public LWJGLGameRenderer gameRenderer() {
		return (LWJGLGameRenderer) super.gameRenderer();
	}

	private void mouseMovement(boolean movement) {
		this.mainFrame.mouse().grabbed(movement).thenRun(() -> {
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
	public GLFWFrame mainFrame() {
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
	public GlThreadGroup glThreadGroup() {
		return this.glThreadGroup;
	}

}
