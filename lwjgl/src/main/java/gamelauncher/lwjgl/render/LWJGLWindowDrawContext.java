package gamelauncher.lwjgl.render;

import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.Transformations;
import gamelauncher.engine.render.Transformations.Projection;
import gamelauncher.engine.util.GameException;

@SuppressWarnings("javadoc")
public class LWJGLWindowDrawContext extends LWJGLDrawContext {

	public LWJGLWindowDrawContext(Framebuffer window) {
		super(window);
	}

	@Override
	public void reloadProjectionMatrix() throws GameException {
		Projection projection = this.projection.get();
		if (projection == null) {
			return;
		}
		if (projection instanceof Transformations.Projection.Projection2D) {
			projectionMatrix.identity();
			projectionMatrix.ortho(0, framebuffer.width().floatValue(), 0, framebuffer.height().floatValue(), -10000,
					10000);
		} else {
			super.reloadProjectionMatrix();
		}
	}
}
