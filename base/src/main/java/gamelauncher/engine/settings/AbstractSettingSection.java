package gamelauncher.engine.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gamelauncher.engine.event.EventManager;
import gamelauncher.engine.event.events.settings.SettingSectionConstructEvent;
import gamelauncher.engine.util.logging.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author DasBabyPixel
 */
public abstract class AbstractSettingSection implements SettingSection {

	protected final Lock lock = new ReentrantLock(false);
	protected final Map<SettingPath, Setting<?>> settings = new ConcurrentHashMap<>();
	protected final Logger logger = Logger.getLogger(SettingSection.class);

	public AbstractSettingSection(EventManager eventManager) {
		addSettings(eventManager);
		SettingSectionConstructor constructor = new SettingSectionConstructor(this, eventManager);
		eventManager.post(new SettingSectionConstructEvent(constructor));
		settings.putAll(constructor.settings);
	}

	protected abstract void addSettings(EventManager eventManager);

	protected void addSetting(SettingPath path, Setting<?> setting) {
		settings.put(path, setting);
	}

	@Override
	public JsonElement serialize() {
		JsonObject json = new JsonObject();
		for (Map.Entry<SettingPath, Setting<?>> entry : settings.entrySet()) {
			json.add(entry.getKey().getPath(), entry.getValue().serialize());
		}
		return json;
	}

	@Override
	public void deserialize(JsonElement json) {
		JsonObject o = json.getAsJsonObject();
		for (Map.Entry<String, JsonElement> entry : o.entrySet()) {
			SettingPath path = new SettingPath(entry.getKey());
			Setting<?> setting = getSetting(path);
			if (setting != null) {
				setting.deserialize(o.get(path.getPath()));
			} else {
				logger.warnf("SettingSection key missing: %s%n -> %s", entry.getKey(),
						entry.getValue().toString());
			}
		}
	}

	@Override
	public void setDefaultValue() {
		for (Setting<?> setting : settings.values()) {
			setting.setDefaultValue();
		}
	}

	@Override
	public SettingSection getSubSection(SettingPath path) {
		try {
			lock.lock();
			Setting<SettingSection> setting = getSetting(path);
			if (setting != null) {
				return setting.getValue();
			}
			return null;
		} finally {
			lock.unlock();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Setting<T> getSetting(SettingPath path) {
		try {
			lock.lock();
			Setting<?> setting = settings.get(path);
			if (setting == null) {
				return null;
			}
			return (Setting<T>) setting;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * @author DasBabyPixel
	 */
	public static class SettingSectionConstructor {

		private final Map<SettingPath, Setting<?>> settings = new HashMap<>();
		private final AbstractSettingSection section;
		private final EventManager eventManager;

		public SettingSectionConstructor(AbstractSettingSection section,
				EventManager eventManager) {
			this.section = section;
			this.eventManager = eventManager;
		}

		/**
		 * Adds a setting
		 *
		 * @param path    the path to identify the setting by
		 * @param setting the setting
		 */
		public void addSetting(SettingPath path, Setting<?> setting) {
			settings.put(path, setting);
		}

		/**
		 * @return the {@link AbstractSettingSection}
		 */
		public AbstractSettingSection getSection() {
			return section;
		}

		/**
		 * @return the {@link EventManager}
		 */
		public EventManager getEventManager() {
			return eventManager;
		}
	}
}
