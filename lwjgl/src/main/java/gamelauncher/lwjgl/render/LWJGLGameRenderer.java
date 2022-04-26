package gamelauncher.lwjgl.render;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import org.joml.Vector3f;

import gamelauncher.engine.GameException;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.GameRenderer;
import gamelauncher.engine.render.Model;
import gamelauncher.engine.render.Renderer;
import gamelauncher.engine.render.Transformations;
import gamelauncher.engine.render.Window;
import gamelauncher.lwjgl.render.light.PointLight;

public class LWJGLGameRenderer implements GameRenderer {

	private final AtomicReference<Renderer> renderer = new AtomicReference<>();
	private Renderer crenderer;
	private GameLauncher launcher;

	private Collection<Model> models = new HashSet<>();
	private PointLight light = new PointLight();
//	private GameItem item;

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
		Model model = launcher.getModelLoader()
				.loadModel(
						launcher.getResourceLoader().getResource(launcher.getEmbedFileSystem().getPath("Pickle2.obj")));

		light.color = new Vector3f(1, 1, 1);
		light.intensity = 1;
		light.position = new Vector3f(2, 2, 2);
		light.att = new PointLight.Attenuation();
		light.att.constant = 1;
		light.att.exponent = 0;
		light.att.linear = 0;

//		LWJGLTexture texture = new LWJGLTexture(new ResourcePath("cube.png"));
//		((MeshModel) model).mesh.setTexture(texture);
		Random r = new Random(1);
		int count = 1;
		float density = 0.1F;
		float width = (float) Math.pow(count / density, 1D / 3D);
		List<GameItem> items = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			GameItem item = new GameItem(model);
			boolean f;
			do {
				f = false;
				item.setPosition(r.nextFloat() * width - width / 2F, r.nextFloat() * width - width / 2F,
						r.nextFloat() * width - width / 2F);
				item.setRotation(r.nextFloat() * 360, r.nextFloat() * 360, r.nextFloat() * 360);
				for (GameItem it : items) {
					if (it.getPosition().distance(item.getPosition()) < 1.5) {
						f = true;
						break;
					}
				}
			} while (f);
			items.add(item);
			models.add(new GameItem.GameItemModel(item));
		}

		ShaderProgram shaderProgram = new ShaderProgram(launcher);
		shaderProgram.createVertexShader(launcher.getResourceLoader()
				.getResource(launcher.getEmbedFileSystem().getPath("shaders/basic/vertex"))
				.newResourceStream()
				.readUTF8FullyClose());
		shaderProgram.createFragmentShader(launcher.getResourceLoader()
				.getResource(launcher.getEmbedFileSystem().getPath("shaders/basic/fragment"))
				.newResourceStream()
				.readUTF8FullyClose());
		shaderProgram.link();
		shaderProgram.deleteVertexShader();
		shaderProgram.deleteFragmentShader();
		shaderProgram.createUniform("projectionMatrix");
		shaderProgram.createUniform("modelViewMatrix");
		shaderProgram.createUniform("texture_sampler");
		
		shaderProgram.createMaterialUniform("material");
		shaderProgram.createUniform("specularPower");
		shaderProgram.createUniform("ambientLight");
		shaderProgram.createPointLightUniform("pointLight");

		LWJGLDrawContext context = (LWJGLDrawContext) window.getContext();
		context.setProgram(shaderProgram);
		context.setProjection(new Transformations.Projection.Projection3D((float) Math.toRadians(70.0f), 0.01F, 1000F));
		context.pointLight = light;

		glEnable(GL_DEPTH_TEST);
//		glEnable(GL_CULL_FACE);
//		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		launcher.getLogger().info("RenderEngine initialized");
	}

	@Override
	public void close(Window window) throws GameException {
		launcher.getLogger().info("Cleaning up RenderEngine");
		for (Model model : models) {
			model.cleanup();
		}
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

	public static float rx, ry, rz;

	@Override
	public void renderFrame(Window window) throws GameException {
		window.beginFrame();

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

		for (Model model : models) {
			window.getContext().drawModel(model, 0, 0, 0, rx, ry, rz);
		}

		window.endFrame();
	}
}
