package gamelauncher.labyrinth;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.event.EventHandler;
import gamelauncher.engine.event.events.settings.SettingSectionConstructEvent;
import gamelauncher.engine.plugin.Plugin;
import gamelauncher.engine.plugin.Plugin.GamePlugin;
import gamelauncher.engine.render.GameRenderer;

@GamePlugin
public class Labyrinth extends Plugin {

	public Labyrinth() {
		super("labyrinth");
	}

	@Override
	public void onEnable() {
		System.out.println("Labyrinth enabled");
		GameLauncher launcher = getLauncher();
		GameRenderer gr = launcher.getGameRenderer();
		gr.setRenderer(new LabyrinthRender(this));
		
		launcher.getEventManager().registerListener(this);
	}

	@Override
	public void onDisable() {
		System.out.println("Labrinth disabled");
	}
	
	@EventHandler
	public void handle(SettingSectionConstructEvent event) {
		System.out.println("Construct setting section: " + event.getConstructor().getSection().toString());
	}
}
