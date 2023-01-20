package gamelauncher.lwjgl.render.texture;

import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.framebuffer.LWJGLFramebuffer;
import gamelauncher.lwjgl.render.states.ContextLocal;

public class CLTextureUtility extends AbstractGameResource {

	public final CLFramebuffer framebuffer1;

	public final CLFramebuffer framebuffer2;

	public CLTextureUtility(LWJGLGameLauncher launcher) {
		this.framebuffer1 = new CLFramebuffer(launcher);
		this.framebuffer2 = new CLFramebuffer(launcher);
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
				return new CLTextureUtility(this.getLauncher());
			}

		};
	}

	@Override
	public void cleanup0() throws GameException {
		this.framebuffer1.cleanup();
		this.framebuffer2.cleanup();
	}


	public static class CLFramebuffer extends LWJGLFramebuffer {

		public CLFramebuffer(LWJGLGameLauncher launcher) {
			super(launcher.mainFrame());
		}

	}

}
