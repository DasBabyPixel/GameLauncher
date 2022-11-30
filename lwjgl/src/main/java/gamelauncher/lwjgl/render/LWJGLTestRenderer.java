package gamelauncher.lwjgl.render;

import org.lwjgl.opengles.GLES20;

import gamelauncher.engine.gui.GuiRenderer;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.launcher.gui.ColorGui;
import gamelauncher.engine.render.Frame;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.GameRenderer;
import gamelauncher.engine.render.Renderer;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.states.GlStates;

public class LWJGLTestRenderer implements GameRenderer {

	private GuiRenderer renderer;

	private LWJGLGameLauncher launcher;

	private Framebuffer framebuffer;

	private final GlContext glContext = new GlContext();

	public LWJGLTestRenderer(LWJGLGameLauncher launcher) {
		this.launcher = launcher;
		this.renderer = new GuiRenderer(this.launcher);
	}

	@Override
	public void renderFrame(Frame frame) throws GameException {
		this.framebuffer.beginFrame();
		GlStates.current().clear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		this.renderer.render(this.framebuffer);
		this.framebuffer.endFrame();
	}

	@Override
	public void windowSizeChanged(Frame frame) throws GameException {
	}

	@Override
	public void init(Frame frame) throws GameException {
		this.framebuffer = frame.framebuffer();
		this.renderer.init(this.framebuffer);
		this.glContext.depth.enabled.value.set(true);
		this.glContext.depth.depthFunc.set(GLES20.GL_LEQUAL);
		this.glContext.blend.enabled.value.set(true);
		this.glContext.blend.srcrgb.set(GLES20.GL_SRC_ALPHA);
		this.glContext.blend.dstrgb.set(GLES20.GL_ONE_MINUS_SRC_ALPHA);
		this.glContext.replace(null);
		this.launcher.getGuiManager().openGui(this.framebuffer, new ParentableAbstractGui(this.launcher) {

			{
				ColorGui colorGui = LWJGLTestRenderer.this.launcher.getGuiManager().createGui(ColorGui.class);
				colorGui.getXProperty().bind(this.getXProperty());
				colorGui.getYProperty().bind(this.getYProperty());
				colorGui.getWidthProperty().bind(this.getWidthProperty());
				colorGui.getHeightProperty().bind(this.getHeightProperty());
				colorGui.getColor().set(1, 1, 1, 1);
				colorGui.getColor().w.bind(this.getHeightProperty().divide(this.getWidthProperty()).min(1));
				this.GUIs.add(colorGui);
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
	public Renderer getRenderer() {
		return this.renderer;
	}

}
