package gamelauncher.engine.settings;

import com.google.gson.JsonElement;

/**
 * @author DasBabyPixel
 * @param <T>
 */
public interface Setting<T> {

	/**
	 * @return the value of this section
	 */
	T getValue();

	/**
	 * @param value
	 */
	void setValue(T value);

	/**
	 * @return the {@link JsonElement} for this {@link Setting}
	 */
	JsonElement serialize();

	/**
	 * Loads this {@link Setting} from a {@link JsonElement}
	 * @param json
	 */
	void deserialize(JsonElement json);

	/**
	 * Sets the default value for this setting
	 */
	void setDefaultValue();

}
