package gamelauncher.engine.util;

import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

public class Serializer {

	public static final Gson GSON;

	public static byte[] serialize(Serializable serializable) {
		return GSON.toJson(serializable.serialize(GSON)).getBytes(StandardCharsets.UTF_8);
	}

	public static <T extends Serializable> T deserialize(byte[] bytes, Class<T> clazz) throws DeserializerException {
		T t;
		try {
			(t = clazz.newInstance())
					.deserialize(GSON, GSON.fromJson(new String(bytes, StandardCharsets.UTF_8), JsonElement.class));
			return t;
		} catch (JsonSyntaxException | InstantiationException | IllegalAccessException e) {
			throw new DeserializerException(e);
		}
	}

	static {
		GsonBuilder builder = new GsonBuilder();
		builder.serializeNulls().setPrettyPrinting();
		GSON = builder.create();
	}

	public static class DeserializerException extends Exception {

		private static final long serialVersionUID = 1L;

		public DeserializerException() {
		}

		public DeserializerException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
			super(arg0, arg1, arg2, arg3);
		}

		public DeserializerException(String message, Throwable cause) {
			super(message, cause);
		}

		public DeserializerException(String message) {
			super(message);
		}

		public DeserializerException(Throwable cause) {
			super(cause);
		}
	}

}
