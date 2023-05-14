package gamelauncher.gles.font.bitmap;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.render.model.GlyphStaticModel;
import gamelauncher.engine.render.model.Model;

public class GlyphModelWrapper extends gamelauncher.gles.model.WrapperModel implements GlyphStaticModel {

    private final NumberValue ascent = NumberValue.withValue(0F);
    private final NumberValue descent = NumberValue.withValue(0F);
    private final NumberValue width = NumberValue.withValue(0F);
    private final NumberValue height = NumberValue.withValue(0F);

    public GlyphModelWrapper(Model handle, int width, int height, float ascent, float descent) {
        super();
        this.handle.value(handle);
        this.width.number(width);
        this.height.number(height);
        this.ascent.number(ascent);
        this.descent.number(descent);
    }

    @Override public NumberValue descent() {
        return descent;
    }

    @Override public NumberValue ascent() {
        return ascent;
    }

    @Override public NumberValue width() {
        return width;
    }

    @Override public NumberValue height() {
        return height;
    }

    public void width(float width) {
        this.width.number(width);
    }

    public void height(float height) {
        this.height.number(height);
    }
}
