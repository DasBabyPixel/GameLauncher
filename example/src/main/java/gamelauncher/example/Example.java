package gamelauncher.example;

import gamelauncher.engine.plugin.Plugin;
import gamelauncher.engine.plugin.Plugin.GamePlugin;

@GamePlugin
public class Example extends Plugin {

	public Example() {
		super("example");
	}
	
	@Override
	public void onEnable() {
		
	}
	
	@Override
	public void onDisable() {
	}
}
