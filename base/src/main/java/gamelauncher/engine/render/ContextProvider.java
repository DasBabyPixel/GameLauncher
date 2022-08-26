package gamelauncher.engine.render;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public class ContextProvider {

	private final GameLauncher launcher;

	/**
	 * @param launcher
	 */
	public ContextProvider(GameLauncher launcher) {
		this.launcher = launcher;
	}

	/**
	 * Must be explicitly freed by {@link #freeContext(DrawContext, ContextType)}
	 * 
	 * @param framebuffer
	 * @param type
	 * @return a new context for the given type
	 * @throws GameException
	 */
	public DrawContext loadContext(Framebuffer framebuffer, ContextType type) throws GameException {
		@SuppressWarnings("deprecation")
		DrawContext ctx = launcher.createContext(framebuffer);
		type.load(launcher, ctx);
		return ctx;
	}

	/**
	 * Frees a context
	 * 
	 * @param context
	 * @param type 
	 * @throws GameException 
	 */
	public void freeContext(DrawContext context, ContextType type) throws GameException {
		type.cleanup(context);
		context.cleanup();
	}

	/**
	 * @return the launcher
	 */
	public GameLauncher getLauncher() {
		return launcher;
	}

	/**
	 * @author DasBabyPixel
	 */
	public static enum ContextType {

		/**
		 * 2D Projection HUD Context
		 */
		HUD {

			@Override
			protected void load(GameLauncher launcher, DrawContext context) throws GameException {
				context.setProgram(launcher.getShaderLoader()
						.loadShader(launcher, launcher.getEmbedFileSystem().getPath("shaders", "hud", "hud.json")));
				context.setProjection(new Transformations.Projection.Projection2D());
			}

			@Override
			protected void cleanup(DrawContext context) throws GameException {
				context.getProgram().cleanup();
			}

		},;

		protected abstract void load(GameLauncher launcher, DrawContext context) throws GameException;

		protected abstract void cleanup(DrawContext context) throws GameException;

	}

}