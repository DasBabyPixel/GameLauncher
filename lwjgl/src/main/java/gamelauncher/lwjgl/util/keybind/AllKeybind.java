package gamelauncher.lwjgl.util.keybind;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.Keybind;
import gamelauncher.engine.util.keybind.KeybindEntry;
import gamelauncher.engine.util.keybind.KeybindHandler;
import gamelauncher.engine.util.keybind.KeybindManager;

/**
 * @author DasBabyPixel
 */
public class AllKeybind implements Keybind {

	/**
	 * The {@link ThreadLocal} of the {@link AllKeybind}
	 */
	public static final ThreadLocal<AllKeybind> ALL = ThreadLocal.withInitial(AllKeybind::new);

	/**
	 */
	public int id = 0;
	
	private AllKeybind() {
	}

	@Override
	public String getName() {
		return "<All>";
	}

	@Override
	public int getUniqueId() {
		return id;
	}

	@Override
	public void handle(KeybindEntry entry) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addHandler(KeybindHandler handler) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeHandler(KeybindHandler handler) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void cleanup() throws GameException {
	}

	@Override
	public KeybindManager getManager() {
		return null;
	}
}
