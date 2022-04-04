package gamelauncher.lwjgl;

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
		window.show();
		window.getInput().addListener(new Listener() {
			@Override
			public void handle(InputType inputType, DeviceType deviceType, int key) {
				System.out.printf("%s %s %s%n", inputType, deviceType, key);
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
			f.addUpdateListener(fps -> {
//				getLogger().info("FPS: " + fps);
			});
		});
	}

	public LWJGLWindow getWindow() {
		return window;
	}

	@Override
	protected void tick() {
		window.getInput().handleInput();
		if (getCurrentTick() % GameLauncher.MAX_TPS == 0) {
			getLogger().info("Tick " + System.currentTimeMillis() % 1000);
		}
	}
}
