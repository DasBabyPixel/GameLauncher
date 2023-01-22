package gamelauncher.engine.render;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public abstract class ContextProvider {

	private final GameLauncher launcher;

	public ContextProvider(GameLauncher launcher) {
		this.launcher = launcher;
	}

	/**
	 * Must be explicitly freed by {@link #freeContext(DrawContext, ContextType)}
	 *
	 * @param framebuffer the {@link Framebuffer} to use while loading the context
	 * @param type the {@link ContextType}
	 * @return a new context for the given type
	 * @throws GameException an exception
	 */
	public DrawContext loadContext(Framebuffer framebuffer, ContextType type) throws GameException {
		DrawContext ctx = createContext(framebuffer);
		type.load(this.launcher, ctx);
		return ctx;
	}

	public abstract DrawContext createContext(Framebuffer framebuffer);

	/**
	 * Frees a context
	 *
	 * @param context the {@link DrawContext} to free
	 * @param type the {@link ContextType}
	 * @throws GameException an exception
	 */
	public void freeContext(DrawContext context, ContextType type) throws GameException {
		type.cleanup(context);
		context.cleanup();
	}

	/**
	 * @return the launcher
	 */
	public GameLauncher launcher() {
		return this.launcher;
	}

	/**
	 * @author DasBabyPixel
	 */
	public enum ContextType {

		/**
		 * 2D Projection HUD Context
		 */
		HUD {
			@Override
			protected void load(GameLauncher launcher, DrawContext context) throws GameException {
				context.program(launcher.shaderLoader().loadShader(launcher,
						launcher.embedFileSystem().getPath("shaders", "hud", "hud.json")));
				context.projection(new Transformations.Projection.Projection2D());
			}

			@Override
			protected void cleanup(DrawContext context) throws GameException {
				context.program().cleanup();
			}

		},
		;

		protected abstract void load(GameLauncher launcher, DrawContext context)
				throws GameException;

		protected abstract void cleanup(DrawContext context) throws GameException;

	}

}
