package gamelauncher.engine.event.events.util.keybind;

import gamelauncher.engine.event.Event;
import gamelauncher.engine.util.keybind.KeybindEntry;

/**
 * @author DasBabyPixel
 *
 */
public class KeybindEntryEvent extends Event {

	private final KeybindEntry entry;

	/**
	 * @param entry
	 */
	public KeybindEntryEvent(KeybindEntry entry) {
		this.entry = entry;
	}

	/**
	 * @return the KeybindEntry of the event
	 */
	public KeybindEntry getEntry() {
		return entry;
	}
}
