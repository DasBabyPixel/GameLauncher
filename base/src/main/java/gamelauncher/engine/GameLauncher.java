package gamelauncher.engine;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import gamelauncher.engine.event.events.LauncherInitializedEvent;
import gamelauncher.engine.file.Files;
import gamelauncher.engine.file.embed.EmbedFileSystem;
import gamelauncher.engine.file.embed.url.EmbedURLStreamHandlerFactory;
import gamelauncher.engine.game.Game;
import gamelauncher.engine.game.GameRegistry;
import gamelauncher.engine.gui.GuiManager;
import gamelauncher.engine.gui.GuiRenderer;
import gamelauncher.engine.launcher.gui.MainScreenGui;
import gamelauncher.engine.plugin.PluginManager;
import gamelauncher.engine.render.Camera;
import gamelauncher.engine.render.DrawContext;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.GameRenderer;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.render.font.GlyphProvider;
import gamelauncher.engine.render.model.ModelLoader;
import gamelauncher.engine.render.shader.ShaderLoader;
import gamelauncher.engine.resource.ResourceLoader;
import gamelauncher.engine.settings.MainSettingSection;
import gamelauncher.engine.settings.SettingSection;
import gamelauncher.engine.settings.StartCommandSettings;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.OperatingSystem;
import gamelauncher.engine.util.keybind.KeybindManager;
import gamelauncher.engine.util.logging.LogLevel;
import gamelauncher.engine.util.logging.Logger;

/**
 * @author DasBabyPixel
 */
public abstract class GameLauncher {

	/**
	 * The GameLauncher name
	 */
	public static final String NAME = "GameLauncher";
	/**
	 * The max TPS in the {@link GameThread}
	 */
	public static final int MAX_TPS = 60;
	private final EventManager eventManager;
	private final Logger logger;
	private GameThread gameThread;
	private Window window;
//	private FileSystem fileSystem;
	private FileSystem embedFileSystem;
	private Path gameDirectory;
	private Path dataDirectory;
	private Path settingsFile;
	private Path pluginsDirectory;
	private OperatingSystem operatingSystem;
	private SettingSection settings;
	private GameRenderer gameRenderer;
	private ModelLoader modelLoader;
	private GlyphProvider glyphProvider;
	private ShaderLoader shaderLoader;
	private GuiManager guiManager;
	private KeybindManager keybindManager;
	private Camera camera;
	private PluginManager pluginManager;
	private ResourceLoader resourceLoader;
	private boolean debugMode = false;
	private Gson settingsGson = new GsonBuilder().setPrettyPrinting().create();
	private GameRegistry gameRegistry;
	private Game currentGame;

	/**
	 * Creates a new GameLauncher
	 */
	public GameLauncher() {
		this.logger = Logger.getLogger(getClass());
		this.gameDirectory = Paths.get(NAME).toAbsolutePath();
		this.dataDirectory = this.gameDirectory.resolve("data");
		this.settingsFile = this.gameDirectory.resolve("settings.json");
		this.pluginsDirectory = this.gameDirectory.resolve("plugins");
		this.gameRegistry = new GameRegistry();
		try {
			URL.setURLStreamHandlerFactory(new EmbedURLStreamHandlerFactory());
			URI uri = URI.create("embed:/");
			this.embedFileSystem = FileSystems.newFileSystem(uri, null);
		} catch (IOException ex) {
			throw new AssertionError(ex);
		}
		this.eventManager = new EventManager();
		this.pluginManager = new PluginManager(this);
	}
	
	/**
	 * @param framebuffer
	 * @return a new {@link DrawContext}
	 */
	public abstract DrawContext createContext(Framebuffer framebuffer);

	/**
	 * Starts the {@link GameLauncher}
	 * 
	 * @param args
	 * @throws GameException
	 */
	public final void start(String[] args) throws GameException {

		if (window != null) {
			return;
		}

		System.setOut(logger.createPrintStream(LogLevel.STDOUT));
		System.setErr(logger.createPrintStream(LogLevel.STDERR));

		logger.info("Starting " + NAME);

		StartCommandSettings scs = StartCommandSettings.parse(args);

		gameThread = new GameThread(this);

		for (Path externalPlugin : scs.externalPlugins) {
			this.pluginManager.loadPlugin(externalPlugin);
		}
		this.pluginManager.loadPlugins(pluginsDirectory);

		Files.createDirectories(gameDirectory);
		Files.createDirectories(dataDirectory);
		Files.createDirectories(pluginsDirectory);

		registerSettingInsertions();
		this.settings = new MainSettingSection(eventManager);
		if (!Files.exists(settingsFile)) {
			Files.createFile(settingsFile);
			settings.setDefaultValue();
			saveSettings();
		} else {
			byte[] bytes = Files.readAllBytes(settingsFile);
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
				Files.move(settingsFile,
						settingsFile.getParent()
								.resolve(String.format("backup-settings-%s.json",
										formatter.format(LocalDateTime.now()).replace(':', '-'))));
				saveSettings();
			}
		}

		gameThread.runLater(() -> {
			start0();
			if (gameRenderer.getRenderer() == null) {
				gameRenderer.setRenderer(new GuiRenderer(this));
			} else {
				logger.warn("Not using GuiRenderer: " + gameRenderer.getRenderer().getClass().getName());
			}
			guiManager.openGuiByClass(window, MainScreenGui.class);
			window.scheduleDrawAndWaitForFrame();
			getEventManager().post(new LauncherInitializedEvent(this));
		});
		gameThread.start();

	}

	/**
	 * Handles an error. May cause the {@link Game} or {@link GameLauncher} to crash
	 * 
	 * @param throwable
	 */
	public void handleError(Throwable throwable) {
		throwable.printStackTrace();
	}

	protected void registerSettingInsertions() {
	}

	/**
	 * @return the current tick of the {@link GameLauncher}
	 */
	public int getCurrentTick() {
		return gameThread.getCurrentTick();
	}

	/**
	 * Stops the {@link GameLauncher}
	 * 
	 * @throws GameException
	 */
	public void stop() throws GameException {
		try {
			gameThread.exit().get();
			this.pluginManager.unloadPlugins();
			this.embedFileSystem.close();
		} catch (InterruptedException | ExecutionException | IOException ex) {
			throw new GameException(ex);
		}
	}

	protected abstract void tick() throws GameException;

	protected abstract void start0() throws GameException;

	protected void setGuiManager(GuiManager guiManager) {
		this.guiManager = guiManager;
	}

	protected void setKeybindManager(KeybindManager keybindManager) {
		this.keybindManager = keybindManager;
	}

	/**
	 * @return the {@link KeybindManager}
	 */
	public KeybindManager getKeybindManager() {
		return keybindManager;
	}

	/**
	 * @return the {@link GuiManager}
	 */
	public GuiManager getGuiManager() {
		return guiManager;
	}

	protected void setOperatingSystem(OperatingSystem operatingSystem) {
		this.operatingSystem = operatingSystem;
	}

	/**
	 * @return the {@link OperatingSystem}
	 */
	public OperatingSystem getOperatingSystem() {
		return operatingSystem;
	}

	/**
	 * @return the {@link GameRegistry}
	 */
	public GameRegistry getGameRegistry() {
		return gameRegistry;
	}

	/**
	 * @return the {@link PluginManager}
	 */
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

	/**
	 * @return the {@link GlyphProvider}
	 */
	public GlyphProvider getGlyphProvider() {
		return glyphProvider;
	}

	protected void setShaderLoader(ShaderLoader shaderLoader) {
		this.shaderLoader = shaderLoader;
	}

	/**
	 * @return the {@link ShaderLoader}
	 */
	public ShaderLoader getShaderLoader() {
		return shaderLoader;
	}

	/**
	 * @return the current {@link Game}
	 */
	public Game getCurrentGame() {
		return currentGame;
	}

	/**
	 * Sets the current {@link Game}
	 * 
	 * @param currentGame
	 */
	public void setCurrentGame(Game currentGame) {
		this.currentGame = currentGame;
	}

	/**
	 * Sets the current {@link GlyphProvider}
	 * 
	 * @param glyphProvider
	 */
	public void setGlyphProvider(GlyphProvider glyphProvider) {
		this.glyphProvider = glyphProvider;
	}

	/**
	 * @return the {@link Camera}
	 */
	public Camera getCamera() {
		return camera;
	}

	/**
	 * Sets the {@link Camera}
	 * 
	 * @param camera
	 */
	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	/**
	 * @return the {@link EventManager}
	 */
	public EventManager getEventManager() {
		return eventManager;
	}

	/**
	 * @return the {@link EmbedFileSystem}
	 */
	public FileSystem getEmbedFileSystem() {
		return embedFileSystem;
	}

	/**
	 * @return the {@link Logger}
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * @return the {@link ModelLoader}
	 */
	public ModelLoader getModelLoader() {
		return modelLoader;
	}

	/**
	 * @return the {@link ResourceLoader}
	 */
	public ResourceLoader getResourceLoader() {
		return resourceLoader;
	}

	/**
	 * @return if the {@link GameLauncher} is in debug mode
	 */
	public boolean isDebugMode() {
		return debugMode;
	}

	/**
	 * Sets the {@link GameLauncher}'s debug mode
	 * 
	 * @param debugMode
	 */
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

	/**
	 * @return the data directory
	 */
	public Path getDataDirectory() {
		return dataDirectory;
	}

	/**
	 * @return the game directory
	 */
	public Path getGameDirectory() {
		return gameDirectory;
	}

	/**
	 * @return the plugins directory
	 */
	public Path getPluginsDirectory() {
		return pluginsDirectory;
	}

	/**
	 * @return the {@link GameThread}
	 */
	public GameThread getGameThread() {
		return gameThread;
	}

	/**
	 * @return the {@link GameRenderer}
	 */
	public GameRenderer getGameRenderer() {
		return gameRenderer;
	}

	/**
	 * Saves the current settings
	 * 
	 * @throws GameException
	 */
	public void saveSettings() throws GameException {
		Files.write(settingsFile, settingsGson.toJson(settings.serialize()).getBytes(StandardCharsets.UTF_8));
	}
}
