package gamelauncher.engine.event.events;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.event.Event;

/**
 * @author DasBabyPixel
 */
public abstract class LauncherEvent extends Event {
	private final GameLauncher launcher;

	/**
	 * @param launcher the launcher
	 */
	public LauncherEvent(GameLauncher launcher) {
		this.launcher = launcher;
	}
	
	/**
	 * @return the {@link GameLauncher}
	 */
	public GameLauncher launcher() {
		return launcher;
	}
}
