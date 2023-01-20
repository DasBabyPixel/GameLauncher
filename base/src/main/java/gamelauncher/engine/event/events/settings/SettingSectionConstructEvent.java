package gamelauncher.engine.event.events.settings;

import gamelauncher.engine.event.Event;
import gamelauncher.engine.settings.AbstractSettingSection.SettingSectionConstructor;

/**
 * @author DasBabyPixel
 *
 */
public class SettingSectionConstructEvent extends Event {

	private final SettingSectionConstructor constructor;

	/**
	 * @param constructor the settings-constructor
	 */
	public SettingSectionConstructEvent(SettingSectionConstructor constructor) {
		this.constructor = constructor;
	}

	/**
	 * @return the {@link SettingSectionConstructor}
	 */
	public SettingSectionConstructor constructor() {
		return constructor;
	}
}
