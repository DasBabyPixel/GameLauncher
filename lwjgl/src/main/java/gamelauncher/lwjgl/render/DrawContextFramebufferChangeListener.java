package gamelauncher.lwjgl.render;

import java.lang.ref.WeakReference;

import de.dasbabypixel.api.property.NumberChangeListener;
import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;

public class DrawContextFramebufferChangeListener extends AbstractGameResource implements NumberChangeListener {

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
