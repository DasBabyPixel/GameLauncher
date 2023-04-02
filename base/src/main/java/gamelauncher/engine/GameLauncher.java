package gamelauncher.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import gamelauncher.engine.event.EventManager;
import gamelauncher.engine.event.events.LauncherInitializedEvent;
import gamelauncher.engine.event.events.game.TickEvent;
import gamelauncher.engine.game.Game;
import gamelauncher.engine.game.GameRegistry;
import gamelauncher.engine.gui.GuiManager;
import gamelauncher.engine.gui.GuiRenderer;
import gamelauncher.engine.io.Files;
import gamelauncher.engine.io.embed.EmbedFileSystem;
import gamelauncher.engine.io.embed.EmbedFileSystemProvider;
import gamelauncher.engine.io.embed.url.EmbedURLStreamHandlerFactory;
import gamelauncher.engine.network.NetworkClient;
import gamelauncher.engine.plugin.PluginManager;
import gamelauncher.engine.render.ContextProvider;
import gamelauncher.engine.render.Frame;
import gamelauncher.engine.render.GameRenderer;
import gamelauncher.engine.render.font.FontFactory;
import gamelauncher.engine.render.font.GlyphProvider;
import gamelauncher.engine.render.model.ModelLoader;
import gamelauncher.engine.render.shader.ShaderLoader;
import gamelauncher.engine.render.texture.TextureManager;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.resource.ResourceLoader;
import gamelauncher.engine.settings.MainSettingSection;
import gamelauncher.engine.settings.SettingSection;
import gamelauncher.engine.settings.StartCommandSettings;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.OperatingSystem;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.function.GameRunnable;
import gamelauncher.engine.util.keybind.KeybindManager;
import gamelauncher.engine.util.logging.LogLevel;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.engine.util.profiler.Profiler;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;

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
	public static final float MAX_TPS = 60F;

	private final EventManager eventManager;

	private final Logger logger;
	private final Path gameDirectory;
	private final Path dataDirectory;
	private final Path settingsFile;
	private final Path pluginsDirectory;
	private final Threads threads;
	private final PluginManager pluginManager;
	private final Profiler profiler;
	private final Gson settingsGson = new GsonBuilder().setPrettyPrinting().create();
	private final GameRegistry gameRegistry;
	private GameThread gameThread;
	private Frame frame;
	private FileSystem embedFileSystem;
	private OperatingSystem operatingSystem;
	private SettingSection settings;
	private GameRenderer gameRenderer;
	private ModelLoader modelLoader;
	private GlyphProvider glyphProvider;
	private ShaderLoader shaderLoader;
	private GuiManager guiManager;
	private KeybindManager keybindManager;
	private TextureManager textureManager;
	private FontFactory fontFactory;
	private NetworkClient networkClient;
	private ResourceLoader resourceLoader;
	private boolean debugMode = false;
	private ContextProvider contextProvider;

	private Game currentGame;

	private boolean startupFailed = false;

	/**
	 * Creates a new GameLauncher
	 */
	public GameLauncher() {
		AnsiConsole.systemInstall();
		Logger.asyncLogStream.start();
		this.logger = Logger.logger(this.getClass());
		this.threads = new Threads();
		this.gameDirectory = Paths.get(GameLauncher.NAME).toAbsolutePath();
		this.dataDirectory = this.gameDirectory.resolve("data");
		this.settingsFile = this.gameDirectory.resolve("settings.json");
		this.pluginsDirectory = this.gameDirectory.resolve("plugins");
		this.profiler = new Profiler();
		this.gameRegistry = new GameRegistry();
		try {
			URL.setURLStreamHandlerFactory(new EmbedURLStreamHandlerFactory());
			URI uri = URI.create("embed:/");
			//			this.embedFileSystem = FileSystems.newFileSystem(uri, null);
			this.embedFileSystem = EmbedFileSystemProvider.instance.newFileSystem(uri, null);
		} catch (ProviderNotFoundException ex) {
			this.logger.error(ex);
			this.startupFailed = true;
		} catch (IOException ex) {
			throw new AssertionError(ex);
		}
		this.eventManager = new EventManager(this);
		this.pluginManager = new PluginManager(this);
	}

	protected void contextProvider(ContextProvider contextProvider) {
		this.contextProvider = contextProvider;
	}

	public final void start(String[] args) throws GameException {

		if (this.frame != null) {
			return;
		}
		if (this.startupFailed) {

			try {
				if (this.embedFileSystem != null)
					this.embedFileSystem.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			Logger.asyncLogStream.cleanup();
			this.threads.cleanup();
			return;
		}

		System.setOut(this.logger.createPrintStream(LogLevel.STDOUT));
		System.setErr(this.logger.createPrintStream(LogLevel.STDERR));

		this.logger.info("Starting " + GameLauncher.NAME);

		StartCommandSettings scs = StartCommandSettings.parse(args);

		this.gameThread = new GameThread(this);

		Files.createDirectories(this.gameDirectory);
		Files.createDirectories(this.dataDirectory);
		Files.createDirectories(this.pluginsDirectory);

		for (Path externalPlugin : scs.externalPlugins) {
			try {
				this.pluginManager.loadPlugin(externalPlugin);
			} catch (GameException exception) {
				logger.error(exception);
			}
		}
		this.pluginManager.loadPlugins(this.pluginsDirectory);

		this.registerSettingInsertions();
		this.settings = new MainSettingSection(this.eventManager);
		if (!Files.exists(this.settingsFile)) {
			Files.createFile(this.settingsFile);
			this.settings.setDefaultValue();
			this.saveSettings();
		} else {
			byte[] bytes = Files.readAllBytes(this.settingsFile);
			String json = new String(bytes, StandardCharsets.UTF_8);
			JsonElement element = this.settingsGson.fromJson(json, JsonElement.class);
			this.settings.deserialize(element);
			JsonElement serialized = this.settingsGson.toJsonTree(this.settings.serialize());
			if (!serialized.equals(element)) {
				this.logger()
						.warnf("Unexpected change in settings.json. Creating backup and replacing file.");
				DateTimeFormatter formatter =
						new DateTimeFormatterBuilder().appendValue(ChronoField.YEAR, 4, 10,
										SignStyle.EXCEEDS_PAD).appendLiteral('-')
								.appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-')
								.appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('_')
								.appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral('-')
								.appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-')
								.appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();
				Files.move(this.settingsFile, this.settingsFile.getParent().resolve(
						String.format("backup-settings-%s.json",
								formatter.format(LocalDateTime.now()).replace(':', '-'))));
				this.saveSettings();
			}
		}

		this.gameThread.runLater(() -> {
			this.start0();
			if (this.gameRenderer.renderer() != null
					&& !(this.gameRenderer.renderer() instanceof GuiRenderer)) {
				this.logger.warn("Not using GuiRenderer: " + this.gameRenderer.renderer().getClass()
						.getName());
			}
			//			window.scheduleDrawAndWaitForFrame();
			// TODO: Gotta render the frame twice in beginning, dunno why
			this.frame.scheduleDrawWaitForFrame();
			this.eventManager().post(new LauncherInitializedEvent(this));
		});
		this.gameThread.start();
	}

	public void stop() throws GameException {
		GameRunnable r = () -> {
			try {
				Threads.waitFor(this.gameThread.exit());
				this.guiManager.cleanup();
				this.stop0();
				this.keybindManager.cleanup();
				this.resourceLoader.cleanup();
				this.pluginManager.unloadPlugins();
				this.threads.cleanup();
				this.embedFileSystem.close();
				Logger.asyncLogStream.cleanup();
				AbstractGameResource.exit();
				AnsiConsole.systemUninstall();
			} catch (IOException ex) {
				throw new GameException(ex);
			}
		};
		new Thread(r.toRunnable(), "ExitThread").start();
	}

	protected final void tick() throws GameException {
		this.eventManager().post(new TickEvent.Game());
		this.guiManager().updateGuis();
		this.tick0();
	}

	protected abstract void tick0() throws GameException;

	protected abstract void start0() throws GameException;

	protected abstract void stop0() throws GameException;

	/**
	 * Handles an error. May cause the {@link Game} or {@link GameLauncher} to crash
	 *
	 * @param throwable a throwable
	 */
	public void handleError(Throwable throwable) {
		this.logger.error(throwable);
	}

	protected void registerSettingInsertions() {
	}

	/**
	 * @return the current tick of the {@link GameLauncher}
	 */
	public int currentTick() {
		return this.gameThread.currentTick();
	}

	/**
	 * @return the {@link TextureManager}
	 */
	public TextureManager textureManager() {
		return this.textureManager;
	}

	protected void textureManager(TextureManager textureManager) {
		this.textureManager = textureManager;
	}

	/**
	 * @return the {@link Threads Threads utility class} for this {@link GameLauncher}
	 */
	public Threads threads() {
		return this.threads;
	}

	/**
	 * @return the {@link KeybindManager}
	 */
	public KeybindManager keybindManager() {
		return this.keybindManager;
	}

	protected void keybindManager(KeybindManager keybindManager) {
		this.keybindManager = keybindManager;
	}

	/**
	 * @return the {@link NetworkClient}
	 */
	public NetworkClient networkClient() {
		return this.networkClient;
	}

	protected void networkClient(NetworkClient networkClient) {
		this.networkClient = networkClient;
	}

	/**
	 * @return the {@link GuiManager}
	 */
	public GuiManager guiManager() {
		return this.guiManager;
	}

	protected void guiManager(GuiManager guiManager) {
		this.guiManager = guiManager;
	}

	/**
	 * @return the {@link OperatingSystem}
	 */
	public OperatingSystem operatingSystem() {
		return this.operatingSystem;
	}

	protected void operatingSystem(OperatingSystem operatingSystem) {
		this.operatingSystem = operatingSystem;
	}

	/**
	 * @return the {@link GameRegistry}
	 */
	public GameRegistry gameRegistry() {
		return this.gameRegistry;
	}

	/**
	 * @return the {@link PluginManager}
	 */
	public PluginManager pluginManager() {
		return this.pluginManager;
	}

	/**
	 * @return the {@link GlyphProvider}
	 */
	public GlyphProvider glyphProvider() {
		return this.glyphProvider;
	}

	/**
	 * Sets the current {@link GlyphProvider}
	 *
	 * @param glyphProvider
	 */
	public void glyphProvider(GlyphProvider glyphProvider) {
		this.glyphProvider = glyphProvider;
	}

	/**
	 * @return the {@link Profiler}
	 */
	public Profiler profiler() {
		return this.profiler;
	}

	/**
	 * @return the {@link ShaderLoader}
	 */
	public ShaderLoader shaderLoader() {
		return this.shaderLoader;
	}

	protected void shaderLoader(ShaderLoader shaderLoader) {
		this.shaderLoader = shaderLoader;
	}

	/**
	 * @return the current {@link Game}
	 */
	public Game currentGame() {
		return this.currentGame;
	}

	/**
	 * Sets the current {@link Game}
	 *
	 * @param currentGame
	 */
	public void currentGame(Game currentGame) {
		this.currentGame = currentGame;
	}

	/**
	 * @return the {@link EventManager}
	 */
	public EventManager eventManager() {
		return this.eventManager;
	}

	/**
	 * @return the {@link EmbedFileSystem}
	 */
	public FileSystem embedFileSystem() {
		return this.embedFileSystem;
	}

	/**
	 * @return the {@link Logger}
	 */
	public Logger logger() {
		return this.logger;
	}

	/**
	 * @return the {@link ModelLoader}
	 */
	public ModelLoader modelLoader() {
		return this.modelLoader;
	}

	protected void modelLoader(ModelLoader loader) {
		this.modelLoader = loader;
	}

	/**
	 * @return the {@link ResourceLoader}
	 */
	public ResourceLoader resourceLoader() {
		return this.resourceLoader;
	}

	protected void resourceLoader(ResourceLoader loader) {
		this.resourceLoader = loader;
		loader.set();
	}

	/**
	 * @return if the {@link GameLauncher} is in debug mode
	 */
	public boolean debugMode() {
		return this.debugMode;
	}

	/**
	 * Sets the {@link GameLauncher}'s debug mode
	 *
	 * @param debugMode
	 */
	public void debugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	protected void frame(Frame frame) {
		this.frame = frame;
		this.frame.frameRenderer(this.gameRenderer);
	}

	/**
	 * @return the data directory
	 */
	public Path dataDirectory() {
		return this.dataDirectory;
	}

	/**
	 * @return the game directory
	 */
	public Path gameDirectory() {
		return this.gameDirectory;
	}

	/**
	 * @return the plugins directory
	 */
	public Path pluginsDirectory() {
		return this.pluginsDirectory;
	}

	/**
	 * @return the {@link FontFactory}
	 */
	public FontFactory fontFactory() {
		return this.fontFactory;
	}

	protected void fontFactory(FontFactory fontFactory) {
		this.fontFactory = fontFactory;
	}

	/**
	 * @return the {@link GameThread}
	 */
	public GameThread gameThread() {
		return this.gameThread;
	}

	/**
	 * @return the {@link ContextProvider} to use for creating contexts.
	 */
	public ContextProvider contextProvider() {
		return this.contextProvider;
	}

	/**
	 * @return the {@link GameRenderer}
	 */
	public GameRenderer gameRenderer() {
		return this.gameRenderer;
	}

	protected void gameRenderer(GameRenderer renderer) {
		this.gameRenderer = renderer;
		if (this.frame != null) {
			this.frame.frameRenderer(renderer);
		}
	}

	/**
	 * Saves the current settings
	 *
	 * @throws GameException an exception
	 */
	public void saveSettings() throws GameException {
		Files.write(this.settingsFile, this.settingsGson.toJson(this.settings.serialize())
				.getBytes(StandardCharsets.UTF_8));
	}

}
