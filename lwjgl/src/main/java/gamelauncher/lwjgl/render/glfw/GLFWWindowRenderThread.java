package gamelauncher.lwjgl.render.glfw;

@SuppressWarnings("javadoc")
public class GLFWWindowRenderThread extends GLFWFrameRenderThread {

	public GLFWWindowRenderThread(GLFWFrame frame) {
		super(frame);
	}

	@Override
	protected boolean hasWindow() {
		return true;
	}

}
