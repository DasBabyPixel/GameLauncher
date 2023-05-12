/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.data.Files;
import gamelauncher.engine.data.embed.EmbedFileSystem;
import gamelauncher.engine.data.embed.EmbedFileSystemProvider;
import gamelauncher.engine.event.EventManager;
import gamelauncher.engine.event.events.LauncherInitializedEvent;
import gamelauncher.engine.event.events.game.TickEvent;
import gamelauncher.engine.game.Game;
import gamelauncher.engine.game.GameRegistry;
import gamelauncher.engine.gui.GuiConstructorTemplates;
import gamelauncher.engine.gui.GuiManager;
import gamelauncher.engine.gui.GuiRenderer;
import gamelauncher.engine.network.NetworkClient;
import gamelauncher.engine.plugin.PluginManager;
import gamelauncher.engine.render.ContextProvider;
import gamelauncher.engine.render.Frame;
import gamelauncher.engine.render.GameRenderer;
import gamelauncher.engine.render.ModelIdRegistry;
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
import gamelauncher.engine.util.Key;
import gamelauncher.engine.util.OperatingSystem;
import gamelauncher.engine.util.concurrent.ExecutorThreadHelper;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.function.GameRunnable;
import gamelauncher.engine.util.i18n.LanguageManager;
import gamelauncher.engine.util.keybind.KeybindManager;
import gamelauncher.engine.util.logging.AnsiProvider;
import gamelauncher.engine.util.logging.LogLevel;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.engine.util.profiler.Profiler;
import gamelauncher.engine.util.service.ServiceProvider;
import java8.util.function.Predicate;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private final Threads threads;
    private final PluginManager pluginManager;
    private final Profiler profiler;
    private final Gson settingsGson = new GsonBuilder().setPrettyPrinting().create();
    private final GameRegistry gameRegistry;
    private final ServiceProvider serviceProvider;
    private Path gameDirectory;
    private Path dataDirectory;
    private Path settingsFile;
    private Path pluginsDirectory;
    private Path assets;
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
    private LanguageManager languageManager;
    private ExecutorThreadHelper executorThreadHelper;
    private ResourceLoader resourceLoader;
    private boolean debugMode = false;
    private ContextProvider contextProvider;
    private ModelIdRegistry modelIdRegistry;

    private Game currentGame;
    private FileLock lock;
    private FileChannel fileChannel;

    private boolean startupFailed = false;

    /**
     * Creates a new GameLauncher
     */
    @SuppressWarnings("NewApi") public GameLauncher() {
        try {
            Logger.Initializer.init(this);
            Logger.asyncLogStream().start();
            this.logger = Logger.logger(this.getClass());
            this.serviceProvider = new ServiceProvider();
            this.threads = new Threads();
            this.gameDirectory(Paths.get(GameLauncher.NAME).toAbsolutePath());
            this.profiler = new Profiler();
            this.gameRegistry = new GameRegistry();

            this.eventManager = new EventManager(this);
            this.pluginManager = new PluginManager(this);
        } catch (Throwable t) {
            t.printStackTrace();
            try {
                Logger.asyncLogStream().cleanup();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            System.exit(-1);
            throw new RuntimeException();
        }
    }

    @Api public AnsiProvider ansi() {
        return new AnsiProvider.Unsupported();
    }

    @SuppressWarnings("NewApi") protected void gameDirectory(Path path) {
        this.gameDirectory = path;
        this.dataDirectory = this.gameDirectory.resolve("data");
        this.settingsFile = this.gameDirectory.resolve("settings.json");
        this.pluginsDirectory = this.gameDirectory.resolve("plugins");
    }

    @Api public boolean isOnOperatingSystem(Predicate<OperatingSystem> predicate) {
        return predicate.test(operatingSystem);
    }

    protected void contextProvider(ContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    @SuppressWarnings("NewApi") public final void start(String[] args) throws GameException {
        try {
            fileChannel = FileChannel.open(gameDirectory.resolve("lock"), StandardOpenOption.APPEND, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            lock = fileChannel.tryLock(0, Long.MAX_VALUE, false);
            if (lock == null) {
                fileChannel.close();
                logger.error("GameLauncher already running");
                Logger.asyncLogStream().cleanup();
                System.exit(0);
            }
        } catch (Exception ex) {
            logger.error(ex);
        }

        if (embedFileSystem == null) {
            try {
                this.embedFileSystem = EmbedFileSystemProvider.instance.newFileSystem((URI) null, null);
            } catch (ProviderNotFoundException ex) {
                this.logger.error(ex);
                this.startupFailed = true;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        this.assets = embedFileSystem.getPath("assets");

        if (this.startupFailed) {
            try {
                if (this.embedFileSystem != null) this.embedFileSystem.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            logger.error("Failed to start");
            Logger.asyncLogStream().cleanup();
            this.threads.cleanup();
            return;
        }
        this.languageManager = new LanguageManager(this);
        this.modelIdRegistry = new ModelIdRegistry();

        System.setOut(this.logger.createPrintStream(LogLevel.STDOUT));
        System.setErr(this.logger.createPrintStream(LogLevel.STDERR));

        this.logger.infof(new Key("starting"));
        this.logger.infof(new Key("os"), operatingSystem.osName());

        StartCommandSettings scs = StartCommandSettings.parse(args);

        this.gameThread = new GameThread(this);

        Files.createDirectories(this.gameDirectory);
        Files.createDirectories(this.dataDirectory);
        Files.createDirectories(this.pluginsDirectory);

        GuiConstructorTemplates.init(this);

        for (Path externalPlugin : scs.externalPlugins) {
            try {
                this.pluginManager.loadPlugin(externalPlugin);
            } catch (GameException exception) {
                logger.error(exception);
            }
        }
        this.pluginManager.loadPlugins(this.pluginsDirectory);
        loadCustomPlugins();

        this.registerSettingInsertions();
        this.settings = new MainSettingSection(this.eventManager);
        if (!Files.exists(this.settingsFile)) {
            Files.createFile(this.settingsFile);
            this.settings.setDefaultValue();
            this.saveSettings();
        } else {
            byte[] bytes = Files.readAllBytes(this.settingsFile);
            String json = new String(bytes, Charsets.UTF_8);
            JsonElement element = this.settingsGson.fromJson(json, JsonElement.class);
            this.settings.deserialize(element);
            JsonElement serialized = this.settingsGson.toJsonTree(this.settings.serialize());
            if (!serialized.equals(element)) {
                this.logger().warnf("Unexpected change in settings.json. Creating backup and replacing file.");
//                DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('_').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral('-').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-').appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();
                DateFormat format = SimpleDateFormat.getDateTimeInstance();
                Files.move(this.settingsFile, this.settingsFile.getParent().resolve(String.format("backup-settings-%s.json", format.format(new Date()).replace(':', '-'))));
                this.saveSettings();
            }
        }

        this.gameThread.runLater(() -> {
            this.start0();
            if (this.gameRenderer.renderer() != null && !(this.gameRenderer.renderer() instanceof GuiRenderer)) {
                this.logger.warn("Not using GuiRenderer: " + this.gameRenderer.renderer().getClass().getName());
            }
            //			window.scheduleDrawAndWaitForFrame();
            // TODO: Gotta render the frame twice in beginning, dunno why
            this.frame.scheduleDrawWaitForFrame();
            this.eventManager().post(new LauncherInitializedEvent(this));
        });
        this.gameThread.start();
    }

    protected void loadCustomPlugins() {
    }

    public void stop() throws GameException {
        GameRunnable r = () -> {
            try {
//                Threads.waitFor(frame.renderThread().submit(() -> gameRenderer.cleanup(frame)));
                this.guiManager.cleanup();
                Threads.waitFor(this.gameThread.exit());
                this.stop0();
                this.modelIdRegistry.cleanup();
                this.keybindManager.cleanup();
                this.resourceLoader.cleanup();
                this.pluginManager.unloadPlugins();
                this.threads.cleanup();
                this.embedFileSystem.close();
                try {
                    lock.release();
                    fileChannel.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                Logger.asyncLogStream().cleanup();
                AbstractGameResource.exit();
            } catch (Throwable ex) {
                logger.error(ex);
                try {
                    Logger.asyncLogStream().cleanup();
                } catch (Throwable ignored) {
                }
                System.exit(-1);
            }
        };
        new Thread(r.toRunnable(), "ExitThread").start();
    }

    protected final void tick() throws GameException {
        this.eventManager().post(new TickEvent.Game());
        this.frame().input().handleInput();
        this.guiManager().updateGuis();
        this.tick0();
    }

    protected void tick0() throws GameException {
    }

    protected void start0() throws GameException {
    }

    protected void stop0() throws GameException {
    }

    /**
     * Handles an error. May cause the {@link Game} or {@link GameLauncher} to crash
     *
     * @param throwable a throwable
     */
    @Api public void handleError(Throwable throwable) {
        this.logger.error(throwable);
        try {
            Logger.asyncLogStream().cleanup();
        } catch (GameException e) {
            throw new RuntimeException(e);
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.exit(-1);
    }

    protected void registerSettingInsertions() {
    }

    /**
     * @return the current tick of the {@link GameLauncher}
     */
    @Api public int currentTick() {
        return this.gameThread.currentTick();
    }

    /**
     * @return the {@link TextureManager}
     */
    @Api public TextureManager textureManager() {
        return this.textureManager;
    }

    protected void textureManager(TextureManager textureManager) {
        this.textureManager = textureManager;
    }

    /**
     * @return the {@link Threads Threads utility class} for this {@link GameLauncher}
     */
    @Api public Threads threads() {
        return this.threads;
    }

    /**
     * @return the {@link KeybindManager}
     */
    @Api public KeybindManager keybindManager() {
        return this.keybindManager;
    }

    protected void embedFileSystem(FileSystem embedFileSystem) {
        this.embedFileSystem = embedFileSystem;
    }

    protected void keybindManager(KeybindManager keybindManager) {
        this.keybindManager = keybindManager;
    }

    @Api public ModelIdRegistry modelIdRegistry() {
        return modelIdRegistry;
    }

    /**
     * @return the {@link NetworkClient}
     */
    @Api public NetworkClient networkClient() {
        return this.networkClient;
    }

    @Api protected void networkClient(NetworkClient networkClient) {
        this.networkClient = networkClient;
    }

    /**
     * @return the {@link GuiManager}
     */
    @Api public GuiManager guiManager() {
        return this.guiManager;
    }

    protected void guiManager(GuiManager guiManager) {
        this.guiManager = guiManager;
    }

    /**
     * @return the {@link OperatingSystem}
     */
    @Api public OperatingSystem operatingSystem() {
        return this.operatingSystem;
    }

    protected void operatingSystem(OperatingSystem operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    /**
     * @return the {@link GameRegistry}
     */
    @Api public GameRegistry gameRegistry() {
        return this.gameRegistry;
    }

    @Api public Path assets() {
        return assets;
    }

    /**
     * @return the {@link PluginManager}
     */
    @Api public PluginManager pluginManager() {
        return this.pluginManager;
    }

    @Api public LanguageManager languageManager() {
        return languageManager;
    }

    /**
     * @return the {@link GlyphProvider}
     */
    @Api public GlyphProvider glyphProvider() {
        return this.glyphProvider;
    }

    /**
     * Sets the current {@link GlyphProvider}
     */
    protected void glyphProvider(GlyphProvider glyphProvider) {
        this.glyphProvider = glyphProvider;
    }

    /**
     * @return the {@link Profiler}
     */
    @Api public Profiler profiler() {
        return this.profiler;
    }

    /**
     * @return the {@link ShaderLoader}
     */
    @Api public ShaderLoader shaderLoader() {
        return this.shaderLoader;
    }

    protected void shaderLoader(ShaderLoader shaderLoader) {
        this.shaderLoader = shaderLoader;
    }

    /**
     * @return the current {@link Game}
     */
    @Api public Game currentGame() {
        return this.currentGame;
    }

    /**
     * Sets the current {@link Game}
     */
    @Api public void currentGame(Game currentGame) {
        this.currentGame = currentGame;
    }

    /**
     * @return the {@link EventManager}
     */
    @Api public EventManager eventManager() {
        return this.eventManager;
    }

    /**
     * @return the {@link EmbedFileSystem}
     */
    @Api public FileSystem embedFileSystem() {
        return this.embedFileSystem;
    }

    /**
     * @return the {@link Logger}
     */
    @Api public Logger logger() {
        return this.logger;
    }

    /**
     * @return the {@link ModelLoader}
     */
    @Api public ModelLoader modelLoader() {
        return this.modelLoader;
    }

    protected void modelLoader(ModelLoader loader) {
        this.modelLoader = loader;
    }

    /**
     * @return the {@link ResourceLoader}
     */
    @Api public ResourceLoader resourceLoader() {
        return this.resourceLoader;
    }

    protected void resourceLoader(ResourceLoader loader) {
        this.resourceLoader = loader;
        loader.set();
    }

    @Api public void keyboardVisible(boolean visible) throws GameException {
    }

    @Api public boolean keyboardVisible() throws GameException {
        return true;
    }

    @Api public ServiceProvider serviceProvider() {
        return serviceProvider;
    }

    /**
     * @return if the {@link GameLauncher} is in debug mode
     */
    @Api public boolean debugMode() {
        return this.debugMode;
    }

    /**
     * Sets the {@link GameLauncher}'s debug mode
     *
     * @param debugMode debug
     */
    @Api public void debugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    protected void frame(Frame frame) {
        this.frame = frame;
        this.frame.frameRenderer(this.gameRenderer);
    }

    @Api public Frame frame() {
        return frame;
    }

    /**
     * @return the data directory
     */
    @Api public Path dataDirectory() {
        return this.dataDirectory;
    }

    /**
     * @return the game directory
     */
    @Api public Path gameDirectory() {
        return this.gameDirectory;
    }

    @Api public ExecutorThreadHelper executorThreadHelper() {
        return executorThreadHelper;
    }

    protected void executorThreadHelper(ExecutorThreadHelper executorThreadHelper) {
        this.executorThreadHelper = executorThreadHelper;
    }

    /**
     * @return the plugins directory
     */
    @Api public Path pluginsDirectory() {
        return this.pluginsDirectory;
    }

    /**
     * @return the {@link FontFactory}
     */
    @Api public FontFactory fontFactory() {
        return this.fontFactory;
    }

    protected void fontFactory(FontFactory fontFactory) {
        this.fontFactory = fontFactory;
    }

    /**
     * @return the {@link GameThread}
     */
    @Api public GameThread gameThread() {
        return this.gameThread;
    }

    /**
     * @return the {@link ContextProvider} to use for creating contexts.
     */
    @Api public ContextProvider contextProvider() {
        return this.contextProvider;
    }

    /**
     * @return the {@link GameRenderer}
     */
    @Api public GameRenderer gameRenderer() {
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
    @Api public void saveSettings() throws GameException {
        Files.write(this.settingsFile, this.settingsGson.toJson(this.settings.serialize()).getBytes(Charsets.UTF_8));
    }

}
