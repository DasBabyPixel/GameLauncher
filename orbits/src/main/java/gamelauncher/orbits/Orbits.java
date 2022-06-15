package gamelauncher.orbits;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.plugins.Plugin;
import gamelauncher.engine.plugins.Plugin.GamePlugin;

@GamePlugin
public class Orbits extends Plugin {

	public Orbits() {
		super("Orbits");
	}

	@Override
	public void onEnable() {
		System.out.println("Enable");
		System.out.println("test2");
		GameLauncher launcher = null;
		System.out.println(launcher);
	}

	@Override
	public void onDisable() {
		System.out.println("Disable");
	}
}
