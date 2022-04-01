package gamelauncher.lwjgl.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import gamelauncher.engine.GameException;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.GameRenderer;
import gamelauncher.engine.render.Renderer;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.resource.ResourcePath;
import gamelauncher.lwjgl.render.shader.ShaderProgram;

public class LWJGLGameRenderer implements GameRenderer {

	private final AtomicReference<Renderer> renderer = new AtomicReference<>();
	private Renderer crenderer;
	private ShaderProgram shaderProgram;
	private GameLauncher launcher;

	private Mesh mesh;

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
	public void init() throws GameException {
		launcher.getLogger().info("Initializing RenderEngine");
		mesh = new Mesh(new float[] {
				-0.5f, 0.5f, 0.0f, -0.5f, -0.5f, 0.0f, 0.5f, -0.5f, 0.0f, 0.5f, 0.5f, 0.0f,
		}, new float[] {
				0.5f, 0.0f, 0.0f, 0.0f, 0.5f, 0.0f, 0.0f, 0.0f, 0.5f, 0.0f, 0.5f, 0.5f,
		}, new int[] {
				0, 1, 3, 3, 1, 2,
		});
		shaderProgram = new ShaderProgram(launcher);
		try {
			shaderProgram.createVertexShader(launcher.getResourceLoader()
					.getResource(new ResourcePath("shaders/basic/vertex"))
					.newResourceStream()
					.readUTF8FullyClose());
			shaderProgram.createFragmentShader(launcher.getResourceLoader()
					.getResource(new ResourcePath("shaders/basic/fragment"))
					.newResourceStream()
					.readUTF8FullyClose());
		} catch (IOException ex) {
			throw new GameException(ex);
		}
		shaderProgram.link();
		shaderProgram.deleteVertexShader();
		shaderProgram.deleteFragmentShader();
		launcher.getLogger().info("RenderEngine initialized");
	}

	@Override
	public void close() throws GameException {
		launcher.getLogger().info("Cleaning up RenderEngine");
		mesh.cleanup();
		shaderProgram.cleanup();
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
		shaderProgram.bind();
		glBindVertexArray(mesh.getVaoId());
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
		shaderProgram.unbind();
		window.endFrame();
	}
}
