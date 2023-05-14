package gamelauncher.engine.render.model;

import de.dasbabypixel.api.property.NumberValue;

/**
 * @author DasBabyPixel
 */
public interface GlyphStaticModel extends WrapperModel {

    /**
     * @return the width
     */
    NumberValue width();

    /**
     * @return the height
     */
    NumberValue height();

    /**
     * @return the descent
     */
    NumberValue descent();

    /**
     * @return the ascent
     */
    NumberValue ascent();

}
