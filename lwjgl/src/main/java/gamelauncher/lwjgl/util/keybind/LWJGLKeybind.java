package gamelauncher.lwjgl.util.keybind;

import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.Keybind;
import gamelauncher.engine.util.keybind.KeybindEntry;
import gamelauncher.engine.util.keybind.KeybindHandler;
import gamelauncher.engine.util.keybind.KeybindManager;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DasBabyPixel
 */
public class LWJGLKeybind extends AbstractGameResource implements Keybind {

    private final String name;
    private final int id;
    private final KeybindManager manager;
    private final Collection<KeybindHandler> handlers = ConcurrentHashMap.newKeySet();

    public LWJGLKeybind(String name, int id, KeybindManager manager) {
        this.name = name;
        this.id = id;
        this.manager = manager;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int uniqueId() {
        return id;
    }

    @Override
    public void handle(KeybindEntry entry) {
        for (KeybindHandler handler : handlers) {
            handler.handle(entry);
        }
    }

    @Override
    public void addHandler(KeybindHandler handler) {
        handlers.add(handler);
    }

    @Override
    public void removeHandler(KeybindHandler handler) {
        handlers.remove(handler);
    }

    @Override
    public KeybindManager manager() {
        return manager;
    }

    @Override
    public void cleanup0() throws GameException {
        handlers.clear();
    }
}
