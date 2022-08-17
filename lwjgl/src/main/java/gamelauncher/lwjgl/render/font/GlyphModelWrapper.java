package gamelauncher.lwjgl.render.font;

import gamelauncher.engine.render.model.GlyphStaticModel;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.model.WrapperModel;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.util.GameException;

@SuppressWarnings("javadoc")
public class GlyphModelWrapper implements GlyphStaticModel, WrapperModel {

	private final Model handle;

	private final int width;

	private final int height;

	private final float ascent;

	private final float descent;

	public GlyphModelWrapper(Model handle, int width, int height, float ascent, float descent) {
		super();
		this.handle = handle;
		this.width = width;
		this.height = height;
		this.ascent = ascent;
		this.descent = descent;
	}

	@Override
	public void render(ShaderProgram program) throws GameException {
		handle.render(program);
	}

	@Override
	public void cleanup() throws GameException {
		handle.cleanup();
	}

	@Override
	public float getDescent() {
		return descent;
	}

	@Override
	public float getAscent() {
		return ascent;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public Model getHandle() {
		return handle;
	}

}
