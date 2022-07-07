package gamelauncher.engine.event;

/**
 * @author DasBabyPixel
 */
public interface EventListener extends Node {

	@Override
	default int priority() {
		return 0;
	}
}
