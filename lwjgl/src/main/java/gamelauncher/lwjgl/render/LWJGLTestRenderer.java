package gamelauncher.lwjgl.render;

import gamelauncher.engine.gui.GuiRenderer;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.gui.launcher.ColorGui;
import gamelauncher.engine.render.Frame;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.GameRenderer;
import gamelauncher.engine.render.Renderer;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.states.GlStates;
import org.lwjgl.opengles.GLES20;

public class LWJGLTestRenderer implements GameRenderer {

	private final GlContext glContext = new GlContext();
	private GuiRenderer renderer;
	private LWJGLGameLauncher launcher;
	private Framebuffer framebuffer;

	public LWJGLTestRenderer(LWJGLGameLauncher launcher) {
		this.launcher = launcher;
		this.renderer = new GuiRenderer(this.launcher);
	}

	@Override
	public void renderFrame(Frame frame) throws GameException {
		this.framebuffer.beginFrame();
		GlStates.current().clear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		GlStates.current().viewport(0, 0, frame.framebuffer().width().intValue(),
				frame.framebuffer().height().intValue());
		this.renderer.render(this.framebuffer);
		this.framebuffer.endFrame();
	}

	@Override
	public void windowSizeChanged(Frame frame) {

	}

	@Override
	public void init(Frame frame) throws GameException {
		this.framebuffer = frame.framebuffer();
		this.renderer.init(this.framebuffer);
		this.glContext.depth.enabled.value.set(true);
		this.glContext.depth.depthFunc.set(GLES20.GL_ALWAYS);
		this.glContext.blend.enabled.value.set(true);
		this.glContext.blend.srcrgb.set(GLES20.GL_SRC_ALPHA);
		this.glContext.blend.dstrgb.set(GLES20.GL_ONE_MINUS_SRC_ALPHA);
		this.glContext.replace(null);
		this.launcher.guiManager()
				.openGui(this.framebuffer, new ParentableAbstractGui(this.launcher) {

					{
						ColorGui colorGui = LWJGLTestRenderer.this.launcher.guiManager()
								.createGui(ColorGui.class);
						//						colorGui.getXProperty().bind(this.getXProperty());
						//						colorGui.getYProperty().bind(this.getYProperty());
						//						colorGui.getWidthProperty().bind(this.getWidthProperty());
						//						colorGui.getHeightProperty().bind(this.getHeightProperty());
						colorGui.xProperty().setValue(100);
						colorGui.yProperty().setValue(100);
						colorGui.widthProperty().bind(widthProperty().subtract(200));
						colorGui.heightProperty().bind(heightProperty().subtract(200));
						colorGui.color().set(1, 1, 1, 0.5F);
						//						colorGui.getColor().w.bind(
						//								this.getHeightProperty().divide(this.getWidthProperty()).min(1));
						this.GUIs.add(colorGui);
						//						TextGui tg = new TextGui(launcher, "A", 100);
						//						tg.getXProperty().bind(this.getXProperty());
						//						tg.getYProperty().bind(this.getYProperty());
						//						GUIs.add(tg);
					}

				});
	}

	@Override
	public void cleanup(Frame frame) throws GameException {
		this.renderer.cleanup(this.framebuffer);
		this.framebuffer = null;
	}

	@Override
	public void refreshDisplay(Frame frame) throws GameException {
		this.renderer.render(this.framebuffer);
	}

	@Override
	public void setRenderer(Renderer renderer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Renderer renderer() {
		return this.renderer;
	}

}
