package gamelauncher.lwjgl.render.glfw;

import java.util.Objects;

public class Monitor {

    private final String name;
    private final int x, y, width, height;
    private final float scaleX, scaleY;
    private final long glfwId;
    private final VideoMode videoMode;

    public Monitor(String name, int x, int y, int width, int height, float scaleX, float scaleY, long glfwId, VideoMode videoMode) {
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

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Monitor monitor = (Monitor) o;
        return x == monitor.x && y == monitor.y && width == monitor.width && height == monitor.height && Float.compare(monitor.scaleX, scaleX) == 0 && Float.compare(monitor.scaleY, scaleY) == 0 && glfwId == monitor.glfwId && Objects.equals(name, monitor.name) && Objects.equals(videoMode, monitor.videoMode);
    }

    @Override public int hashCode() {
        return Objects.hash(name, x, y, width, height, scaleX, scaleY, glfwId, videoMode);
    }

    public static class VideoMode {
        private final int width, height, refreshRate;

        public VideoMode(int width, int height, int refreshRate) {
            this.width = width;
            this.height = height;
            this.refreshRate = refreshRate;
        }

        public int width() {
            return width;
        }

        public int height() {
            return height;
        }

        public int refreshRate() {
            return refreshRate;
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            VideoMode videoMode = (VideoMode) o;
            return width == videoMode.width && height == videoMode.height && refreshRate == videoMode.refreshRate;
        }

        @Override public int hashCode() {
            return Objects.hash(width, height, refreshRate);
        }
    }

}
