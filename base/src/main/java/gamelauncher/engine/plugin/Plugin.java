package gamelauncher.engine.plugin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.i18n.Message;
import gamelauncher.engine.util.i18n.SimpleMessage;
import gamelauncher.engine.util.logging.Logger;

/**
 * A plugin must extend this.
 * <p>
 * A plugin must also have the {@link GameException} annotation
 *
 * @author DasBabyPixel
 */
public abstract class Plugin {

	private final String name;
	private final Logger logger;
	private GameLauncher launcher;
	private final Message displayName;

	/**
	 * @param name the plugins name (identity)
	 */
	public Plugin(String name) {
		this.logger = Logger.logger(this.getClass());
		this.name = name;
		this.displayName = new SimpleMessage(this, name);
	}

	public Message displayName() {
		return displayName;
	}

	/**
	 * Called when the plugin is enabled
	 *
	 * @throws GameException an exception
	 */
	public void onEnable() throws GameException {
	}

	/**
	 * Called when the plugin is disabled
	 *
	 * @throws GameException an exception
	 */
	public void onDisable() throws GameException {
	}

	/**
	 * Sets the {@link GameLauncher}
	 *
	 * @param launcher sets the launcher
	 */
	public void launcher(GameLauncher launcher) {
		this.launcher = launcher;
	}

	/**
	 * @return the {@link GameLauncher}
	 */
	public GameLauncher launcher() {
		return this.launcher;
	}

	/**
	 * @return the {@link Logger} of this plugin
	 */
	public Logger logger() {
		return this.logger;
	}

	/**
	 * @return the name of this plugin
	 */
	public String name() {
		return this.name;
	}

	/**
	 * Add this to every plugin
	 *
	 * @author DasBabyPixel
	 */
	@Retention(RetentionPolicy.RUNTIME)
	public @interface GamePlugin {

	}

	@Override
	public int hashCode() {
		return Objects.hash(this.name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		Plugin other = (Plugin) obj;
		return Objects.equals(this.name, other.name);
	}
}
