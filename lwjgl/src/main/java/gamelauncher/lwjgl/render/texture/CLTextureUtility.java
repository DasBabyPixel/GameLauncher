package gamelauncher.lwjgl.render.texture;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameResource;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.framebuffer.LWJGLFramebuffer;
import gamelauncher.lwjgl.render.states.ContextLocal;

@SuppressWarnings("javadoc")
public class CLTextureUtility implements GameResource {

	public final CLFramebuffer framebuffer1;
	public final CLFramebuffer framebuffer2;

	public CLTextureUtility(LWJGLGameLauncher launcher) {
		framebuffer1 = new CLFramebuffer(launcher);
		framebuffer2 = new CLFramebuffer(launcher);
	}

	@Override
	public void cleanup() throws GameException {
		framebuffer1.cleanup();
		framebuffer2.cleanup();
	}

	public static class CLFramebuffer extends LWJGLFramebuffer {

		public CLFramebuffer(LWJGLGameLauncher launcher) {
			super(launcher.getWindow());
		}

		@Override
		public void cleanup() throws GameException {
			super.cleanup();
		}

	}

	/**
	 * @return a new {@link ContextLocal}
	 */
	public static ContextLocal<CLTextureUtility> local(LWJGLGameLauncher launcher) {
		return new ContextLocal<CLTextureUtility>(launcher) {

			@Override
			protected void valueRemoved(CLTextureUtility value) {
				try {
					value.cleanup();
				} catch (GameException ex) {
					throw new RuntimeException(ex);
				}
			}

			@Override
			protected CLTextureUtility initialValue() {
				return new CLTextureUtility(getLauncher());
			}

		};
	}

}
