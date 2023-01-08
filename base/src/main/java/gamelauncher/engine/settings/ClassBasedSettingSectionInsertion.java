package gamelauncher.engine.settings;

import gamelauncher.engine.event.events.settings.SettingSectionConstructEvent;

/**
 * @author DasBabyPixel
 */
public abstract class ClassBasedSettingSectionInsertion extends SettingSectionInsertion {

	private final Class<?> target;

	public ClassBasedSettingSectionInsertion(Class<?> target) {
		this.target = target;
	}

	@Override
	protected boolean shouldHandle(SettingSectionConstructEvent event) {
		return super.shouldHandle(event) && event.getConstructor().getSection().getClass()
				.equals(target);
	}
}
