package gamelauncher.test;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class WindowTest {

	public static void main(String[] args) {
		glfwInit();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, GLFW_TRUE);
		long w = glfwCreateWindow(400, 400, "test", 0, 0);
		glfwMakeContextCurrent(w);
		GL.createCapabilities();

		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex2d(0.3, 0.3);
		GL11.glVertex2d(0.7, 0.3);
		GL11.glVertex2d(0.5, 0.7);
		GL11.glEnd();
		
		glfwShowWindow(w);

		glfwSwapBuffers(w);
		
		while (!glfwWindowShouldClose(w)) {
			
			glfwWaitEvents();
			glfwPollEvents();
		}
		glfwDestroyWindow(w);
		glfwTerminate();
	}

}
