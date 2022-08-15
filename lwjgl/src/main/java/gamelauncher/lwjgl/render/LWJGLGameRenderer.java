package gamelauncher.lwjgl.render;

import static org.lwjgl.opengles.GLES20.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import gamelauncher.engine.gui.GuiRenderer;
import gamelauncher.engine.launcher.gui.MainScreenGui;
import gamelauncher.engine.render.BasicCamera;
import gamelauncher.engine.render.Camera;
import gamelauncher.engine.render.DrawContext;
import gamelauncher.engine.render.GameRenderer;
import gamelauncher.engine.render.Renderer;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.framebuffer.ManualQueryFramebuffer;
import gamelauncher.lwjgl.render.states.GlStates;

@SuppressWarnings("javadoc")
public class LWJGLGameRenderer implements GameRenderer {

//	public static final boolean WIREFRAMES = false;

	private final AtomicReference<Renderer> renderer = new AtomicReference<>();

	private final Map<Window, Entry> map = new ConcurrentHashMap<>();

	private LWJGLGameLauncher launcher;

	private GlContext glContext = new GlContext();

	public LWJGLGameRenderer(LWJGLGameLauncher launcher) {
		this.launcher = launcher;
		this.renderer.set(new GuiRenderer(launcher));
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
		map.put(window, new Entry(window));
		map.get(window).init();
		launcher.getLogger().info("RenderEngine initialized");
	}

	@Override
	public void cleanup(Window window) throws GameException {
		launcher.getLogger().info("Cleaning up RenderEngine");
//		Threads.waitFor(launcher.getGuiManager().cleanup(window));
		map.remove(window).cleanup();
		launcher.getLogger().info("RenderEngine cleaned up");
	}

	@Override
	public void windowSizeChanged(Window window) throws GameException {
		map.get(window).windowSizeChanged();
	}

	DrawContext ctx;

	Model model;

	Camera camera = new BasicCamera();

	@Override
	public void renderFrame(Window window) throws GameException {
		launcher.getProfiler().begin("render", "frame");
		map.get(window).renderFrame(renderer.get());
		launcher.getProfiler().end();
	}

	private class Entry {

		private final Window window;

		private final ManualQueryFramebuffer usedFb;

		private final ManualQueryFramebuffer lastFb;

//		private BasicFramebuffer mainFramebuffer;
//
//		private GameItem mainScreenItem;
//
//		private GameItem.GameItemModel mainScreenItemModel;

		private Renderer crenderer;

//		private Camera camera;
//
//		private DrawContext contexthud;

		@SuppressWarnings("deprecation")
		public Entry(Window window) {
			this.window = window;
//			this.camera = new BasicCamera(() -> window.scheduleDraw());
			this.usedFb = new ManualQueryFramebuffer(this.window.getFramebuffer());
			this.lastFb = new ManualQueryFramebuffer(this.window.getFramebuffer());
		}

		public void init() throws GameException {

			usedFb.query();
			lastFb.query();

			System.out.println(usedFb);

			launcher.getGuiManager().openGuiByClass(usedFb, MainScreenGui.class);

//			ShaderProgram shaderhud = launcher.getShaderLoader()
//					.loadShader(launcher, launcher.getEmbedFileSystem().getPath("shaders/hud/hud.json"));

			glContext.depth.enabled.value.set(true);
			glContext.depth.depthFunc.set(GL_LEQUAL);
			glContext.blend.enabled.value.set(true);
			glContext.blend.srcrgb.set(GL_SRC_ALPHA);
			glContext.blend.dstrgb.set(GL_ONE_MINUS_SRC_ALPHA);
			glContext.replace(null);

//			mainFramebuffer = new BasicFramebuffer(launcher, mqfb.width().intValue(), mqfb.height().intValue());
//			mainScreenItem = new GameItem(new Texture2DModel(mainFramebuffer.getColorTexture()));
//			mainScreenItemModel = mainScreenItem.createModel();
//			
//			contexthud = launcher.createContext(mainFramebuffer);
//			contexthud.setProgram(shaderhud);
//			contexthud.setProjection(new Transformations.Projection.Projection2D());
//			((LWJGLDrawContext) contexthud).swapTopBottom = true;

			updateScreenItems();

		}

		public void cleanup() throws GameException {
//			contexthud.getProgram().cleanup();
//			contexthud.cleanup();
//			mainFramebuffer.cleanup();
		}

		public void windowSizeChanged() {
//			usedFb.query();
//			mainFramebuffer.resize(mqfb.width().intValue(), mqfb.height().intValue());
			updateScreenItems();
		}

		private void updateScreenItems() {
			float fw = usedFb.width().floatValue();
			float fh = usedFb.height().floatValue();
//			mainScreenItem.setScale(fw, fh, 1);
//			mainScreenItem.setPosition(fw / 2F, fh / 2F, 0);
		}

		public void renderFrame(Renderer renderer) throws GameException {
			GlStates cur = GlStates.current();
			window.beginFrame();
//			cur.viewport(0, 0, usedFb.width().intValue(), usedFb.height().intValue());
			System.out.printf("%s %s%n", usedFb.width().intValue(), usedFb.height().intValue());
			cur.clearColor(0, 0, 0, 0);
			cur.clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

//			mainFramebuffer.bind();

			cur.clearColor(0.2F, 0.2F, 0.2F, 0.8F);
			cur.clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			if (renderer != crenderer) {
				cleanup(crenderer);
				init(renderer);
				crenderer = renderer;
			}
			if (renderer != null) {
				renderer.render(usedFb);
			}

//			mainFramebuffer.unbind();

//			contexthud.update(camera);
//			contexthud.drawModel(mainScreenItemModel, 0, 0, 0);
//			contexthud.getProgram().clearUniforms();

			window.endFrame();
		}

		private void cleanup(Renderer renderer) throws GameException {
			if (renderer == null) {
				return;
			}
			renderer.cleanup(usedFb);
		}

		private void init(Renderer renderer) throws GameException {
			if (renderer == null) {
				return;
			}
			renderer.init(usedFb);
		}

	}

}
