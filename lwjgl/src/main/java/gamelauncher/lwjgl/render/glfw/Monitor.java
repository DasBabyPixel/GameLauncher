package gamelauncher.lwjgl.render.glfw;

public class Monitor {

	private final String name;
	private final int x, y, width, height;
	private final float scaleX, scaleY;
	private final long glfwId;
	private final VideoMode videoMode;

	public Monitor(String name, int x, int y, int width, int height, float scaleX, float scaleY,
			long glfwId, VideoMode videoMode) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.glfwId = glfwId;
		this.videoMode = videoMode;
	}

	public String name() {
		return name;
	}

	public int x() {
		return x;
	}

	public int y() {
		return y;
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	public float scaleX() {
		return scaleX;
	}

	public float scaleY() {
		return scaleY;
	}

	public long glfwId() {
		return glfwId;
	}

	public VideoMode videoMode() {
		return videoMode;
	}

	public static class VideoMode {
		public VideoMode(int width, int height, int refreshRate) {
			this.width = width;
			this.height = height;
			this.refreshRate = refreshRate;
		}

		private final int width, height, refreshRate;

		public int width() {
			return width;
		}

		public int height() {
			return height;
		}

		public int refreshRate() {
			return refreshRate;
		}
	}

}
