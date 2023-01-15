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
import gamelauncher.engine.resource.ResourceLoader;
import gamelauncher.engine.util.GameException;

public class LabyrinthRender extends Renderer {

	private Labyrinth labyrinth;

	private GameLauncher launcher;

	private ResourceLoader resourceLoader;

	private FileSystem embedFileSystem;

	private ModelLoader modelLoader;

	private DrawContext contexthud;

	private Camera camera = new BasicCamera();

	Model model1;

	GameItem gi1;

	Model model2;

	GameItem gi2;

	public LabyrinthRender(Labyrinth labyrinth) {
		this.labyrinth = labyrinth;
		this.launcher = this.labyrinth.launcher();
		this.resourceLoader = this.launcher.resourceLoader();
		this.embedFileSystem = this.launcher.embedFileSystem();
		this.modelLoader = this.launcher.modelLoader();
	}

	@Override
	public void init(Framebuffer framebuffer) throws GameException {
		contexthud = launcher.contextProvider().loadContext(framebuffer, ContextType.HUD);

		Model model = modelLoader.loadModel(resourceLoader.resource(embedFileSystem.getPath("cube.obj")));

		gi1 = new GameItem(model);
		gi1.scale(100);
		model1 = gi1.createModel();

		gi2 = new GameItem(model);
		gi2.scale(200);
		gi2.position(300, 0, 0);
		model2 = gi2.createModel();
	}

	@Override
	public void render(Framebuffer framebuffer) throws GameException {
		contexthud.update(camera);
		contexthud.drawModel(model1, 0, 0, 0);
		contexthud.drawModel(model2, 0, 0, 0);
		contexthud.program().clearUniforms();
//		program.clearUniforms();
	}

	@Override
	public void cleanup(Framebuffer framebuffer) throws GameException {
		model1.cleanup();
		model2.cleanup();
		launcher.contextProvider().freeContext(contexthud, ContextType.HUD);
	}

}
