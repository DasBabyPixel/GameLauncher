package gamelauncher.labyrinth;

import java.nio.file.FileSystem;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.BasicCamera;
import gamelauncher.engine.render.Camera;
import gamelauncher.engine.render.ContextProvider.ContextType;
import gamelauncher.engine.render.DrawContext;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.GameItem;
import gamelauncher.engine.render.Renderer;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.model.ModelLoader;
import gamelauncher.engine.render.shader.ShaderLoader;
import gamelauncher.engine.resource.ResourceLoader;
import gamelauncher.engine.util.GameException;

public class LabyrinthRender extends Renderer {

	private Labyrinth labyrinth;

	private GameLauncher launcher;

	private ResourceLoader resourceLoader;

	private FileSystem embedFileSystem;

	private ModelLoader modelLoader;

	private ShaderLoader shaderLoader;

	private DrawContext contexthud;

	private Camera camera = new BasicCamera();

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
	public void init(Framebuffer framebuffer) throws GameException {
		contexthud = launcher.getContextProvider().loadContext(framebuffer, ContextType.HUD);

		Model model = modelLoader.loadModel(resourceLoader.getResource(embedFileSystem.getPath("cube.obj")));

		gi1 = new GameItem(model);
		gi1.setScale(100);
		model1 = gi1.createModel();

		gi2 = new GameItem(model);
		gi2.setScale(200);
		gi2.setPosition(300, 0, 0);
		model2 = gi2.createModel();
	}

	@Override
	public void render(Framebuffer framebuffer) throws GameException {
		contexthud.update(camera);
		contexthud.drawModel(model1, 0, 0, 0);
		contexthud.drawModel(model2, 0, 0, 0);
		contexthud.getProgram().clearUniforms();
//		program.clearUniforms();
	}

	@Override
	public void cleanup(Framebuffer framebuffer) throws GameException {
		model1.cleanup();
		model2.cleanup();
		launcher.getContextProvider().freeContext(contexthud, ContextType.HUD);
	}

}
