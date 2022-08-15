package gamelauncher.lwjgl.render;

import static org.lwjgl.opengles.GLES20.*;

import gamelauncher.engine.render.BasicCamera;
import gamelauncher.engine.render.Camera;
import gamelauncher.engine.render.DrawContext;
import gamelauncher.engine.render.GameItem;
import gamelauncher.engine.render.GameRenderer;
import gamelauncher.engine.render.Renderer;
import gamelauncher.engine.render.Transformations.Projection;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.framebuffer.ManualQueryFramebuffer;
import gamelauncher.lwjgl.render.model.Texture2DModel;
import gamelauncher.lwjgl.render.states.GlStates;
import gamelauncher.lwjgl.render.texture.LWJGLTexture;

public class TestGameRenderer implements GameRenderer {

	private final LWJGLGameLauncher launcher;

	private DrawContext ctx;

	private Model model;
	
	private Camera camera = new BasicCamera();

	public TestGameRenderer(LWJGLGameLauncher launcher) {
		this.launcher = launcher;
	}

	@Override
	public void renderFrame(Window window) throws GameException {
		window.beginFrame();
		GlStates gl = GlStates.current();
		gl.clearColor(0.2F, 0.2F, 0.2F, 0.8F);
		gl.clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		ctx.update(camera);
		ctx.drawModel(model);
//		ctx.getProgram().clearUniforms();
		System.out.println("Incoming");
		Threads.sleep(100);
		window.endFrame();
	}

	@Override
	public void windowSizeChanged(Window window) throws GameException {
	}

	@Override
	public void init(Window window) throws GameException {
		ManualQueryFramebuffer mqfb  = new ManualQueryFramebuffer(window.getFramebuffer());
		mqfb.query();
		ctx = launcher.createContext(mqfb);
		ctx.setProgram(launcher.getShaderLoader()
				.loadShader(launcher, launcher.getEmbedFileSystem().getPath("shaders", "hud", "hud.json")));
		ctx.setProjection(new Projection.Projection2D());
		LWJGLTexture tex = Threads.waitFor(launcher.getTextureManager().createTexture());
		Threads.waitFor(tex.uploadAsync(launcher.getResourceLoader()
				.getResource(launcher.getEmbedFileSystem().getPath("pixel64x64.png"))
				.newResourceStream()));
		model = new Texture2DModel(tex);
		GameItem gi = new GameItem(model);
		gi.setScale(400, 400, 0);
		gi.setPosition(300, 300, 0);
		model = gi.createModel();
	}

	@Override
	public void cleanup(Window window) throws GameException {
	}

	@Override
	public void setRenderer(Renderer renderer) {
	}

	@Override
	public Renderer getRenderer() {
		return null;
	}

}
