package gamelauncher.lwjgl.render;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import javax.imageio.ImageIO;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.Camera;
import gamelauncher.engine.render.DrawContext;
import gamelauncher.engine.render.GameItem;
import gamelauncher.engine.render.GameRenderer;
import gamelauncher.engine.render.Renderer;
import gamelauncher.engine.render.Transformations;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.render.framebuffer.BasicFramebuffer;
import gamelauncher.lwjgl.render.model.Texture2DModel;

@SuppressWarnings("javadoc")
public class LWJGLGameRenderer implements GameRenderer {

	private final AtomicReference<Renderer> renderer = new AtomicReference<>();
	private GameLauncher launcher;
	private ConcurrentHashMap<Window, WindowBasedRenderer> m = new ConcurrentHashMap<>();

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

		WindowBasedRenderer r = new WindowBasedRenderer(launcher, window);
		r.init();
		m.put(window, r);

		launcher.getLogger().info("RenderEngine initialized");
	}

	@Override
	public void close(Window window) throws GameException {
		launcher.getLogger().info("Cleaning up RenderEngine");
		m.remove(window).cleanup();
		launcher.getLogger().info("RenderEngine cleaned up");
	}

	@Override
	public void windowSizeChanged(Window window) throws GameException {
		m.get(window).windowSizeChanged();
	}

	@Override
	public void renderFrame(Window window) throws GameException {
		m.get(window).render(renderer.get());
	}

	public static class WindowBasedRenderer {
		private final GameLauncher launcher;
		private final Window window;
		private Renderer crenderer;
		private DrawContext contexthud;
		private BasicFramebuffer mainFramebuffer;
		private GameItem mainScreenItem;
		private GameItem.GameItemModel mainScreenItemModel;
		private final GlContext glContext = new GlContext();

		public WindowBasedRenderer(GameLauncher launcher, Window window) {
			this.launcher = launcher;
			this.window = window;
		}

		private void cleanup() throws GameException {
			cleanup(crenderer);
			contexthud.getProgram().cleanup();
			contexthud.cleanup();
			mainFramebuffer.cleanup();
			mainScreenItemModel.cleanup();
		}

		private void render(Renderer renderer) throws GameException {
			window.beginFrame();
			glClearColor(0, 0, 0, 0);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

//			mainFramebuffer.bind();
			Camera camera = launcher.getCamera();
			if (renderer != crenderer) {
				cleanup(crenderer);
				init(renderer);
				crenderer = renderer;
			}
			if (crenderer != null) {
				crenderer.render(window);
			}

			BufferedImage img = mainFramebuffer.getColorTexture().getBufferedImage();
			try {
				ImageIO.write(img, "png", new File("fb.png"));
			} catch (IOException ex) {
				ex.printStackTrace();
			}

//			mainFramebuffer.unbind();
//
//			contexthud.update(camera);
//			contexthud.drawModel(mainScreenItemModel);
//			contexthud.getProgram().clearUniforms();

			window.endFrame();
		}

		private void updateScreenItems() {
			float fw = window.getFramebuffer().width().floatValue();
			float fh = window.getFramebuffer().height().floatValue();
			mainScreenItem.setScale(fw, fh, 1);
			mainScreenItem.setPosition(fw / 2F, fh / 2F, 0);
		}

		private void init() throws GameException {
			contexthud = launcher.createContext(window.getFramebuffer());
			ShaderProgram program = launcher.getShaderLoader()
					.loadShader(launcher, launcher.getEmbedFileSystem().getPath("shaders", "hud", "hud.json"));
			contexthud.setProgram(program);
			contexthud.setProjection(new Transformations.Projection.Projection2D());

			glContext.depth.enabled.value.set(true);
			glContext.depth.depthFunc.set(GL_LEQUAL);
			glContext.blend.enabled.value.set(true);
			glContext.blend.srcrgb.set(GL_SRC_ALPHA);
			glContext.blend.dstrgb.set(GL_ONE_MINUS_SRC_ALPHA);
			glContext.replace(null);

			mainFramebuffer = new BasicFramebuffer(window.getFramebuffer().width().intValue(),
					window.getFramebuffer().height().intValue());
//			System.out.println(mainFramebuffer.width().intValue());
			mainScreenItem = new GameItem(new Texture2DModel(mainFramebuffer.getColorTexture()));
			mainScreenItemModel = new GameItem.GameItemModel(mainScreenItem);
			updateScreenItems();
		}

		public void windowSizeChanged() {
			mainFramebuffer.resize(window.getFramebuffer().width().intValue(),
					window.getFramebuffer().height().intValue());
			updateScreenItems();
		}

		private void init(Renderer renderer) throws GameException {
			if (renderer == null) {
				return;
			}
			renderer.init(window);
		}

		private void cleanup(Renderer renderer) throws GameException {
			if (renderer == null) {
				return;
			}
			renderer.cleanup(window);
		}
	}
}
