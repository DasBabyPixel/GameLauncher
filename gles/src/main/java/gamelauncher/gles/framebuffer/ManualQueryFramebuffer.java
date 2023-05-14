package gamelauncher.gles.framebuffer;

import de.dasbabypixel.api.property.BooleanValue;
import de.dasbabypixel.api.property.InvalidationListener;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.RenderThread;
import gamelauncher.engine.util.GameException;
import java8.util.concurrent.CompletableFuture;

public class ManualQueryFramebuffer extends AbstractFramebuffer {

    private final Framebuffer handle;
    private final BooleanValue newValue = BooleanValue.trueValue();

    public ManualQueryFramebuffer(Framebuffer handle, RenderThread renderThread) {
        super(renderThread, handle::scheduleRedraw);
        this.handle = handle;
        InvalidationListener invalid = property -> ManualQueryFramebuffer.this.newValue.value(true);
        handle.width().addListener(invalid);
        handle.height().addListener(invalid);
        this.query();
    }

    public ManualQueryFramebuffer(Framebuffer handle) {
        this(handle, handle.renderThread());
    }

    public void query() {
        this.newValue.value(false);
        this.width().number(this.handle.width().intValue());
        this.height().number(this.handle.height().intValue());
    }

    public BooleanValue newValue() {
        return this.newValue;
    }

    @Override public CompletableFuture<Void> cleanup0() throws GameException {
        //		this.handle.cleanup();
        return null;
    }

    @Override public void beginFrame() {
        this.handle.beginFrame();
    }

    @Override public void endFrame() {
        this.handle.endFrame();
    }

}
