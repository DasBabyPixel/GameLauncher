package game.level;

import game.render.*;

public class WorldRenderer implements Renderer {

	public final World world;

	public WorldRenderer(World world) {
		this.world = world;
	}

	@Override
	public void render(Window window) {
		for (int x = 0; x < world.width; x++) {
			for (int y = 0; y < world.height; y++) {

			}
		}
	}
}
