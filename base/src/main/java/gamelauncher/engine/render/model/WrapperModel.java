package gamelauncher.engine.render.model;

import de.dasbabypixel.api.property.Property;

/**
 * @author DasBabyPixel
 */
public interface WrapperModel extends Model {

    /**
     * @return the handle
     */
    Model handle();

    Property<Model> handleProperty();

}
