package gamelauncher.lwjgl.render.states;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;

@SuppressWarnings("javadoc")
public class ContextDependant extends AbstractGameResource {

	public final GlStates states = new GlStates();
	public final Map<ContextLocal<?>, Object> contextLocals = new ConcurrentHashMap<>();
	public final long contextId;

	public ContextDependant(long contextId) {
		this.contextId = contextId;
	}

	@Override
	protected void cleanup0() throws GameException {
		for(ContextLocal<?> local : contextLocals.keySet()) {
			local.remove(this);
		}
	}
}
