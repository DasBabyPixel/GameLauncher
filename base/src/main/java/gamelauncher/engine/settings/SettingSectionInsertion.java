package gamelauncher.engine.settings;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.event.EventHandler;
import gamelauncher.engine.event.events.settings.SettingSectionConstructEvent;
import gamelauncher.engine.settings.AbstractSettingSection.SettingSectionConstructor;

/**
 * @author DasBabyPixel
 */
public abstract class SettingSectionInsertion {

	/**
	 * @param launcher
	 */
	public void register(GameLauncher launcher) {
		launcher.eventManager().registerListener(this);
	}

	protected abstract void construct(SettingSectionConstructor constructor);

	protected boolean shouldHandle(SettingSectionConstructEvent event) {
		return true;
	}

	@EventHandler
	private void handle(SettingSectionConstructEvent event) {
		if (shouldHandle(event)) {
			construct(event.constructor());
		}
	}
}
