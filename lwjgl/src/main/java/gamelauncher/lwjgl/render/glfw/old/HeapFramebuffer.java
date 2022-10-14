package gamelauncher.lwjgl.render.glfw.old;

import gamelauncher.engine.render.Frame;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.RenderThread;
import gamelauncher.lwjgl.render.framebuffer.AbstractFramebuffer;

/**
 * A {@link Framebuffer} that only exists on the java heap, not in OpenGL
 * memory. Used for when we need a {@link Framebuffer} java object but there is
 * no context available. Can't use this in GL operations
 * 
 * @author DasBabyPixel
 */
@SuppressWarnings("javadoc")
public class HeapFramebuffer extends AbstractFramebuffer {

	public HeapFramebuffer(Frame frame, RenderThread renderThread) {
		super(renderThread, frame::scheduleDraw);
	}

	@Override
	public void beginFrame() {
	}

	@Override
	public void endFrame() {
	}

}
