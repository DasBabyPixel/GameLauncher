package gamelauncher.lwjgl.render.glfw;

import gamelauncher.engine.util.logging.Logger;
import gamelauncher.lwjgl.render.glfw.Monitor.VideoMode;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import java.util.Collection;
import java.util.Collections;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class GLFWMonitorManager {

    private final Logger logger = Logger.logger();
    private final CopyOnWriteArrayList<Monitor> monitors = new CopyOnWriteArrayList<>();

    void init() {
        GLFW.glfwSetMonitorCallback((monitor, event) -> {
            if (event == GLFW.GLFW_CONNECTED) {
                GLFWMonitorManager.this.newMonitor(monitor);
            } else if (event == GLFW.GLFW_DISCONNECTED) {
                GLFWMonitorManager.this.removeMonitor(monitor);
            }
        });
        PointerBuffer mbuf = GLFW.glfwGetMonitors();
        assert mbuf != null;
        while (mbuf.hasRemaining()) {
            this.newMonitor(mbuf.get());
        }
    }

    public Monitor getMonitor(long glfwId) {
        for (Monitor monitor : monitors) {
            if (monitor.glfwId() == glfwId) return monitor;
        }
        return null;
    }

    public Collection<Monitor> getMonitors() {
        return Collections.unmodifiableCollection(monitors);
    }

    void cleanup() {
        GLFW.glfwSetMonitorCallback(null);
    }

    private synchronized void newMonitor(long monitor) {
        int[] w = new int[1];
        int[] h = new int[1];
        int[] x = new int[1];
        int[] y = new int[1];
        GLFW.glfwGetMonitorWorkarea(monitor, x, y, w, h);
        String name = GLFW.glfwGetMonitorName(monitor);
        float[] sx = new float[1];
        float[] sy = new float[1];
        GLFW.glfwGetMonitorContentScale(monitor, sx, sy);
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(monitor);
        assert vidMode != null;
        Monitor m = new Monitor(name, x[0], y[0], vidMode.width(), vidMode.height(), sx[0], sy[0], monitor, new VideoMode(w[0], h[0], vidMode.refreshRate()));
        logger.infof("New Monitor connected! %s[x=%s, y=%s, width=%s, height=%s, " + "refreshRate=%sHz, scaleX=%.2f, scaleY=%.2f]", m.name(), m.x(), m.y(), m.width(), m.height(), m.videoMode().refreshRate(), m.scaleX(), m.scaleY());
        this.monitors.add(m);
    }

    private synchronized void removeMonitor(long monitor) {
        ListIterator<Monitor> it = this.monitors.listIterator();
        int index = -1;
        while (it.hasNext()) {
            int i = it.nextIndex();
            Monitor m = it.next();
            if (m.glfwId() == monitor) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new IllegalStateException();
        }
        this.monitors.remove(index);
    }

}
