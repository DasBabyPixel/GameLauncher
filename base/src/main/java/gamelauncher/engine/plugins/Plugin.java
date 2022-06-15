package gamelauncher.engine.plugins;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import gamelauncher.engine.GameLauncher;

public abstract class Plugin {

	private final String name;
	private GameLauncher launcher;

	public Plugin(String name) {
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

	public String getName() {
		return name;
	}

	@Retention(RetentionPolicy.RUNTIME)
	public static @interface GamePlugin {

	}
}
