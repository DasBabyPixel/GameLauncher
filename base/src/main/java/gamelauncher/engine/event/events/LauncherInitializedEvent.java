package gamelauncher.engine.event.events;

import gamelauncher.engine.GameLauncher;

/**
 * Called when the launcher is fully initialized
 *
 * @author DasBabyPixel
 */
public class LauncherInitializedEvent extends LauncherEvent {

    public LauncherInitializedEvent(GameLauncher launcher) {
        super(launcher);
    }

}
