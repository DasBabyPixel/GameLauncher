package gamelauncher.lwjgl.render;

import static org.lwjgl.opengles.GLES20.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import gamelauncher.lwjgl.render.states.GlStates;

@SuppressWarnings("javadoc")
public class GlContext {

	public final DepthState depth = new DepthState();

	public final BlendState blend = new BlendState();

	public final CullState cull = new CullState();

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
		if (current.cull.enabled.value.get() != cull.enabled.value.get()) {
			current.cull.enabled.apply();
		}
		if (current.cull.cullFace.get() != cull.cullFace.get()) {
			cull.applyValues();
		}
	}

	public static class CullState {

		public final BooleanState enabled = new BooleanState(GL_CULL_FACE);

		public final AtomicInteger cullFace = new AtomicInteger(GL_BACK);

		public void applyValues() {
			GlStates.current().cullFace(cullFace.get());
		}

	}

	public static class BlendState {

		public final BooleanState enabled = new BooleanState(GL_BLEND);

		public final AtomicBoolean separate = new AtomicBoolean(false);

		public final AtomicInteger srcrgb = new AtomicInteger(GL_SRC_ALPHA);

		public final AtomicInteger dstrgb = new AtomicInteger(GL_ONE_MINUS_SRC_ALPHA);

		public final AtomicInteger srcalpha = new AtomicInteger(GL_ONE);

		public final AtomicInteger dstalpha = new AtomicInteger(GL_ONE);

		public void applyValues() {
			if (separate.get()) {
				GlStates.current().blendFuncSeparate(srcrgb.get(), dstrgb.get(), srcalpha.get(), dstalpha.get());
			} else {
				GlStates.current().blendFunc(srcrgb.get(), dstrgb.get());
			}
		}

	}

	public static class DepthState {

		public final BooleanState enabled = new BooleanState(GL_DEPTH_TEST);

		public final AtomicInteger depthFunc = new AtomicInteger(GL_LEQUAL);

		public void applyValues() {
			GlStates.current().depthFunc(depthFunc.get());
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
				GlStates.current().enable(state);
			} else {
				GlStates.current().disable(state);
			}
		}

	}

}
