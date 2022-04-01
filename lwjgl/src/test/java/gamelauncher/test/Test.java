package gamelauncher.test;

import gamelauncher.engine.GameException;
import gamelauncher.lwjgl.LWJGLGameLauncher;

public class Test {

	public static void main(String[] args) throws GameException {
		LWJGLGameLauncher launcher = new LWJGLGameLauncher();
		launcher.start();
//		launcher.getGameRenderer().setRenderer(new Renderer() {
//			private double r1 = 0.0, g1 = 0.3, b1 = 0.6;
//			private double r2 = 0.5, g2 = 0.8, b2 = 0.1;
//			private double r3 = 0.8, g3 = 0.1, b3 = 0.9;
//
//			@Override
//			public void render(Window window, DrawContext context) {
//				glBegin(GL_TRIANGLES);
//
//				double min = -(Math.sin(Math.toRadians(System.currentTimeMillis() / 20)) + 1) / 3 - 0.3;
//
//				glVertex2d(min, -0.5);
//				glColor3d(r1, g1, b1);
//				glVertex2d(0, 0.5);
//				glColor3d(r2, g2, b2);
//				glVertex2d(-min, -0.5);
//				glColor3d(r3, g3, b3);
//				glEnd();
//			}
//		});
	}
}
