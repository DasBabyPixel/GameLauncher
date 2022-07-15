package gamelauncher.lwjgl.render;

import static org.lwjgl.opengles.GLES20.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import gamelauncher.engine.render.BasicCamera;
import gamelauncher.engine.render.Camera;
import gamelauncher.engine.render.DrawContext;
import gamelauncher.engine.render.GameItem;
import gamelauncher.engine.render.GameRenderer;
import gamelauncher.engine.render.Renderer;
import gamelauncher.engine.render.Transformations;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.framebuffer.BasicFramebuffer;
import gamelauncher.lwjgl.render.model.Texture2DModel;

@SuppressWarnings("javadoc")
public class LWJGLGameRenderer implements GameRenderer {

//	public static final boolean WIREFRAMES = false;

	private final AtomicReference<Renderer> renderer = new AtomicReference<>();
	private final Map<Window, Entry> map = new ConcurrentHashMap<>();
	private LWJGLGameLauncher launcher;

	private GlContext glContext = new GlContext();

	public LWJGLGameRenderer(LWJGLGameLauncher launcher) {
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
		map.put(window, new Entry(window));
		map.get(window).init();
		launcher.getLogger().info("RenderEngine initialized");
	}

	@Override
	public void cleanup(Window window) throws GameException {
		launcher.getLogger().info("Cleaning up RenderEngine");
		map.remove(window).cleanup();
		launcher.getLogger().info("RenderEngine cleaned up");
	}

	@Override
	public void windowSizeChanged(Window window) throws GameException {
		map.get(window).windowSizeChanged();
	}

	@Override
	public void renderFrame(Window window) throws GameException {
		map.get(window).renderFrame(renderer.get());
	}

	private class Entry {
		private final Window window;
		private BasicFramebuffer mainFramebuffer;
		private GameItem mainScreenItem;
		private GameItem.GameItemModel mainScreenItemModel;
		private Renderer crenderer;
		private Camera camera;

		private DrawContext contexthud;

		public Entry(Window window) {
			this.window = window;
			this.camera = new BasicCamera(() -> window.scheduleDraw());
		}

		public void init() throws GameException {

			ShaderProgram shaderhud = launcher.getShaderLoader()
					.loadShader(launcher, launcher.getEmbedFileSystem().getPath("shaders/hud/hud.json"));

			glContext.depth.enabled.value.set(true);
			glContext.depth.depthFunc.set(GL_LEQUAL);
			glContext.blend.enabled.value.set(true);
			glContext.blend.srcrgb.set(GL_SRC_ALPHA);
			glContext.blend.dstrgb.set(GL_ONE_MINUS_SRC_ALPHA);
			glContext.replace(null);

			mainFramebuffer = new BasicFramebuffer(launcher, window.getFramebuffer().width().intValue(),
					window.getFramebuffer().height().intValue());
			mainScreenItem = new GameItem(new Texture2DModel(mainFramebuffer.getColorTexture()));
			mainScreenItemModel = new GameItem.GameItemModel(mainScreenItem);

			contexthud = launcher.createContext(mainFramebuffer);
			contexthud.setProgram(shaderhud);
			contexthud.setProjection(new Transformations.Projection.Projection2D());
			((LWJGLDrawContext) contexthud).swapTopBottom = true;

			updateScreenItems();

//			if (WIREFRAMES) {
//				glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
//			}
		}

		public void cleanup() throws GameException {
			contexthud.getProgram().cleanup();
			contexthud.cleanup();
			mainFramebuffer.cleanup();
		}

		public void windowSizeChanged() {
			mainFramebuffer.resize(window.getFramebuffer().width().intValue(),
					window.getFramebuffer().height().intValue());
			updateScreenItems();
		}

		private void updateScreenItems() {
			float fw = window.getFramebuffer().width().floatValue();
			float fh = window.getFramebuffer().height().floatValue();
			mainScreenItem.setScale(fw, fh, 1);
			mainScreenItem.setPosition(fw / 2F, fh / 2F, 0);
		}

		public void renderFrame(Renderer renderer) throws GameException {
			window.beginFrame();
			glClearColor(0, 0, 0, 0);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			mainFramebuffer.bind();
			glClearColor(0.2F, 0.2F, 0.2F, 0.8F);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			if (renderer != crenderer) {
				cleanup(crenderer);
				init(renderer);
				crenderer = renderer;
			}
			if (renderer != null) {
				renderer.render(window);
			}

			mainFramebuffer.unbind();

			contexthud.update(camera);
			contexthud.drawModel(mainScreenItemModel, 0, 0, 0);
			contexthud.getProgram().clearUniforms();

			window.endFrame();
		}

		private void cleanup(Renderer renderer) throws GameException {
			if (renderer == null) {
				return;
			}
			renderer.cleanup(window);
		}

		private void init(Renderer renderer) throws GameException {
			if (renderer == null) {
				return;
			}
			renderer.init(window);
		}
	}
}
