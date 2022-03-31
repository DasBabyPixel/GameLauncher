package gamelauncher.engine;

import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import gamelauncher.engine.file.FileSystem;
import gamelauncher.engine.file.Path;
import gamelauncher.engine.render.GameRenderer;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.resource.ResourceLoader;
import gamelauncher.engine.settings.MainSettingSection;
import gamelauncher.engine.settings.SettingSection;
import gamelauncher.engine.util.logging.Logger;

public abstract class GameLauncher {

	public static final String NAME = "GameLauncher";
	public static final Logger logger = Logger.getLogger(GameLauncher.class);
	private Window window;
	private FileSystem fileSystem;
	private Path gameDirectory;
	private Path settingsFile;
	private SettingSection settings = new MainSettingSection();
	private GameRenderer gameRenderer;
	private Gson settingsGson = new GsonBuilder().setPrettyPrinting().create();

	protected void setFileSystem(FileSystem fileSystem) {
		this.fileSystem = fileSystem;
		this.gameDirectory = fileSystem.getPath("run").resolve(NAME);
		this.settingsFile = this.gameDirectory.resolve("settings.json");
	}

	protected void setResourceLoader(ResourceLoader loader) {
		loader.set();
	}

	protected void setGameRenderer(GameRenderer renderer) {
		this.gameRenderer = renderer;
		if (window != null) {
			window.setFrameRenderer(renderer);
		}
	}

	protected void setWindow(Window window) {
		this.window = window;
		this.window.setFrameRenderer(gameRenderer);
	}

	public GameRenderer getGameRenderer() {
		return gameRenderer;
	}

	public void saveSettings() throws GameException {
		fileSystem.write(settingsFile, settingsGson.toJson(settings.serialize()).getBytes(StandardCharsets.UTF_8));
	}

	public final void start() throws GameException {
		if (window != null) {
			return;
		}
		System.setOut(logger.createPrintStream());
		logger.info("Starting " + NAME);

		fileSystem.createDirectories(gameDirectory);
		if (!fileSystem.exists(settingsFile)) {
			fileSystem.createFile(settingsFile);
			settings.setDefaultValue();
			saveSettings();
		} else {
			byte[] bytes = fileSystem.readAllBytes(settingsFile);
			String json = new String(bytes, StandardCharsets.UTF_8);
			JsonElement element = settingsGson.fromJson(json, JsonElement.class);
			settings.deserialize(element);
		}

		start0();
	}

	protected abstract void start0() throws GameException;
}
