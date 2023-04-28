package gamelauncher.engine.event;

import de.dasbabypixel.annotations.Api;

/**
 * @author DasBabyPixel
 */
@Api
public interface Cancellable {

    /**
     * @return if the event is cancelled
     */
    @Api boolean isCancelled();

    /**
     * Sets if the event is cancelled
     */
    @Api void setCancelled(boolean cancel);

}
