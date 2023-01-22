package gamelauncher.engine.util.text.format;

public enum TextDecoration {
	BOLD(State.DISABLED, State.ENABLED),
	ITALIC(State.DISABLED, State.ENABLED),
	UNDERLINED(State.DISABLED, State.ENABLED),
	STRIKETHROUGH(State.DISABLED, State.ENABLED);

	private final State defaultState;
	private final State activeState;

	TextDecoration(State defaultState, State activeState) {
		this.defaultState = defaultState;
		this.activeState = activeState;
	}

	public State activeState() {
		return activeState;
	}

	public State defaultState() {
		return defaultState;
	}

	public enum State {
		ENABLED, DISABLED
	}
}
