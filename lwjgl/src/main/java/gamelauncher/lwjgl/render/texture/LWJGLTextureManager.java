package gamelauncher.lwjgl.render.texture;

import gamelauncher.engine.render.texture.TextureManager;
import gamelauncher.engine.util.GameException;

@SuppressWarnings("javadoc")
public class LWJGLTextureManager implements TextureManager {

	@Override
	public LWJGLTexture createTexture() {
		return new LWJGLTexture();
	}

	@Override
	public void cleanup() throws GameException {
	}
}
