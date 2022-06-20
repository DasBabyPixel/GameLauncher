package gamelauncher.lwjgl.render;

import static org.lwjgl.opengl.GL11.*;

import java.util.concurrent.atomic.AtomicReference;

import gamelauncher.engine.GameException;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.file.Files;
import gamelauncher.engine.render.Camera;
import gamelauncher.engine.render.GameRenderer;
import gamelauncher.engine.render.Model;
import gamelauncher.engine.render.Renderer;
import gamelauncher.engine.render.Transformations;
import gamelauncher.engine.render.Window;
import gamelauncher.lwjgl.render.font.BasicFont;
import gamelauncher.lwjgl.render.framebuffer.BasicFramebuffer;
import gamelauncher.lwjgl.render.model.Texture2DModel;
import gamelauncher.lwjgl.render.shader.ShaderLoader;
import gamelauncher.lwjgl.render.shader.ShaderProgram;

public class LWJGLGameRenderer implements GameRenderer {

	public static final boolean WIREFRAMES = false;

	private final AtomicReference<Renderer> renderer = new AtomicReference<>();
	private Renderer crenderer;
	private GameLauncher launcher;

	private BasicFramebuffer mainFramebuffer;
	private GameItem mainScreenItem;
	private GameItem.GameItemModel mainScreenItemModel;

	private Model model;
	private Model model2;
	private ShaderProgram shaderhud;
	private LWJGLDrawContext contexthud;
	private GlContext glContext = new GlContext();

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
		model = launcher.getGlyphProvider()
				.loadStaticModel(
						new BasicFont(
								Files.readAllBytes(launcher.getEmbedFileSystem().getPath("fonts/cinzel_regular.ttf"))),
						"tesQT", 400);
		model2 = launcher.getModelLoader()
				.loadModel(launcher.getResourceLoader().getResource(launcher.getEmbedFileSystem().getPath("cube.obj")));

		shaderhud = ShaderLoader.loadShader(launcher, launcher.getEmbedFileSystem().getPath("shaders/hud/hud.json"));

		LWJGLDrawContext context = (LWJGLDrawContext) window.getContext();

		contexthud = context.duplicate();
		contexthud.setProgram(shaderhud);
		contexthud.setProjection(new Transformations.Projection.Projection2D());

		glContext.depth.enabled.value.set(true);
		glContext.depth.depthFunc.set(GL_LEQUAL);
		glContext.blend.enabled.value.set(true);
		glContext.blend.srcrgb.set(GL_SRC_ALPHA);
		glContext.blend.dstrgb.set(GL_ONE_MINUS_SRC_ALPHA);
		glContext.replace(null);

		mainFramebuffer = new BasicFramebuffer(window.getFramebufferWidth(), window.getFramebufferHeight());
		mainScreenItem = new GameItem(new Texture2DModel(mainFramebuffer.getColorTexture()));
		mainScreenItemModel = new GameItem.GameItemModel(mainScreenItem);

		updateScreenItems(window);

		launcher.getLogger().info("RenderEngine initialized");

		if (WIREFRAMES) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		}
	}

	@Override
	public void close(Window window) throws GameException {
		launcher.getLogger().info("Cleaning up RenderEngine");
		contexthud.cleanup();
		shaderhud.cleanup();
		mainFramebuffer.cleanup();
		launcher.getLogger().info("RenderEngine cleaned up");
	}

	@Override
	public void windowSizeChanged(Window window) throws GameException {
		mainFramebuffer.resize(window.getFramebufferWidth(), window.getFramebufferHeight());
		updateScreenItems(window);
	}

	private void updateScreenItems(Window window) {
		mainScreenItem.setScale(window.getFramebufferWidth(), window.getFramebufferHeight(), 1);
//		mainScreenItem.setPosition(window.getFramebufferWidth()/2F, window.getFramebufferHeight()/2F, 0);
		mainScreenItem.setPosition(0, 0, 0);
	}

	@Override
	public void renderFrame(Window window) throws GameException {
		window.beginFrame();
		glClearColor(0, 0, 0, 0);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		Camera camera = launcher.getCamera();

		mainFramebuffer.bind();
		glClearColor(0.2F, 0.2F, 0.2F, 0.8F);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		Renderer renderer = this.renderer.get();
		if (renderer != crenderer) {
			cleanup(crenderer, window);
			init(renderer, window);
			crenderer = renderer;
		}
		if (renderer != null) {
			renderer.render(window);
		}

		contexthud.update(camera);
		contexthud.drawModel(model, 0, 100, 0);
		GameItem gi = new GameItem(model2);
		gi.setScale(500);
		contexthud.drawModel(new GameItem.GameItemModel(gi), 0, 0, 0);
		shaderhud.clearUniforms();

		mainFramebuffer.unbind();

		contexthud.update(camera);
		contexthud.drawModel(mainScreenItemModel, 0, 0, 0);
		shaderhud.clearUniforms();

		window.endFrame();
	}

	private void cleanup(Renderer renderer, Window window) throws GameException {
		if (renderer == null) {
			return;
		}
		renderer.close(window);
	}

	private void init(Renderer renderer, Window window) throws GameException {
		if (renderer == null) {
			return;
		}
		renderer.init(window);
	}
}
