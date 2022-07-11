package gamelauncher.lwjgl.render;

import java.lang.ref.WeakReference;

import de.dasbabypixel.api.property.NumberChangeListener;
import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.util.function.GameResource;

@SuppressWarnings("javadoc")
public class DrawContextFramebufferChangeListener implements NumberChangeListener, GameResource {

	private final WeakReference<LWJGLDrawContext> ref;
	private final NumberValue w, h;

	public DrawContextFramebufferChangeListener(LWJGLDrawContext ctx, NumberValue fbw, NumberValue fbh) {
		this.ref = new WeakReference<LWJGLDrawContext>(ctx);
		this.w = fbw;
		this.h = fbh;
		this.w.addListener(this);
		this.h.addListener(this);
	}

	@Override
	public void handleChange(NumberValue value, Number oldValue, Number newValue) {
		LWJGLDrawContext ctx = ref.get();
		if (ctx == null) {
			cleanup();
		} else {
			ctx.invalidateProjectionMatrix();
		}
	}

	@Override
	public void cleanup() {
		w.removeListener(this);
		h.removeListener(this);
	}
}
