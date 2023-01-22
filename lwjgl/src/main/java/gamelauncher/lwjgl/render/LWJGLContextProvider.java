package gamelauncher.lwjgl.render;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.ContextProvider;
import gamelauncher.engine.render.DrawContext;
import gamelauncher.engine.render.Framebuffer;

public class LWJGLContextProvider extends ContextProvider {
	public LWJGLContextProvider(GameLauncher launcher) {
		super(launcher);
	}

	@Override
	public DrawContext createContext(Framebuffer framebuffer) {
		return new LWJGLDrawContext(framebuffer);
	}
}
