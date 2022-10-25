package gamelauncher.engine;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.ProviderNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;

import org.fusesource.jansi.AnsiConsole;

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
import gamelauncher.engine.io.embed.url.EmbedURLStreamHandlerFactory;
import gamelauncher.engine.network.NetworkClient;
import gamelauncher.engine.plugin.PluginManager;
import gamelauncher.engine.render.ContextProvider;
import gamelauncher.engine.render.DrawContext;
import gamelauncher.engine.render.Frame;
import gamelauncher.engine.render.Framebuffer;
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

	private GameThread gameThread;

	private Frame frame;

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

	private TextureManager textureManager;

	private FontFactory fontFactory;

	private NetworkClient networkClient;

	private Threads threads;

	private PluginManager pluginManager;

	private ResourceLoader resourceLoader;

	private Profiler profiler;

	private boolean debugMode = false;

	private Gson settingsGson = new GsonBuilder().setPrettyPrinting().create();

	private GameRegistry gameRegistry;

	private ContextProvider contextProvider;

	private Game currentGame;

	private boolean startupFailed = false;

	/**
	 * Creates a new GameLauncher
	 */
	public GameLauncher() {
		AnsiConsole.systemInstall();
		Logger.asyncLogStream.start();
		this.logger = Logger.getLogger(this.getClass());
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
			this.embedFileSystem = FileSystems.newFileSystem(uri, null);
		} catch (ProviderNotFoundException ex) {
			this.logger.error(ex);
			this.startupFailed = true;
		} catch (IOException ex) {
			throw new AssertionError(ex);
		}
		this.eventManager = new EventManager(this);
		this.contextProvider = new ContextProvider(this);
		this.pluginManager = new PluginManager(this);
	}

	/**
	 * @param framebuffer
	 * @return a new {@link DrawContext}
	 */
	@Deprecated
	public abstract DrawContext createContext(Framebuffer framebuffer);

	/**
	 * Starts the {@link GameLauncher}
	 * 
	 * @param args
	 * @throws GameException
	 */
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
			this.pluginManager.loadPlugin(externalPlugin);
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
				this.getLogger().warnf("Unexpected change in settings.json. Creating backup and replacing file.");
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
				Files.move(this.settingsFile,
						this.settingsFile.getParent()
								.resolve(String.format("backup-settings-%s.json",
										formatter.format(LocalDateTime.now()).replace(':', '-'))));
				this.saveSettings();
			}
		}

		this.gameThread.runLater(() -> {
			this.start0();
			if (this.gameRenderer.getRenderer() != null && !(this.gameRenderer.getRenderer() instanceof GuiRenderer)) {
				this.logger.warn("Not using GuiRenderer: " + this.gameRenderer.getRenderer().getClass().getName());
			}
//			window.scheduleDrawAndWaitForFrame(); // TODO: Gotta render the frame twice in beginning, dunny why
			this.frame.scheduleDrawWaitForFrame();
			this.getEventManager().post(new LauncherInitializedEvent(this));
		});
		this.gameThread.start();
	}

	/**
	 * Stops the {@link GameLauncher}
	 * 
	 * @throws GameException
	 */
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

	/**
	 * @throws GameException
	 */
	protected final void tick() throws GameException {
		this.getEventManager().post(new TickEvent.Game());
		this.getGuiManager().updateGuis();
		this.tick0();
	}
	
	protected abstract void tick0() throws GameException;

	protected abstract void start0() throws GameException;

	protected abstract void stop0() throws GameException;

	protected void setGuiManager(GuiManager guiManager) {
		this.guiManager = guiManager;
	}

	protected void setKeybindManager(KeybindManager keybindManager) {
		this.keybindManager = keybindManager;
	}

	protected void setTextureManager(TextureManager textureManager) {
		this.textureManager = textureManager;
	}

	/**
	 * Handles an error. May cause the {@link Game} or {@link GameLauncher} to crash
	 * 
	 * @param throwable
	 */
	public void handleError(Throwable throwable) {
		this.logger.error(throwable);
	}

	protected void registerSettingInsertions() {
	}

	/**
	 * @return the current tick of the {@link GameLauncher}
	 */
	public int getCurrentTick() {
		return this.gameThread.getCurrentTick();
	}

	/**
	 * @return the {@link TextureManager}
	 */
	public TextureManager getTextureManager() {
		return this.textureManager;
	}

	/**
	 * @return the {@link Threads Threads utility class} for this
	 *         {@link GameLauncher}
	 */
	public Threads getThreads() {
		return this.threads;
	}

	/**
	 * @return the {@link KeybindManager}
	 */
	public KeybindManager getKeybindManager() {
		return this.keybindManager;
	}

	protected void setNetworkClient(NetworkClient networkClient) {
		this.networkClient = networkClient;
	}

	/**
	 * @return the {@link NetworkClient}
	 */
	public NetworkClient getNetworkClient() {
		return this.networkClient;
	}

	/**
	 * @return the {@link GuiManager}
	 */
	public GuiManager getGuiManager() {
		return this.guiManager;
	}

	protected void setOperatingSystem(OperatingSystem operatingSystem) {
		this.operatingSystem = operatingSystem;
	}

	/**
	 * @return the {@link OperatingSystem}
	 */
	public OperatingSystem getOperatingSystem() {
		return this.operatingSystem;
	}

	/**
	 * @return the {@link GameRegistry}
	 */
	public GameRegistry getGameRegistry() {
		return this.gameRegistry;
	}

	/**
	 * @return the {@link PluginManager}
	 */
	public PluginManager getPluginManager() {
		return this.pluginManager;
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
		return this.glyphProvider;
	}

	protected void setShaderLoader(ShaderLoader shaderLoader) {
		this.shaderLoader = shaderLoader;
	}

	/**
	 * @return the {@link Profiler}
	 */
	public Profiler getProfiler() {
		return this.profiler;
	}

	/**
	 * @return the {@link ShaderLoader}
	 */
	public ShaderLoader getShaderLoader() {
		return this.shaderLoader;
	}

	/**
	 * @return the current {@link Game}
	 */
	public Game getCurrentGame() {
		return this.currentGame;
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
	 * @return the {@link EventManager}
	 */
	public EventManager getEventManager() {
		return this.eventManager;
	}

	/**
	 * @return the {@link EmbedFileSystem}
	 */
	public FileSystem getEmbedFileSystem() {
		return this.embedFileSystem;
	}

	/**
	 * @return the {@link Logger}
	 */
	public Logger getLogger() {
		return this.logger;
	}

	/**
	 * @return the {@link ModelLoader}
	 */
	public ModelLoader getModelLoader() {
		return this.modelLoader;
	}

	/**
	 * @return the {@link ResourceLoader}
	 */
	public ResourceLoader getResourceLoader() {
		return this.resourceLoader;
	}

	/**
	 * @return if the {@link GameLauncher} is in debug mode
	 */
	public boolean isDebugMode() {
		return this.debugMode;
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
		if (this.frame != null) {
			this.frame.frameRenderer(renderer);
		}
	}

	protected void setFrame(Frame frame) {
		this.frame = frame;
		this.frame.frameRenderer(this.gameRenderer);
	}

	/**
	 * @return the data directory
	 */
	public Path getDataDirectory() {
		return this.dataDirectory;
	}

	/**
	 * @return the game directory
	 */
	public Path getGameDirectory() {
		return this.gameDirectory;
	}

	/**
	 * @return the plugins directory
	 */
	public Path getPluginsDirectory() {
		return this.pluginsDirectory;
	}

	/**
	 * @return the {@link FontFactory}
	 */
	public FontFactory getFontFactory() {
		return this.fontFactory;
	}

	protected void setFontFactory(FontFactory fontFactory) {
		this.fontFactory = fontFactory;
	}

	/**
	 * @return the {@link GameThread}
	 */
	public GameThread getGameThread() {
		return this.gameThread;
	}

	/**
	 * @return the {@link ContextProvider} to use for creating contexts. This is
	 *         preferred over {@link #createContext(Framebuffer)}
	 */
	public ContextProvider getContextProvider() {
		return this.contextProvider;
	}

	/**
	 * @return the {@link GameRenderer}
	 */
	public GameRenderer getGameRenderer() {
		return this.gameRenderer;
	}

	/**
	 * Saves the current settings
	 * 
	 * @throws GameException
	 */
	public void saveSettings() throws GameException {
		Files.write(this.settingsFile, this.settingsGson.toJson(this.settings.serialize()).getBytes(StandardCharsets.UTF_8));
	}

}
