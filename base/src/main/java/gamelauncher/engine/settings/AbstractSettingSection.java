package gamelauncher.engine.settings;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class AbstractSettingSection implements SettingSection {

	protected final Lock lock = new ReentrantLock(false);
	protected final Map<SettingPath, Setting<?>> settings = new ConcurrentHashMap<>();

	public AbstractSettingSection() {
		addSettings();
	}

	protected abstract void addSettings();

	protected void addSetting(SettingPath path, Setting<?> setting) {
		settings.put(path, setting);
	}

	@Override
	public void setDefaultValue() {
		for (Setting<?> setting : settings.values()) {
			setting.setDefaultValue();
		}
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
			getSetting(path).deserialize(o.get(path.getPath()));
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
}
