package gamelauncher.labyrinth;

import java.nio.file.FileSystem;

import gamelauncher.engine.GameException;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.DrawContext;
import gamelauncher.engine.render.GameItem;
import gamelauncher.engine.render.Renderer;
import gamelauncher.engine.render.Transformations.Projection;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.model.ModelLoader;
import gamelauncher.engine.render.shader.ShaderLoader;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.resource.ResourceLoader;

public class LabyrinthRender extends Renderer {

	private Labyrinth labyrinth;
	private GameLauncher launcher;
	private ResourceLoader resourceLoader;
	private FileSystem embedFileSystem;
	private ModelLoader modelLoader;
	private ShaderLoader shaderLoader;
	private ShaderProgram program;
	private DrawContext contexthud;

	Model model1;
	GameItem gi1;
	Model model2;
	GameItem gi2;

	public LabyrinthRender(Labyrinth labyrinth) {
		this.labyrinth = labyrinth;
		this.launcher = this.labyrinth.getLauncher();
		this.resourceLoader = this.launcher.getResourceLoader();
		this.embedFileSystem = this.launcher.getEmbedFileSystem();
		this.shaderLoader = this.launcher.getShaderLoader();
		this.modelLoader = this.launcher.getModelLoader();
	}

	@Override
	public void init(Window window) throws GameException {
		program = shaderLoader.loadShader(launcher, embedFileSystem.getPath("shaders/hud/hud.json"));
		contexthud = window.getContext().duplicate();
		contexthud.setProgram(program);
		contexthud.setProjection(new Projection.Projection2D());

		Model model = modelLoader.loadModel(resourceLoader.getResource(embedFileSystem.getPath("cube.obj")));
		
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
