package gamelauncher.engine.plugin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.logging.Logger;

/**
 * A plugin must extend this. 
 * 
 * A plugin must also have the {@link GameException} annotation
 * 
 * @author DasBabyPixel
 */
public abstract class Plugin {

	private final String name;
	private final Logger logger;
	private GameLauncher launcher;

	/**
	 * @param name
	 */
	public Plugin(String name) {
		this.logger = Logger.getLogger(getClass());
		this.name = name;
	}

	/**
	 * Called when the plugin is enabled
	 * @throws GameException 
	 */
	public void onEnable() throws GameException {
	}

	/**
	 * Called when the plugin is disabled
	 * @throws GameException 
	 */
	public void onDisable() throws GameException {
	}

	/**
	 * Sets the {@link GameLauncher}
	 * 
	 * @param launcher
	 */
	public void setLauncher(GameLauncher launcher) {
		this.launcher = launcher;
	}

	/**
	 * @return the {@link GameLauncher}
	 */
	public GameLauncher getLauncher() {
		return launcher;
	}

	/**
	 * @return the {@link Logger} of this plugin
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * @return the name of this plugin
	 */
	public String getName() {
		return name;
	}

	/**
	 * Add this to every plugin
	 * 
	 * @author DasBabyPixel
	 */
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface GamePlugin {

	}
}
