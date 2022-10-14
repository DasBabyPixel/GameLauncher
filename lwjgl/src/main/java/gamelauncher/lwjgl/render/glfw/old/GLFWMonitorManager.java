package gamelauncher.lwjgl.render.glfw.old;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWMonitorCallbackI;

public class GLFWMonitorManager {

	void init() {
		glfwSetMonitorCallback(new GLFWMonitorCallbackI() {
			
			@Override
			public void invoke(long monitor, int event) {
				
			}
			
		});
	}

	void cleanup() {
		
	}
	
	private void newMonitor(long monitor) {
		
	}
	
	private void removeMonitor(long monitor) {
		
	}

}
