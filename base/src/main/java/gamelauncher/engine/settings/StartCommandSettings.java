package gamelauncher.engine.settings;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author DasBabyPixel
 */
public class StartCommandSettings {

	public final Collection<Path> externalPlugins = new HashSet<>();

	private StartCommandSettings() {
	}

	public static StartCommandSettings parse(String[] args) {
		StartCommandSettings settings = new StartCommandSettings();
		for (String arg : args) {
			String[] a0 = arg.split(":", 2);
			if (a0[0].equals("externalPlugin")) {
				Path path = Paths.get(a0[1]);
				settings.externalPlugins.add(path);
			}
		}
		return settings;
	}
}
