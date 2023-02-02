package gamelauncher.lwjgl.render.font.sdf;

import gamelauncher.engine.render.font.GlyphProvider;
import gamelauncher.engine.render.model.GlyphStaticModel;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.text.Component;

import java.util.concurrent.CompletableFuture;

public class LWJGLGlyphProvider extends AbstractGameResource implements GlyphProvider {
	@Override
	public GlyphStaticModel loadStaticModel(Component text, int pixelHeight) throws GameException {
		return null;
	}

	@Override
	protected void cleanup0() throws GameException {

	}
}
