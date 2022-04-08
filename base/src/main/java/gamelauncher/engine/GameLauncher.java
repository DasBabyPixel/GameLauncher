package gamelauncher.engine;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import gamelauncher.engine.file.FileSystem;
import gamelauncher.engine.file.Path;
import gamelauncher.engine.render.GameRenderer;
import gamelauncher.engine.render.ModelLoader;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.resource.ResourceLoader;
import gamelauncher.engine.settings.MainSettingSection;
import gamelauncher.engine.settings.SettingSection;
import gamelauncher.engine.util.logging.Logger;

public abstract class GameLauncher {

	public static final String NAME = "GameLauncher";
	public static final int MAX_TPS = 30;
	private GameThread gameThread;
	private Window window;
	private FileSystem fileSystem;
	private Path gameDirectory;
	private Path settingsFile;
	private SettingSection settings = new MainSettingSection();
	private GameRenderer gameRenderer;
	private ModelLoader modelLoader;
	private ResourceLoader resourceLoader;
	private boolean debugMode = false;
	private Gson settingsGson = new GsonBuilder().setPrettyPrinting().create();
	private final Logger logger = Logger.getLogger(getClass());

	protected void setFileSystem(FileSystem fileSystem) {
		this.fileSystem = fileSystem;
		this.gameDirectory = fileSystem.getPath("run").resolve(NAME);
		this.settingsFile = this.gameDirectory.resolve("settings.json");
	}

	protected void setResourceLoader(ResourceLoader loader) {
		this.resourceLoader = loader;
		loader.set();
	}
	
	protected void setModelLoader(ModelLoader loader) {
		this.modelLoader = loader;
	}

	public void handleError(Throwable throwable) {
		throwable.printStackTrace();
	}

	public Logger getLogger() {
		return logger;
	}
	
	public ModelLoader getModelLoader() {
		return modelLoader;
	}

	public ResourceLoader getResourceLoader() {
		return resourceLoader;
	}

	public boolean isDebugMode() {
		return debugMode;
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
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

		gameThread = new GameThread(this);
		gameThread.runLater(() -> start0());
		gameThread.start();

	}
	
	public int getCurrentTick() {
		return gameThread.getCurrentTick();
	}

	public void stop() throws GameException {
		try {
			gameThread.exit().get();
		} catch (InterruptedException | ExecutionException ex) {
			throw new GameException(ex);
		}
	}

	protected abstract void tick() throws GameException;

	protected abstract void start0() throws GameException;
}
