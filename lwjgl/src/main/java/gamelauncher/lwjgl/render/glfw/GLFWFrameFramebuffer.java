package gamelauncher.lwjgl.render.glfw;

import de.dasbabypixel.api.property.BooleanValue;
import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.ScissorStack;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.gles.GLESScissorStack;
import gamelauncher.gles.states.StateRegistry;
import gamelauncher.gles.util.GLDebugUtil;
import java8.util.concurrent.CompletableFuture;
import org.lwjgl.glfw.GLFW;

public class GLFWFrameFramebuffer extends AbstractGameResource implements Framebuffer {

    private final NumberValue width = NumberValue.withValue(0);
    private final NumberValue height = NumberValue.withValue(0);
    private final BooleanValue swapBuffers = BooleanValue.falseValue();
    private final GLFWFrame frame;
    private final GLESScissorStack scissor;

    public GLFWFrameFramebuffer(GLFWFrame frame) {
        this.frame = frame;
        this.scissor = new GLESScissorStack(this);
    }

    @Override public void beginFrame() {
    }

    @Override public void endFrame() {
        if (this.swapBuffers.booleanValue()) {
            GLDebugUtil.skip.set(true);
            GLFW.glfwSwapBuffers(this.frame.context.glfwId);
            GLDebugUtil.skip.remove();
            StateRegistry.currentGl().glGetError();
        }
    }

    @Override public void scheduleRedraw() {
        frame.scheduleDraw();
    }

    @Override protected CompletableFuture<Void> cleanup0() {
        return null;
    }

    @Override public GLFWFrameRenderThread renderThread() {
        return this.frame.renderThread;
    }

    @Override public ScissorStack scissorStack() {
        return this.scissor;
    }

    public BooleanValue swapBuffers() {
        return this.swapBuffers;
    }

    @Override public NumberValue width() {
        return this.width;
    }

    @Override public NumberValue height() {
        return this.height;
    }

    public GLFWFrame frame() {
        return this.frame;
    }

}
