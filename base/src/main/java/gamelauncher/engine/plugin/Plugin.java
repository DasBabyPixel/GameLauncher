package gamelauncher.engine.plugin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.util.logging.Logger;

public abstract class Plugin {

	private final String name;
	public final Logger logger;
	private GameLauncher launcher;

	public Plugin(String name) {
		this.logger = Logger.getLogger(getClass());
		this.name = name;
	}

	public void onEnable() {
	}

	public void onDisable() {
	}
	
	public void setLauncher(GameLauncher launcher) {
		this.launcher = launcher;
	}
	
	public GameLauncher getLauncher() {
		return launcher;
	}
	
	public Logger getLogger() {
		return logger;
	}

	public String getName() {
		return name;
	}

	@Retention(RetentionPolicy.RUNTIME)
	public static @interface GamePlugin {

	}
}
