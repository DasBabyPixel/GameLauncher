package gamelauncher.engine.render.model;

/**
 * @author DasBabyPixel
 */
public interface GlyphStaticModel extends Model {

    /**
     * @return the width
     */
    int width();

    /**
     * @return the height
     */
    int height();

    /**
     * @return the descent
     */
    float descent();

    /**
     * @return the ascent
     */
    float ascent();

}
