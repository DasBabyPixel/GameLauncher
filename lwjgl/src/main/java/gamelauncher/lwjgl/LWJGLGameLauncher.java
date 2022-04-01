package gamelauncher.lwjgl;

import static org.lwjgl.opengl.GL11.*;

import gamelauncher.engine.GameException;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.RenderMode;
import gamelauncher.engine.resource.EmbedResourceLoader;
import gamelauncher.lwjgl.file.LWJGLFileSystem;
import gamelauncher.lwjgl.render.LWJGLGameRenderer;
import gamelauncher.lwjgl.render.LWJGLWindow;

public class LWJGLGameLauncher extends GameLauncher {

	public LWJGLGameLauncher() {
		setFileSystem(new LWJGLFileSystem());
		setGameRenderer(new LWJGLGameRenderer(this));
	}

	@Override
	protected void start0() throws GameException {
		setResourceLoader(new EmbedResourceLoader());
		LWJGLWindow window = new LWJGLWindow(400, 400, NAME);
		setWindow(window);
		window.renderLater(() -> {
			glClearColor(.2F, .2F, .2F, .8F);
		});
		window.setRenderMode(RenderMode.CONTINUOUSLY);
		window.createWindow();
		window.startRendering();
		window.show();
		window.setFloating(true);
		window.getFrameCounter().ifPresent(f -> {
			f.limit(60);
			f.addUpdateListener(fps -> {
				getLogger().info("FPS: " + fps);
			});
		});
	}
}
