package gamelauncher.engine.event;

public interface EventListener extends Node {

	@Override
	default int priority() {
		return 0;
	}
}
