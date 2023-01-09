package gamelauncher.lwjgl.render.glfw;

public record Monitor(String name, int x, int y, int width, int height, float scaleX, float scaleY,
		long glfwId, VideoMode videoMode) {

	public record VideoMode(int width, int height, int refreshRate) {

	}

}
