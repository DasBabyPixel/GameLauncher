package gamelauncher.lwjgl.render.glfw;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengles.GLES20;

import de.dasbabypixel.api.property.BooleanValue;
import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.ScissorStack;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.render.LWJGLScissorStack;

public class GLFWFrameFramebuffer extends AbstractGameResource implements Framebuffer {

	private final NumberValue width = NumberValue.zero();

	private final NumberValue height = NumberValue.zero();

	private final BooleanValue swapBuffers = BooleanValue.falseValue();

	private final GLFWFrame frame;

	private final LWJGLScissorStack scissor;

	public GLFWFrameFramebuffer(GLFWFrame frame) {
		this.frame = frame;
		this.scissor = new LWJGLScissorStack(this);
	}

	@Override
	public void beginFrame() {
	}

	@Override
	public void endFrame() {
		if (this.swapBuffers.booleanValue()) {
			GLUtil.skip.set(true);
			GLFW.glfwSwapBuffers(this.frame.context.glfwId);
			GLUtil.skip.remove();
			GLES20.glGetError();
		}
	}

	@Override
	public void scheduleRedraw() {
	}

	@Override
	protected void cleanup0() throws GameException {
	}

	@Override
	public GLFWFrameRenderThread renderThread() {
		return this.frame.renderThread;
	}

	@Override
	public ScissorStack scissorStack() {
		return this.scissor;
	}

	public BooleanValue swapBuffers() {
		return this.swapBuffers;
	}

	@Override
	public NumberValue width() {
		return this.width;
	}

	@Override
	public NumberValue height() {
		return this.height;
	}

	public GLFWFrame frame() {
		return this.frame;
	}

}
