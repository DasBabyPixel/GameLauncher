package gamelauncher.engine.util.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Phaser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.io.Files;
import gamelauncher.engine.resource.Resource;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.Arrays;
import gamelauncher.engine.util.GameException;

class LanguageReader {

	private static final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

	static Language[] readLanguages(GameLauncher launcher, Resource... resources) throws GameException {
		final Phaser phaser = new Phaser();
		phaser.register();
		LanguageData[] adata = new LanguageData[resources.length];
		JsonObject[] jobjects = new JsonObject[resources.length];
		for (int i = 0; i < resources.length; i++) {
			Resource resource = resources[i];
			ResourceStream stream = resource.newResourceStream();
			try {
				String str = stream.readUTF8Fully();
				JsonObject obj;
				try {
					obj = gson.fromJson(str, JsonObject.class);
				} catch (JsonSyntaxException ex) {
					throw new GameException("Invalid language", ex);
				}
				if (!obj.has("id")) {
					throw new GameException("Language file has no id");
				}
				jobjects[i] = obj;
				adata[i] = new LanguageData(localeFromId(obj.get("id").getAsString()), stream.getPath());
			} finally {
				stream.cleanup();
			}
		}
		for (int i = 0; i < jobjects.length; i++) {
			LanguageData data = adata[i];
			JsonObject o = jobjects[i];
			if (o.has("fallbacks")) {
				JsonElement e = o.get("fallbacks");
				if (!e.isJsonArray()) {
					throw new GameException("Invalid language file format: " + data.locale.toLanguageTag());
				}
				JsonArray ja = e.getAsJsonArray();
				for (JsonElement element : ja) {
					if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
						throw new GameException("Invalid language file format: " + data.locale.toLanguageTag());
					}
					String sfallback = element.getAsJsonPrimitive().getAsString();
					for (LanguageData d : adata) {
						if (d == data)
							continue;
						if (idFromLocale(d.locale).equals(sfallback)) {
							data.fallbacks.add(d);
							break;
						}
					}
				}
			}
			if (o.has("data")) {
				JsonElement edata = o.get("data");
				loadAsync(launcher, phaser, data, edata);
			}
		}
		Map<LanguageData, Language> languageMap = new HashMap<>();
		List<LanguageData> ldata = Arrays.asList(adata);
		Language[] languages = new Language[adata.length];
		for (int i = 0; i < languages.length; i++) {
			LanguageData d = adata[i];
			languages[i] = new Language(d.locale, new Language[d.fallbacks.size()]);
			languageMap.put(d, languages[i]);
		}
		for (int i = 0; i < languages.length; i++) {
			Language l = languages[i];
			LanguageData d = adata[i];
			for (int j = 0; j < d.fallbacks.size(); j++) {
				LanguageData dfb = d.fallbacks.get(j);
				int index = ldata.indexOf(dfb);
				l.fallbacks[j] = languages[index];
			}
		}
		phaser.arriveAndAwaitAdvance();
		return languages;
	}

	private static void loadAsync(GameLauncher launcher, Phaser phaser, LanguageData data, JsonElement edata)
			throws GameException {
		if (edata.isJsonArray()) {
			for (JsonElement e : edata.getAsJsonArray()) {
				loadAsync(launcher, phaser, data, e);
			}
		} else if (edata.isJsonPrimitive()) {
			final String spath = edata.getAsJsonPrimitive().getAsString();
			final Path path = data.languageFile.resolveSibling(spath);
			phaser.register();
			launcher.getThreads().cached.submit(() -> {
				Properties p = new Properties();
				try (InputStream in = Files.newInputStream(path, StandardOpenOption.READ)) {
					p.load(in);
				} catch (IOException ex) {
					throw new GameException(ex);
				}
				for (Object oe : p.keySet()) {
					data.data.put((String) oe, p.getProperty((String) oe));
				}
				phaser.arriveAndDeregister();
			});
		} else {
			throw new GameException("Invalid language file format");
		}
	}

	private static String idFromLocale(Locale locale) {
		return locale.toLanguageTag();
	}

	private static Locale localeFromId(String id) {
		return Locale.forLanguageTag(id);
	}

	static class LanguageData {

		final Locale locale;

		final List<LanguageData> fallbacks;

		final Map<String, String> data = new ConcurrentHashMap<>();

		final Path languageFile;

		public LanguageData(Locale locale, Path languageFile) {
			super();
			this.locale = locale;
			this.languageFile = languageFile;
			this.fallbacks = new ArrayList<>(4);
		}

	}

}
