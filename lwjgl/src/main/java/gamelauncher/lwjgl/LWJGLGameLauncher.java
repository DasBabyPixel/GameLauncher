package gamelauncher.lwjgl;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import gamelauncher.engine.GameException;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.RenderMode;
import gamelauncher.engine.resource.EmbedResourceLoader;
import gamelauncher.lwjgl.file.LWJGLFileSystem;
import gamelauncher.lwjgl.render.LWJGLGameRenderer;
import gamelauncher.lwjgl.render.LWJGLInput.DeviceType;
import gamelauncher.lwjgl.render.LWJGLInput.InputType;
import gamelauncher.lwjgl.render.LWJGLInput.Listener;
import gamelauncher.lwjgl.render.LWJGLWindow;
import gamelauncher.lwjgl.render.LWJGLWindow.CloseCallback;

public class LWJGLGameLauncher extends GameLauncher {

	private LWJGLWindow window;

	public LWJGLGameLauncher() {
		setFileSystem(new LWJGLFileSystem());
		setGameRenderer(new LWJGLGameRenderer(this));
	}

	@Override
	protected void start0() throws GameException {
		setResourceLoader(new EmbedResourceLoader());
		window = new LWJGLWindow(400, 400, NAME);
		setWindow(window);
		window.renderLater(() -> {
			glClearColor(.2F, .2F, .2F, .8F);
		});
		window.setRenderMode(RenderMode.CONTINUOUSLY);
		window.createWindow();
		window.startRendering();
		window.getInput().addListener(new Listener() {
			@Override
			public void handle(InputType inputType, DeviceType deviceType, int key) {
				if (inputType == InputType.HELD) {
					if (key == GLFW_KEY_W) {
						window.getCamera().getPosition().z -= 0.02;
					} else if (key == GLFW_KEY_S) {
						window.getCamera().getPosition().z += 0.02;
					} else if (key == GLFW_KEY_A) {
						window.getCamera().getPosition().x -= 0.02;
					} else if (key == GLFW_KEY_D) {
						window.getCamera().getPosition().x += 0.02;
					} else if (key == GLFW_KEY_SPACE) {
						window.getCamera().getPosition().y += 0.02;
					} else if (key == GLFW_KEY_LEFT_SHIFT) {
						window.getCamera().getPosition().y -= 0.02;
					}
				}
//				System.out.printf("%s %s %s%n", inputType, deviceType, key);
			}
		});
		CloseCallback oldCloseCallback = window.getCloseCallback();
		window.setCloseCallback(new CloseCallback() {
			@Override
			public void close() throws GameException {
				oldCloseCallback.close();
				LWJGLGameLauncher.this.stop();
			}
		});
		window.getFrameCounter().ifPresent(f -> {
			f.limit(60);
		});
		System.out.println("Wait 1");
		window.waitForFrame();
		System.out.println("Wait 2");
		window.waitForFrame();
		System.out.println("Wait 3");
		window.waitForFrame();
		System.out.println("Wait Done");
		window.show();

	}

	public LWJGLWindow getWindow() {
		return window;
	}

	@Override
	protected void tick() {
		window.getInput().handleInput();
//		if (getCurrentTick() % GameLauncher.MAX_TPS == 0) {
//			LWJGLInput input = window.getInput();
//			getLogger().infof("%s %s", input.qentrysize.get(), input.entrysize.get());
//		}
	}
}
