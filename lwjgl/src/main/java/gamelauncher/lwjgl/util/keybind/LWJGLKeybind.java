package gamelauncher.lwjgl.util.keybind;

import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.Keybind;
import gamelauncher.engine.util.keybind.KeybindEntry;
import gamelauncher.engine.util.keybind.KeybindHandler;
import gamelauncher.engine.util.keybind.KeybindManager;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author DasBabyPixel
 */
public class LWJGLKeybind extends AbstractGameResource implements Keybind {

	private final String name;
	private final AtomicInteger id = new AtomicInteger();
	private final KeybindManager manager;
	private final Collection<KeybindHandler> handlers = ConcurrentHashMap.newKeySet();

	public LWJGLKeybind(String name, int id, KeybindManager manager) {
		this.name = name;
		this.id.set(id);
		this.manager = manager;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getUniqueId() {
		return id.get();
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
	public KeybindManager getManager() {
		return manager;
	}

	@Override
	public void cleanup0() throws GameException {
		handlers.clear();
	}
}
