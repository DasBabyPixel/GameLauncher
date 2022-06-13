package gamelauncher.lwjgl.render;

import static org.lwjgl.opengl.GL11.*;

import java.util.concurrent.atomic.AtomicReference;

import gamelauncher.engine.GameException;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.Camera;
import gamelauncher.engine.render.GameRenderer;
import gamelauncher.engine.render.Renderer;
import gamelauncher.engine.render.Transformations;
import gamelauncher.engine.render.Window;
import gamelauncher.lwjgl.render.framebuffer.BasicFramebuffer;
import gamelauncher.lwjgl.render.model.Texture2DModel;
import gamelauncher.lwjgl.render.shader.ShaderLoader;
import gamelauncher.lwjgl.render.shader.ShaderProgram;

public class LWJGLGameRenderer implements GameRenderer {

	private final AtomicReference<Renderer> renderer = new AtomicReference<>();
	private Renderer crenderer;
	private GameLauncher launcher;

//	private GlyphProvider gprovider;
//	private Collection<Model> models = new HashSet<>();
//	private Collection<Model> hud = new HashSet<>();
//	private Collection<Model> secondaryModels = new HashSet<>();

	private BasicFramebuffer mainFramebuffer;
	private GameItem mainScreenItem;
	private GameItem.GameItemModel mainScreenItemModel;
//	private BasicFramebuffer secondaryFramebuffer;
//	private GameItem secondaryScreenItem;
//	private GameItem.GameItemModel secondaryScreenItemModel;

	private ShaderProgram shaderhud;
	private LWJGLDrawContext contexthud;
//	private ShaderProgram shader3d;
//	private LWJGLDrawContext context3d;
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
//		Model model = launcher.getModelLoader()
//				.loadModel(launcher.getResourceLoader().getResource(launcher.getEmbedFileSystem().getPath("cube.obj")));
//		models.add(model);
//		gprovider = new GlyphProvider();
//		Font font = new Font(launcher.getResourceLoader()
//				.getResource(launcher.getEmbedFileSystem().getPath("fonts").resolve("cinzel_regular.ttf"))
//				.newResourceStream());
//		model = gprovider.loadStaticModel(font, "TestQr+", 500);

//		GameItem ii = new GameItem(model);
//		ii.setPosition(1, 100, 0);
//		GameItem modelg = new GameItem(model);
//		modelg.setPosition(2, 0, 0);
//		modelg.setScale(.001F);
//		models.add(new GameItem.GameItemModel(modelg));
//		hud.add(new GameItem.GameItemModel(ii));

//		shader3d = ShaderLoader.loadShader(launcher, launcher.getEmbedFileSystem().getPath("shaders/basic/basic.json"));

		shaderhud = ShaderLoader.loadShader(launcher, launcher.getEmbedFileSystem().getPath("shaders/hud/hud.json"));

		LWJGLDrawContext context = (LWJGLDrawContext) window.getContext();
//		context3d = context.duplicate();
//		context3d.setProgram(shader3d);
//		context3d.setProjection(
//				new Transformations.Projection.Projection3D((float) Math.toRadians(70.0F), 0.01F, 10000F));

		contexthud = context.duplicate();
		contexthud.setProgram(shaderhud);
		contexthud.setProjection(new Transformations.Projection.Projection2D());

		glContext.depth.enabled.value.set(true);
		glContext.depth.depthFunc.set(GL_LEQUAL);
		glContext.blend.enabled.value.set(true);
		glContext.blend.srcrgb.set(GL_SRC_ALPHA);
		glContext.blend.dstrgb.set(GL_ONE_MINUS_SRC_ALPHA);
		glContext.blend.srcalpha.set(GL_ONE);
		glContext.blend.dstalpha.set(GL_ONE);
		glContext.replace(null);

		mainFramebuffer = new BasicFramebuffer(window.getFramebufferWidth(), window.getFramebufferHeight());
		mainScreenItem = new GameItem(new Texture2DModel(mainFramebuffer.getColorTexture()));
		mainScreenItemModel = new GameItem.GameItemModel(mainScreenItem);
//		secondaryFramebuffer = new BasicFramebuffer(window.getFramebufferWidth(), window.getFramebufferHeight());
//		secondaryScreenItem = new GameItem(new Texture2DModel(secondaryFramebuffer.getColorTexture()));
//		secondaryScreenItemModel = new GameItem.GameItemModel(secondaryScreenItem);

		updateScreenItems(window);

//		GameItem g2 = new GameItem(mainScreenItemModel);
//		g2.setColor(3, 3, 3, 1);
//		g2.setPosition(1.5F, 1F, 1);
//		g2.setScale(1 / 1000F);
//		secondaryModels.add(new GameItem.GameItemModel(g2));

		launcher.getLogger().info("RenderEngine initialized");
	}

	@Override
	public void close(Window window) throws GameException {
		launcher.getLogger().info("Cleaning up RenderEngine");
//		for (Model model : models) {
//			model.cleanup();
//		}
//		gprovider.cleanup();
//		shader3d.cleanup();
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
		mainScreenItem.setPosition(window.getFramebufferWidth() / 2F, window.getFramebufferHeight() / 2F, 0);
//		secondaryScreenItem.setScale(window.getFramebufferWidth(), window.getFramebufferHeight(), 1);
//		secondaryScreenItem.setPosition(window.getFramebufferWidth() / 2F, window.getFramebufferHeight() / 2F, 0);
	}

	@Override
	public void renderFrame(Window window) throws GameException {
		window.beginFrame();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		Camera camera = launcher.getCamera();

		mainFramebuffer.bind();
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

		mainFramebuffer.unbind();

		contexthud.update(camera);
		contexthud.drawModel(mainScreenItemModel, 0, 0, 0);
		shaderhud.clearUniforms();

//		secondaryFramebuffer.bind();
//		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//
//		render3d(camera);
//		shader3d.clearUniforms();
//		glClear(GL_DEPTH_BUFFER_BIT);
//		renderHud(camera);
//		shaderhud.clearUniforms();
//		secondaryFramebuffer.unbind();
//
//		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//		contexthud.update(camera);
//		contexthud.drawModel(secondaryScreenItemModel, 0, 0, 0);
////		for (Model m : secondaryModels) {
////			context3d.drawModel(m, 0, 0, 0);
////		}
////		shaderhud.clearUniforms();
//		mainFramebuffer.unbind();
//
//		contexthud.update(camera);
//		contexthud.drawModel(mainScreenItemModel, 0, 0, 0);
//		shaderhud.clearUniforms();
//
////		render3d(camera);
////		shader3d.clearUniforms();

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

//	private void render3d(Camera camera) throws GameException {
//		context3d.update(camera);
//		for (Model model : models) {
//			context3d.drawModel(model, 0, 0, 0, 0, 0, 0);
//		}
//	}
//
//	private void renderHud(Camera camera) throws GameException {
//		contexthud.update(camera);
//		for (Model model : hud) {
//			contexthud.drawModel(model, 0, 0, 0, 0, 0, 0);
//		}
//	}
}
