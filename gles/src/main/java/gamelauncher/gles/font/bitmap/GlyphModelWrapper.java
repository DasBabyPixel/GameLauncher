package gamelauncher.gles.font.bitmap;

import gamelauncher.engine.render.model.GlyphStaticModel;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.model.WrapperModel;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;

public class GlyphModelWrapper extends AbstractGameResource implements GlyphStaticModel, WrapperModel {

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

    @Override public void render(ShaderProgram program) throws GameException {
        handle.render(program);
    }

    @Override public void cleanup0() throws GameException {
        handle.cleanup();
    }

    @Override public float descent() {
        return descent;
    }

    @Override public float ascent() {
        return ascent;
    }

    @Override public int width() {
        return width;
    }

    @Override public int height() {
        return height;
    }

    @Override public Model getHandle() {
        return handle;
    }

}
