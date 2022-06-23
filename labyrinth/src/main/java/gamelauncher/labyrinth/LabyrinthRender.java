package gamelauncher.labyrinth;

import gamelauncher.engine.GameException;
import gamelauncher.engine.render.Model;
import gamelauncher.engine.render.Renderer;
import gamelauncher.engine.render.Transformations.Projection;
import gamelauncher.engine.render.Window;
import gamelauncher.lwjgl.render.GameItem;
import gamelauncher.lwjgl.render.LWJGLDrawContext;
import gamelauncher.lwjgl.render.shader.ShaderLoader;
import gamelauncher.lwjgl.render.shader.ShaderProgram;

public class LabyrinthRender extends Renderer {

	Labyrinth labyrinth;
	ShaderProgram program;
	LWJGLDrawContext contexthud;

	Model model1;
	GameItem gi1;
	Model model2;
	GameItem gi2;

	public LabyrinthRender(Labyrinth labyrinth) {
		this.labyrinth = labyrinth;
	}

	@Override
	public void init(Window window) throws GameException {
		program = ShaderLoader.loadShader(labyrinth.getLauncher(),
				labyrinth.getLauncher().getEmbedFileSystem().getPath("shaders/hud/hud.json"));
		contexthud = (LWJGLDrawContext) window.getContext().duplicate();
		contexthud.setProgram(program);
		contexthud.setProjection(new Projection.Projection2D());

		Model model = labyrinth.getLauncher()
				.getModelLoader()
				.loadModel(labyrinth.getLauncher()
						.getResourceLoader()
						.getResource(labyrinth.getLauncher().getEmbedFileSystem().getPath("cube.obj")));
		gi1 = new GameItem(model);
		gi1.setScale(100);
		model1 = new GameItem.GameItemModel(gi1);
		
		gi2 = new GameItem(model);
		gi2.setScale(200);
		gi2.setPosition(300, 0, 0);
		model2 = new GameItem.GameItemModel(gi2);
	}

	@Override
	public void render(Window window) throws GameException {
		contexthud.update(labyrinth.getLauncher().getCamera());
		contexthud.drawModel(model1, 0, 0, 0);
		contexthud.drawModel(model2, 0, 0, 0);
		program.clearUniforms();
	}

	@Override
	public void close(Window window) throws GameException {
		program.cleanup();
		model1.cleanup();
		model2.cleanup();
		contexthud.cleanup();
	}
}
