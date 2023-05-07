package gamelauncher.example;

import gamelauncher.engine.game.Game;
import gamelauncher.engine.plugin.Plugin;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.util.GameException;
import gamelauncher.example.gui.ExampleGameGui;

public class ExampleGame extends Game {

    public ExampleGame(Plugin plugin) {
        super(plugin, "example");
    }

    @Override protected void launch0(Framebuffer framebuffer) throws GameException {
        System.out.println("launch example game");
        this.launcher().guiManager().openGui(framebuffer, new ExampleGameGui(this.launcher()));
    }

    @Override protected void close0() {
        System.out.println("close example game");
    }

}
