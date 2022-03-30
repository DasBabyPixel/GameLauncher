package gamelauncher.engine.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public interface Serializable {

	JsonElement serialize(Gson gson);
	
	void deserialize(Gson gson, JsonElement element);
	
}
