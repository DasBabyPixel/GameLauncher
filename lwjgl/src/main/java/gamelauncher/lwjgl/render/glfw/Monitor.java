package gamelauncher.lwjgl.render.glfw;

@SuppressWarnings("javadoc")
public class Monitor {

	public final String name;

	public final int x;

	public final int y;

	public final int w;

	public final int h;

	public final float scaleX;

	public final float scaleY;

	public final long glfwId;

	public Monitor(String name, int x, int y, int w, int h, float scaleX, float scaleY, long glfwId) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.glfwId = glfwId;
	}

}
