package gamelauncher.engine.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * @author DasBabyPixel
 */
public interface Serializable {

	/**
	 * Serializes this object
	 * 
	 * @param gson
	 * @return the {@link JsonElement} serialized of this {@link Serializable}
	 */
	JsonElement serialize(Gson gson);

	/**
	 * Deserializes the {@link JsonElement}
	 * @param gson
	 * @param element
	 */
	void deserialize(Gson gson, JsonElement element);

}
