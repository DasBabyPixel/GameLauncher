package gamelauncher.gles;

import de.dasbabypixel.api.property.NumberChangeListener;
import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;

import java.lang.ref.WeakReference;

public class GLESDrawContextFramebufferChangeListener extends AbstractGameResource implements NumberChangeListener {

    private final WeakReference<GLESDrawContext> ref;

    private final NumberValue w, h;

    public GLESDrawContextFramebufferChangeListener(GLESDrawContext ctx, NumberValue fbw, NumberValue fbh) {
        this.ref = new WeakReference<>(ctx);
        this.w = fbw;
        this.h = fbh;
        this.w.addListener(this);
        this.h.addListener(this);
    }

    @Override
    public void handleChange(NumberValue value, Number oldValue, Number newValue) {
        GLESDrawContext ctx = ref.get();
        if (ctx == null) {
            try {
                cleanup();
            } catch (GameException ex) {
                throw new Error(ex);
            }
        } else {
            try {
                ctx.invalidateProjectionMatrix();
            } catch (GameException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void cleanup0() {
        w.removeListener(this);
        h.removeListener(this);
    }
}
