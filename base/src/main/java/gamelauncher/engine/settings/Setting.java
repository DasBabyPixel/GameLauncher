package gamelauncher.engine.settings;

import com.google.gson.JsonElement;

/**
 * @param <T>
 *
 * @author DasBabyPixel
 */
public interface Setting<T> {

	/**
	 * @return the value of this section
	 */
	T getValue();

	/**
	 * @param value the new value
	 */
	void setValue(T value);

	/**
	 * @return the {@link JsonElement} for this {@link Setting}
	 */
	JsonElement serialize();

	/**
	 * Loads this {@link Setting} from a {@link JsonElement}
	 *
	 * @param json the json to load from
	 */
	void deserialize(JsonElement json);

	/**
	 * Sets the default value for this setting
	 */
	void setDefaultValue();

}
