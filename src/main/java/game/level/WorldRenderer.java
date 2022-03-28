package game.level;

import java.io.IOException;

import game.render.RenderException;
import game.render.Renderer;
import game.render.Window;
import game.render.shader.ShaderProgram;
import game.resource.ResourcePath;
import game.resource.ResourceStream;

public class WorldRenderer extends Renderer {

	public final World world;
	private ShaderProgram program;

	public WorldRenderer(World world) {
		this.world = world;
	}

	@Override
	public void init() throws RenderException {
		try (ResourceStream stream = new ResourcePath("shaders/basic/").newResourceStream()) {
			program = stream.loadProgram();
		} catch (IOException ex) {
			throw new RenderException(ex);
		}
	}

	@Override
	public void close() throws RenderException {
		program.delete();
	}

	@Override
	public void render(Window window) throws RenderException {
		for (int x = 0; x < world.width; x++) {
			for (int y = 0; y < world.height; y++) {

			}
		}
	}
}
