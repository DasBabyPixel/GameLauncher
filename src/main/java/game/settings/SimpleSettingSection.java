package game.settings;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleSettingSection implements SettingSection {

	private final Lock lock = new ReentrantLock(false);
	
	@Override
	public SettingSection getSubSection(SettingPath<SettingSection> path) {
		try {
			lock.lock();
			return null;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public <T> Setting<T> getSetting(SettingPath<T> path) {
		return null;
	}
}
