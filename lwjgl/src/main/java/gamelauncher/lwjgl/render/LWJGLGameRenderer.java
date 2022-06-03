package gamelauncher.lwjgl.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

import gamelauncher.engine.GameException;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.Camera;
import gamelauncher.engine.render.GameRenderer;
import gamelauncher.engine.render.Model;
import gamelauncher.engine.render.Renderer;
import gamelauncher.engine.render.Transformations;
import gamelauncher.engine.render.Window;
import gamelauncher.lwjgl.render.font.Font;
import gamelauncher.lwjgl.render.font.GlyphProvider;

public class LWJGLGameRenderer implements GameRenderer {

	private final AtomicReference<Renderer> renderer = new AtomicReference<>();
	private Renderer crenderer;
	private GameLauncher launcher;
	GlyphProvider gprovider;
//	FontInfo finfo;

	private Collection<Model> models = new HashSet<>();
	private Collection<Model> hud = new HashSet<>();
	private ShaderProgram shaderhud;
	private LWJGLDrawContext contexthud;
	private ShaderProgram shader3d;
	private LWJGLDrawContext context3d;
//	private GameItem item;

	public LWJGLGameRenderer(GameLauncher launcher) {
		this.launcher = launcher;
	}

	@Override
	public void setRenderer(Renderer renderer) {
		this.renderer.set(renderer);
	}

	@Override
	public Renderer getRenderer() {
		return this.renderer.get();
	}

	@Override
	public void init(Window window) throws GameException {
		launcher.getLogger().info("Initializing RenderEngine");
		Model model = launcher.getModelLoader()
				.loadModel(launcher.getResourceLoader().getResource(launcher.getEmbedFileSystem().getPath("cube.obj")));
		models.add(model);
		gprovider = new GlyphProvider();
		Font font = new Font(launcher.getResourceLoader()
				.getResource(launcher.getEmbedFileSystem().getPath("fonts").resolve("cinzel_regular.ttf"))
				.newResourceStream());
		long millis = System.currentTimeMillis();
		model = gprovider.loadStaticModel(font, "AVtqe y", 500);
		System.out.println(System.currentTimeMillis() - millis);

		GameItem ii = new GameItem(model);
		ii.setPosition(30, 100, 0);
		models.add(new GameItem.GameItemModel(ii));

		hud.add(new GameItem.GameItemModel(ii));

		shader3d = new ShaderProgram(launcher);
		shader3d.createVertexShader(launcher.getResourceLoader()
				.getResource(launcher.getEmbedFileSystem().getPath("shaders/basic/vertex"))
				.newResourceStream()
				.readUTF8FullyClose());
		shader3d.createFragmentShader(launcher.getResourceLoader()
				.getResource(launcher.getEmbedFileSystem().getPath("shaders/basic/fragment"))
				.newResourceStream()
				.readUTF8FullyClose());
		shader3d.link();
		shader3d.deleteVertexShader();
		shader3d.deleteFragmentShader();

		shaderhud = new ShaderProgram(launcher);
		shaderhud.createVertexShader(launcher.getResourceLoader()
				.getResource(launcher.getEmbedFileSystem().getPath("shaders/hud/vertex"))
				.newResourceStream()
				.readUTF8FullyClose());
		shaderhud.createFragmentShader(launcher.getResourceLoader()
				.getResource(launcher.getEmbedFileSystem().getPath("shaders/hud/fragment"))
				.newResourceStream()
				.readUTF8FullyClose());
		shaderhud.link();
		shaderhud.deleteVertexShader();
		shaderhud.deleteFragmentShader();

		LWJGLDrawContext context = (LWJGLDrawContext) window.getContext();
		context3d = context.duplicate();
		context3d.setProgram(shader3d);
		context3d.setProjection(
				new Transformations.Projection.Projection3D((float) Math.toRadians(70.0F), 0.01F, 1000F));

		contexthud = context.duplicate();
		contexthud.setProgram(shaderhud);
		contexthud.setProjection(new Transformations.Projection.Projection2D());

		glEnable(GL_DEPTH_TEST);
//		glEnable(GL_CULL_FACE);
		glEnable(GL_BLEND);
		glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE);
//		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

//		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		launcher.getLogger().info("RenderEngine initialized");
	}

	@Override
	public void close(Window window) throws GameException {
		launcher.getLogger().info("Cleaning up RenderEngine");
		for (Model model : models) {
			model.cleanup();
		}
		gprovider.cleanup();
		shader3d.cleanup();
		shaderhud.cleanup();
		launcher.getLogger().info("RenderEngine cleaned up");
	}

	private void cleanup(Renderer renderer) throws GameException {
		if (renderer == null) {
			return;
		}
		renderer.close();
	}

	private void init(Renderer renderer) throws GameException {
		if (renderer == null) {
			return;
		}
		renderer.init();
	}

	@Override
	public void renderFrame(Window window) throws GameException {
		window.beginFrame();

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		Renderer renderer = this.renderer.get();
		if (renderer != crenderer) {
			cleanup(crenderer);
			init(renderer);
			crenderer = renderer;
		}
		if (renderer != null) {
			renderer.render(window);
		}

		Camera camera = ((LWJGLWindow) window).getCamera();
		render3d(camera);
		renderHud(camera);

		window.endFrame();
	}

	private void render3d(Camera camera) throws GameException {
		context3d.update(camera);
		for (Model model : models) {
			context3d.drawModel(model, 0, 0, 0, 0, 0, 0);
		}
	}

	private void renderHud(Camera camera) throws GameException {
		contexthud.update(camera);
		for (Model model : hud) {
			contexthud.drawModel(model, 0, 0, 0, 0, 0, 0);
		}
	}
}
