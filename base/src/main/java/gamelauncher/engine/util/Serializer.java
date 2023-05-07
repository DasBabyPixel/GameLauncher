package gamelauncher.engine.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

/**
 * @author DasBabyPixel
 */
public class Serializer {

    /**
     * The {@link Gson} object to serialize {@link Serializable}s
     */
    public static final Gson GSON;

    /**
     * @param serializable
     * @return the byte[] by the {@link Serializable}
     */
    public static byte[] serialize(Serializable serializable) {
        return GSON.toJson(serializable.serialize(GSON)).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * @param <T>
     * @param bytes
     * @param clazz
     * @return the deserialized value
     * @throws DeserializerException
     */
    public static <T extends Serializable> T deserialize(byte[] bytes, Class<T> clazz) throws DeserializerException {
        T t;
        try {
            (t = clazz.getDeclaredConstructor().newInstance()).deserialize(GSON, GSON.fromJson(new String(bytes, StandardCharsets.UTF_8), JsonElement.class));
            return t;
        } catch (JsonSyntaxException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new DeserializerException(e);
        }
    }

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls().setPrettyPrinting();
        GSON = builder.create();
    }

    /**
     * @author DasBabyPixel
     */
    public static class DeserializerException extends Exception {

        private static final long serialVersionUID = 1L;

        /**
         *
         */
        public DeserializerException() {
        }

        /**
         * @param arg0
         * @param arg1
         * @param arg2
         * @param arg3
         */
        public DeserializerException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
            super(arg0, arg1, arg2, arg3);
        }

        /**
         * @param message
         * @param cause
         */
        public DeserializerException(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * @param message
         */
        public DeserializerException(String message) {
            super(message);
        }

        /**
         * @param cause
         */
        public DeserializerException(Throwable cause) {
            super(cause);
        }
    }

}
