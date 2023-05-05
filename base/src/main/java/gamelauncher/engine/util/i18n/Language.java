package gamelauncher.engine.util.i18n;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.data.Files;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.Key;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author DasBabyPixel
 */
public class Language {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    private final Locale locale;
    private final Map<String, String> map = new HashMap<>();
    private final Language[] fallbacks;

    Language(Locale locale, Language... fallbacks) {
        this.locale = locale;
        this.fallbacks = fallbacks;
    }

    @Api public Locale locale() {
        return locale;
    }

    @Api public void load(Path path) throws GameException {
        load(path, Charsets.UTF_8);
    }

    @Api public void load(Key key) throws GameException {
        load(key.toPath(key.plugin().launcher().assets()));
    }

    @Api public void load(Path path, Charset charset) throws GameException {
        String s = new String(Files.readAllBytes(path), charset);
        JsonObject object = gson.fromJson(s, JsonObject.class);
        for (String key : object.keySet()) {
            map.put(key, object.getAsJsonPrimitive(key).getAsString());
        }
    }

    @Api public String translate(Message message, Object... objects) {
        return translate(message.key(), objects);
    }

    @Api public String translate(Key key, Object... objects) {
        if (map.containsKey(key.toString())) {
            return String.format(map.get(key.toString()), objects);
        }
        for (Language fallback : fallbacks) {
            String s = fallback.translate(key, objects);
            if (s != null) return s;
        }
        return key + (objects.length != 0 ? Arrays.toString(objects) : "");
    }
}
