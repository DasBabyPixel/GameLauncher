package gamelauncher.lwjgl.render;

import static org.lwjgl.opengl.GL11.*;

import java.util.concurrent.atomic.AtomicReference;

import gamelauncher.engine.GameException;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.GameRenderer;
import gamelauncher.engine.render.Model;
import gamelauncher.engine.render.Renderer;
import gamelauncher.engine.render.Transformations;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.resource.ResourcePath;

public class LWJGLGameRenderer implements GameRenderer {

	private final AtomicReference<Renderer> renderer = new AtomicReference<>();
	private Renderer crenderer;
	private GameLauncher launcher;

	private Model model;
	private GameItem item;

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
		Mesh mesh = new Mesh(new float[] {
				-0.5f, 0.5f, -1.55f, -0.5f, -0.5f, -1.55f, 0.5f, -0.5f, -1.55f, 0.5f, 0.5f, -1.55f,
		}, new float[] {
				0.5f, 0.0f, 0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.0f, 0.5f, 0.0f, 0.5f, 0.5f,
		}, new int[] {
				0, 1, 3, 3, 1, 2,
		});
		item = new GameItem(mesh);
		model = new GameItem.GameItemModel(item);

		ShaderProgram shaderProgram = new ShaderProgram(launcher);
		shaderProgram.createVertexShader(launcher.getResourceLoader()
				.getResource(new ResourcePath("shaders/basic/vertex"))
				.newResourceStream()
				.readUTF8FullyClose());
		shaderProgram.createFragmentShader(launcher.getResourceLoader()
				.getResource(new ResourcePath("shaders/basic/fragment"))
				.newResourceStream()
				.readUTF8FullyClose());
		shaderProgram.link();
		shaderProgram.deleteVertexShader();
		shaderProgram.deleteFragmentShader();
		shaderProgram.createUniform("projectionMatrix");
		shaderProgram.createUniform("transformationMatrix");

		LWJGLDrawContext context = (LWJGLDrawContext) window.getContext();
		context.setProgram(shaderProgram);
		context.setProjectionMatrix(
				new Transformations.Projection.Projection3D((float) Math.toRadians(70.0f), 0.01F, 1000F));
		launcher.getLogger().info("RenderEngine initialized");
	}

	@Override
	public void close(Window window) throws GameException {
		launcher.getLogger().info("Cleaning up RenderEngine");
		model.cleanup();
		((LWJGLDrawContext) window.getContext()).getProgram().cleanup();
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

		item.setPosition((float) Math.sin(Math.toRadians(System.currentTimeMillis() / 20D)), 0, 0);

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		Renderer renderer = this.renderer.get();
		if (renderer != crenderer) {
			cleanup(crenderer);
			init(renderer);
			crenderer = renderer;
		}
		if (renderer != null) {
			renderer.render(window, window.getContext());
		}

		window.getContext().drawModel(model, 0, 0, 0);

		window.endFrame();
	}
}
