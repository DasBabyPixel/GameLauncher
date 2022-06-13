package gamelauncher.lwjgl.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class GlContext {

	public final DepthState depth = new DepthState();
	public final BlendState blend = new BlendState();

	public void replace(GlContext current) {
		if (current == null) {
			blend.enabled.apply();
			blend.applyValues();
			depth.enabled.apply();
			depth.applyValues();
			return;
		}
		if (current.blend.enabled.value.get() != blend.enabled.value.get()) {
			blend.enabled.apply();
		}
		if (current.blend.srcrgb.get() != blend.srcrgb.get() || current.blend.dstrgb.get() != blend.dstrgb.get()
				|| current.blend.srcalpha.get() != blend.srcalpha.get()
				|| current.blend.dstalpha.get() != blend.dstalpha.get()) {
			blend.applyValues();
		}
		if (current.depth.enabled.value.get() != depth.enabled.value.get()) {
			depth.enabled.apply();
		}
		if (current.depth.depthFunc.get() != depth.depthFunc.get()) {
			depth.applyValues();
		}
	}

	public static class BlendState {
		public final BooleanState enabled = new BooleanState(GL_BLEND);
		public final AtomicInteger srcrgb = new AtomicInteger(GL_SRC_ALPHA);
		public final AtomicInteger dstrgb = new AtomicInteger(GL_ONE_MINUS_SRC_ALPHA);
		public final AtomicInteger srcalpha = new AtomicInteger(GL_ONE);
		public final AtomicInteger dstalpha = new AtomicInteger(GL_ONE);

		public void applyValues() {
			glBlendFuncSeparate(srcrgb.get(), dstrgb.get(), srcalpha.get(), dstalpha.get());
		}
	}

	public static class DepthState {
		public final BooleanState enabled = new BooleanState(GL_DEPTH_TEST);
		public final AtomicInteger depthFunc = new AtomicInteger(GL_LEQUAL);

		public void applyValues() {
			glDepthFunc(depthFunc.get());
		}
	}

	public static class BooleanState {
		public final AtomicBoolean value = new AtomicBoolean(false);
		public final int state;

		public BooleanState(int state) {
			this.state = state;
		}

		public void apply() {
			if (value.get()) {
				glEnable(state);
			} else {
				glDisable(state);
			}
		}
	}
}
