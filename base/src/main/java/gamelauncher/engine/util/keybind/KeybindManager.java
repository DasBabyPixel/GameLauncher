package gamelauncher.engine.util.keybind;

import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public interface KeybindManager extends GameResource {

    /**
     * @param keybind the keybind id
     * @return the created {@link Keybind}
     */
    Keybind getKeybind(int keybind);

    void post(KeybindEvent event) throws GameException;

}
