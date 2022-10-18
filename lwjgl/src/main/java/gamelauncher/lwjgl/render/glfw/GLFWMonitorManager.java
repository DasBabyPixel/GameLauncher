package gamelauncher.lwjgl.render.glfw;

import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("javadoc")
public class GLFWMonitorManager {

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
		while (mbuf.hasRemaining()) {
			this.newMonitor(mbuf.get());
		}
	}
	
	void cleanup() {
		GLFW.glfwSetMonitorCallback(null);
	}

	private void newMonitor(long monitor) {
		int[] w = new int[1];
		int[] h = new int[1];
		int[] x = new int[1];
		int[] y = new int[1];
		GLFW.glfwGetMonitorWorkarea(monitor, x, y, w, h);
		String name = GLFW.glfwGetMonitorName(monitor);
		float[] sx = new float[1];
		float[] sy = new float[1];
		GLFW.glfwGetMonitorContentScale(monitor, sx, sy);
		Monitor m = new Monitor(name, x[0], y[0], w[0], h[0], sx[0], sy[0], monitor);
		this.monitors.add(m);
	}

	private void removeMonitor(long monitor) {
		ListIterator<Monitor> it = this.monitors.listIterator();
		int index = -1;
		while (it.hasNext()) {
			int i = it.nextIndex();
			Monitor m = it.next();
			if (m.glfwId == monitor) {
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
