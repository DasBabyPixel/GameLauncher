package gamelauncher.engine.render;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.logging.Logger;

/**
 * @author DasBabyPixel
 */
public record ContextProvider(GameLauncher launcher) {

	private static final Logger logger = Logger.logger();

	/**
	 * @param launcher
	 */
	public ContextProvider {
	}

	/**
	 * Must be explicitly freed by {@link #freeContext(DrawContext, ContextType)}
	 *
	 * @param framebuffer
	 * @param type
	 *
	 * @return a new context for the given type
	 *
	 * @throws GameException
	 */
	public DrawContext loadContext(Framebuffer framebuffer, ContextType type) throws GameException {
		DrawContext ctx = this.launcher.createContext(framebuffer);
		type.load(this.launcher, ctx);
		return ctx;
	}

	/**
	 * Frees a context
	 *
	 * @param context
	 * @param type
	 *
	 * @throws GameException
	 */
	public void freeContext(DrawContext context, ContextType type) throws GameException {
		type.cleanup(context);
		context.cleanup();
	}

	/**
	 * @return the launcher
	 */
	@Override
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
