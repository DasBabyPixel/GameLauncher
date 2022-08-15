package gamelauncher.lwjgl.render.states;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("javadoc")
public class ContextDependant {

	public final GlStates states = new GlStates();
	public final Map<ContextLocal<?>, Object> contextLocals = new ConcurrentHashMap<>();
	public final long contextId;

	public ContextDependant(long contextId) {
		this.contextId = contextId;
	}
}
