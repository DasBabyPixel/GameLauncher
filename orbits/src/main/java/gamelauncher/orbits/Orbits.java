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
		System.out.println("test2");
		System.out.println(getLauncher().getCurrentTick());
		System.out.println("test3");
		System.out.println("test4");
	}

	@Override
	public void onDisable() {
		System.out.println("Disable");
	}
}
