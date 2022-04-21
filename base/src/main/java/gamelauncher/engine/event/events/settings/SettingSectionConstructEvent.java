package gamelauncher.engine.event.events.settings;

import gamelauncher.engine.event.Event;
import gamelauncher.engine.settings.AbstractSettingSection.SettingSectionConstructor;

public class SettingSectionConstructEvent extends Event {

	private final SettingSectionConstructor constructor;

	public SettingSectionConstructEvent(SettingSectionConstructor constructor) {
		this.constructor = constructor;
	}

	public SettingSectionConstructor getConstructor() {
		return constructor;
	}
}
