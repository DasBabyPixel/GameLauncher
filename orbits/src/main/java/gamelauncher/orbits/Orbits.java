package gamelauncher.orbits;

import gamelauncher.engine.plugin.Plugin;
import gamelauncher.engine.plugin.Plugin.GamePlugin;

@GamePlugin
public class Orbits extends Plugin {

	public Orbits() {
		super("Orbits");
	}

	@Override
	public void onEnable() {
		System.out.println("Enable");
	}

	@Override
	public void onDisable() {
		System.out.println("Disable");
	}
}
