package gamelauncher.lwjgl.render.states;

@SuppressWarnings("javadoc")
public class ContextDependant {

	public final GlStates states = new GlStates();
	public final long contextId;

	public ContextDependant(long contextId) {
		this.contextId = contextId;
	}
}
