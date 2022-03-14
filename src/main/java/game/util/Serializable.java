package game.util;

import com.google.gson.*;

public interface Serializable {

	JsonElement serialize(Gson gson);
	
	void deserialize(Gson gson, JsonElement element);
	
}
