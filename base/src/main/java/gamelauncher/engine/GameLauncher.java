package gamelauncher.engine;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.concurrent.ExecutionException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import gamelauncher.engine.event.EventManager;
import gamelauncher.engine.file.FileSystem;
import gamelauncher.engine.file.Path;
import gamelauncher.engine.plugins.PluginManager;
import gamelauncher.engine.render.Camera;
import gamelauncher.engine.render.GameRenderer;
import gamelauncher.engine.render.ModelLoader;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.render.font.GlyphProvider;
import gamelauncher.engine.resource.ResourceLoader;
import gamelauncher.engine.settings.MainSettingSection;
import gamelauncher.engine.settings.SettingSection;
import gamelauncher.engine.util.logging.LogLevel;
import gamelauncher.engine.util.logging.Logger;

public abstract class GameLauncher {

	public static final String NAME = "GameLauncher";
	public static final int MAX_TPS = 60;
	private final EventManager eventManager;
	private final Logger logger;
	private GameThread gameThread;
	private Window window;
	private FileSystem fileSystem;
	private FileSystem embedFileSystem;
	private Path gameDirectory;
	private Path dataDirectory;
	private Path settingsFile;
	private Path pluginsDirectory;
	private SettingSection settings;
	private GameRenderer gameRenderer;
	private ModelLoader modelLoader;
	private GlyphProvider glyphProvider;
	private Camera camera;
	private PluginManager pluginManager;
	private ResourceLoader resourceLoader;
	private boolean debugMode = false;
	private Gson settingsGson = new GsonBuilder().setPrettyPrinting().create();

	public GameLauncher() {
		this.logger = Logger.getLogger(getClass());
		this.eventManager = new EventManager();
		this.pluginManager = new PluginManager(this);
		registerSettingInsertions();
		this.settings = new MainSettingSection(eventManager);
	}

	protected void setFileSystem(FileSystem fileSystem, FileSystem embedFileSystem) {
		this.fileSystem = fileSystem;
		this.embedFileSystem = embedFileSystem;
		this.gameDirectory = this.fileSystem.getPath(NAME);
		this.dataDirectory = this.gameDirectory.resolve("data");
		this.settingsFile = this.gameDirectory.resolve("settings.json");
		this.pluginsDirectory = this.gameDirectory.resolve("plugins");
	}

	public PluginManager getPluginManager() {
		return pluginManager;
	}

	protected void setResourceLoader(ResourceLoader loader) {
		this.resourceLoader = loader;
		loader.set();
	}

	protected void setModelLoader(ModelLoader loader) {
		this.modelLoader = loader;
	}

	public GlyphProvider getGlyphProvider() {
		return glyphProvider;
	}

	public void setGlyphProvider(GlyphProvider glyphProvider) {
		this.glyphProvider = glyphProvider;
	}

	public void handleError(Throwable throwable) {
		throwable.printStackTrace();
	}

	public Camera getCamera() {
		return camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public FileSystem getEmbedFileSystem() {
		return embedFileSystem;
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

	public Path getDataDirectory() {
		return dataDirectory;
	}

	public FileSystem getFileSystem() {
		return fileSystem;
	}

	public Path getGameDirectory() {
		return gameDirectory;
	}

	public Path getPluginsDirectory() {
		return pluginsDirectory;
	}

	public GameRenderer getGameRenderer() {
		return gameRenderer;
	}

	public void saveSettings() throws GameException {
		fileSystem.write(settingsFile, settingsGson.toJson(settings.serialize()).getBytes(StandardCharsets.UTF_8));
	}

	protected void registerSettingInsertions() {
	}

	public final void start() throws GameException {

		try {
//			System.out.println(Paths.get(getClass().getClassLoader().getResource("cube.obj").toURI()));
			java.nio.file.FileSystem efs = FileSystems.newFileSystem(URI.create("embed:/"), null);
			java.nio.file.Path path = efs.getPath("cube.obj");
			System.out.println("Lines: " + Files.readAllLines(path));
//			efs.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (window != null) {
			return;
		}
		System.setOut(logger.createPrintStream(LogLevel.STDOUT));
		System.setErr(logger.createPrintStream(LogLevel.STDERR));
		logger.info("Starting " + NAME);

		fileSystem.createDirectories(gameDirectory);
		fileSystem.createDirectories(dataDirectory);
		fileSystem.createDirectories(pluginsDirectory);
		if (!fileSystem.exists(settingsFile)) {
			fileSystem.createFile(settingsFile);
			settings.setDefaultValue();
			saveSettings();
		} else {
			byte[] bytes = fileSystem.readAllBytes(settingsFile);
			String json = new String(bytes, StandardCharsets.UTF_8);
			JsonElement element = settingsGson.fromJson(json, JsonElement.class);
			settings.deserialize(element);
			JsonElement serialized = settingsGson.toJsonTree(settings.serialize());
			if (!serialized.equals(element)) {
				getLogger().warnf("Unexpected change in settings.json. Creating backup and replacing file.");
				DateTimeFormatter formatter = new DateTimeFormatterBuilder()
						.appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
						.appendLiteral('-')
						.appendValue(ChronoField.MONTH_OF_YEAR, 2)
						.appendLiteral('-')
						.appendValue(ChronoField.DAY_OF_MONTH, 2)
						.appendLiteral('_')
						.appendValue(ChronoField.HOUR_OF_DAY, 2)
						.appendLiteral('-')
						.appendValue(ChronoField.MINUTE_OF_HOUR, 2)
						.appendLiteral('-')
						.appendValue(ChronoField.SECOND_OF_MINUTE, 2)
						.toFormatter();
				fileSystem.move(settingsFile,
						settingsFile.getParent()
								.resolve(String.format("backup-settings-%s.json",
										formatter.format(LocalDateTime.now()).replace(':', '-'))));
				saveSettings();
			}
		}

		gameThread = new GameThread(this);
		gameThread.runLater(() -> {
			start0();

		});
		gameThread.start();

		this.pluginManager.loadPlugin(this.getEmbedFileSystem().getPath("orbits-0.0.1-SNAPSHOT.jar"));
		new Exception().printStackTrace();

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
