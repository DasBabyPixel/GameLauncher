package gamelauncher.lwjgl.render;

import gamelauncher.engine.gui.GuiRenderer;
import gamelauncher.engine.gui.launcher.MainScreenGui;
import gamelauncher.engine.render.*;
import gamelauncher.engine.render.ContextProvider.ContextType;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.framebuffer.BasicFramebuffer;
import gamelauncher.lwjgl.render.model.Texture2DModel;
import gamelauncher.lwjgl.render.states.GlStates;
import org.lwjgl.opengles.GLES20;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class LWJGLGameRenderer implements GameRenderer {

	//	public static final boolean WIREFRAMES = false;

	private final AtomicReference<Renderer> renderer = new AtomicReference<>();

	private final Map<Frame, Entry> map = new ConcurrentHashMap<>();

	private final LWJGLGameLauncher launcher;

	private final GlContext glContext = new GlContext();

	public LWJGLGameRenderer(LWJGLGameLauncher launcher) {
		this.launcher = launcher;
		this.renderer.set(new GuiRenderer(launcher));
	}

	@Override
	public void renderFrame(Frame frame) throws GameException {
		this.launcher.profiler().begin("render", "frame");
		this.map.get(frame).renderFrame(this.renderer.get());
		this.launcher.profiler().end();
	}

	@Override
	public void windowSizeChanged(Frame frame) throws GameException {
		this.launcher.profiler().begin("render", "windowSizeChanged");
		this.map.get(frame).windowSizeChanged();
		this.launcher.profiler().end();
	}

	@Override
	public void init(Frame frame) throws GameException {
		this.launcher.profiler().begin("render", "init");
		this.launcher.logger().info("Initializing RenderEngine");
		this.map.put(frame, new Entry(frame));
		this.map.get(frame).init();
		this.launcher.logger().info("RenderEngine initialized");
		this.launcher.profiler().end();
	}

	@Override
	public void cleanup(Frame frame) throws GameException {
		this.launcher.profiler().begin("render", "cleanup");
		this.launcher.logger().info("Cleaning up RenderEngine");
		this.map.remove(frame).cleanup();
		this.launcher.logger().info("RenderEngine cleaned up");
		this.launcher.profiler().end();
	}

	@Override
	public void refreshDisplay(Frame frame) throws GameException {
		this.launcher.profiler().begin("render", "refresh");
		this.map.get(frame).refreshDisplay(this.renderer.get());
		this.launcher.profiler().end();
	}

	public class Entry extends AbstractGameResource {

		public final Frame frame;
		public BasicFramebuffer mainFramebuffer;
		public GameItem mainScreenItem;
		public GameItem.GameItemModel mainScreenItemModel;
		public Renderer crenderer;
		public Camera camera;
		public DrawContext contexthud;

		public Entry(Frame frame) {
			this.frame = frame;
			this.camera = new BasicCamera();
		}

		public void init() throws GameException {

			glContext.depth.enabled.value.set(true);
			glContext.depth.depthFunc.set(GLES20.GL_LEQUAL);
			glContext.blend.enabled.value.set(true);
			glContext.blend.srcrgb.set(GLES20.GL_SRC_ALPHA);
			glContext.blend.dstrgb.set(GLES20.GL_ONE_MINUS_SRC_ALPHA);
			glContext.replace(null);

			this.mainFramebuffer = new BasicFramebuffer(LWJGLGameRenderer.this.launcher,
					this.frame.framebuffer().width().intValue(),
					this.frame.framebuffer().height().intValue());
			this.mainScreenItem =
					new GameItem(new Texture2DModel(this.mainFramebuffer.getColorTexture()));
			this.mainScreenItem.scale().x.bind(this.mainFramebuffer.width());
			this.mainScreenItem.scale().y.bind(this.mainFramebuffer.height());
			this.mainScreenItem.position().x.bind(this.mainFramebuffer.width().divide(2));
			this.mainScreenItem.position().y.bind(this.mainFramebuffer.height().divide(2));

			this.mainScreenItemModel = this.mainScreenItem.createModel();

			this.contexthud = launcher.contextProvider()
					.loadContext(this.mainFramebuffer, ContextType.HUD);
			((LWJGLDrawContext) this.contexthud).swapTopBottom = true;

			launcher.guiManager().openGuiByClass(this.mainFramebuffer, MainScreenGui.class);

			//			updateScreenItems();

		}

		@Override
		public void cleanup0() throws GameException {
			this.mainScreenItemModel.cleanup();
			launcher.contextProvider().freeContext(this.contexthud, ContextType.HUD);
			this.mainFramebuffer.cleanup();
		}

		public void windowSizeChanged() throws GameException {
			this.mainFramebuffer.resize(this.frame.framebuffer().width().intValue(),
					this.frame.framebuffer().height().intValue());
			//			updateScreenItems();
		}

		//		private void updateScreenItems() {
		//			float fw = window.getFramebuffer().width().floatValue();
		//			float fh = window.getFramebuffer().height().floatValue();
		//			mainScreenItem.setScale(fw, fh, 1);
		//			mainScreenItem.setPosition(fw / 2F, fh / 2F, 0);
		//		}

		public void refreshDisplay(Renderer renderer) throws GameException {
			GlStates cur = GlStates.current();
			this.frame.framebuffer().beginFrame();
			cur.viewport(0, 0, this.frame.framebuffer().width().intValue(),
					this.frame.framebuffer().height().intValue());
			cur.clearColor(0, 0, 0, 0);
			cur.clear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

			this.contexthud.update(this.camera);
			this.contexthud.drawModel(this.mainScreenItemModel, 0, 0, 0);
			this.contexthud.program().clearUniforms();
			this.frame.framebuffer().endFrame();
		}

		public void renderFrame(Renderer renderer) throws GameException {
			GlStates cur = GlStates.current();
			this.frame.framebuffer().beginFrame();
			cur.viewport(0, 0, this.frame.framebuffer().width().intValue(),
					this.frame.framebuffer().height().intValue());
			cur.clearColor(0, 0, 0, 0);
			cur.clear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

			this.mainFramebuffer.bind();

			cur.clearColor(0.2F, 0.2F, 0.2F, 0.8F);
			cur.clear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

			if (renderer != this.crenderer) {
				this.cleanup(this.crenderer);
				this.init(renderer);
				this.crenderer = renderer;
			}
			if (renderer != null) {
				renderer.render(this.mainFramebuffer);
			}

			this.mainFramebuffer.unbind();

			this.contexthud.update(this.camera);
			this.contexthud.drawModel(this.mainScreenItemModel, 0, 0, 0);
			this.contexthud.program().clearUniforms();

			this.frame.framebuffer().endFrame();
		}

		private void cleanup(Renderer renderer) throws GameException {
			if (renderer == null) {
				return;
			}
			renderer.cleanup(this.mainFramebuffer);
		}

		private void init(Renderer renderer) throws GameException {
			if (renderer == null) {
				return;
			}
			renderer.init(this.mainFramebuffer);
		}

	}

	@Override
	public void setRenderer(Renderer renderer) {
		this.renderer.set(renderer);
	}

	@Override
	public Renderer renderer() {
		return this.renderer.get();
	}

}
