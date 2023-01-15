package gamelauncher.lwjgl.util.keybind;

import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.Keybind;
import gamelauncher.engine.util.keybind.KeybindEntry;
import gamelauncher.engine.util.keybind.KeybindHandler;
import gamelauncher.engine.util.keybind.KeybindManager;

/**
 * @author DasBabyPixel
 */
public class AllKeybind extends AbstractGameResource implements Keybind {

	/**
	 * The {@link ThreadLocal} of the {@link AllKeybind}
	 */
	public static final ThreadLocal<AllKeybind> ALL = ThreadLocal.withInitial(AllKeybind::new);

	/**
	 *
	 */
	public int id = 0;

	private AllKeybind() {
		AbstractGameResource.logCleanup(this);
	}

	@Override
	public String name() {
		return "<All>";
	}

	@Override
	public int uniqueId() {
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
	public KeybindManager manager() {
		return null;
	}

	@Override
	public final boolean cleanedUp() {
		return true;
	}

	@Override
	protected final void cleanup0() throws GameException {
	}

}
