package gamelauncher.engine.settings;

import com.google.gson.JsonElement;

public interface Setting<T> {

	T getValue();

	void setValue(T value);

	JsonElement serialize();

	void deserialize(JsonElement json);
	
	void setDefaultValue();

}
