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
import gamelauncher.engine.data.GameDirectoryResolver;
import gamelauncher.engine.data.embed.EmbedFileSystemProvider;
import gamelauncher.engine.event.EventManager;
import gamelauncher.engine.event.events.LauncherInitializedEvent;
import gamelauncher.engine.event.events.game.TickEvent;
import gamelauncher.engine.game.Game;
import gamelauncher.engine.game.GameRegistry;
import gamelauncher.engine.gui.GuiConstructorTemplates;
import gamelauncher.engine.gui.GuiManager;
import gamelauncher.engine.gui.GuiRenderer;
import gamelauncher.engine.gui.guis.MainScreenGui;
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
import gamelauncher.engine.resource.ResourceLoader;
import gamelauncher.engine.resource.ResourceTracker;
import gamelauncher.engine.settings.MainSettingSection;
import gamelauncher.engine.settings.SettingSection;
import gamelauncher.engine.settings.StartCommandSettings;
import gamelauncher.engine.util.*;
import gamelauncher.engine.util.concurrent.ExecutorThreadHelper;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.function.GameRunnable;
import gamelauncher.engine.util.i18n.LanguageManager;
import gamelauncher.engine.util.image.ImageDecoder;
import gamelauncher.engine.util.keybind.KeybindManager;
import gamelauncher.engine.util.logging.AnsiProvider;
import gamelauncher.engine.util.logging.LogLevel;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.engine.util.logging.SelectiveStream;
import gamelauncher.engine.util.profiler.Profiler;
import gamelauncher.engine.util.service.ServiceProvider;
import gamelauncher.engine.util.service.ServiceReference;
import java8.util.function.Predicate;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.ProviderNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author DasBabyPixel
 */
@SuppressWarnings("NewApi")
public abstract class GameLauncher {

    /**
     * The max TPS in the {@link TickerThread}
     */
    public static final float MAX_TPS = 60F;
    private final Gson settingsGson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    private final long startedMillis = System.currentTimeMillis();
    private final ServiceProvider serviceProvider;
    private Logger logger;
    private Threads threads;
    private PluginManager pluginManager;
    private Profiler profiler;
    private GameRegistry gameRegistry;
    private Path gameDirectory;
    private Path dataDirectory;
    private Path settingsFile;
    private Path pluginsDirectory;
    private Path assets;
    private TickerThread gameThread;
    private Frame frame;
    private OperatingSystem operatingSystem;
    private SettingSection settings;
    private GameRenderer gameRenderer;
    private ModelIdRegistry modelIdRegistry;

    private Game currentGame;

    private boolean startupFailed = false;

    /**
     * Creates a new GameLauncher
     */
    public GameLauncher() {
        this.serviceProvider = new ServiceProvider();
    }

    public final void start(String[] args) throws GameException {
        Debug.printInformation();
        LauncherUtils.acquire(logger, gameDirectory);
        Logger.asyncLogStream().initialize();
        initializeFileSystem();

        if (ensureSuccessfulStart()) return;

        serviceProvider.register(ServiceReference.LANGUAGE_MANAGER, new LanguageManager(this));
        this.modelIdRegistry = new ModelIdRegistry();

        setupOutputStreams();

        this.logger.infof(new Key("starting"));
        this.logger.infof(new Key("os"), operatingSystem.osName());

        StartCommandSettings scs = StartCommandSettings.parse(args);

        this.gameThread = new TickerThread(this);

        GuiConstructorTemplates.init(this);

        loadPlugins(scs);

        initializeSettings();

        this.gameThread.submit(() -> {
            this.start0();
            guiManager().openGuiByClass(MainScreenGui.class);
            if (this.gameRenderer.renderer() != null && !(this.gameRenderer.renderer() instanceof GuiRenderer)) {
                this.logger.warn("Not using GuiRenderer: " + this.gameRenderer.renderer().getClass().getName());
            }
            this.frame.scheduleDrawWaitForFrame();
            this.eventManager().post(new LauncherInitializedEvent(this));
            long timeStarted = System.currentTimeMillis();

            logger.infof("Startup took %sms", timeStarted - this.startedMillis);
        }).exceptionally(t->{
            t.printStackTrace();
            return null;
        });
        this.gameThread.start();
    }

    public void stop() throws GameException {
        if (currentGame != null) currentGame.close();
        GameRunnable r = () -> {
            try {
                this.pluginManager.unloadPlugins();
                this.stop0();
                embedFileSystem().close();
                Threads.await(this.gameThread.cleanup());
                Threads.await(this.modelIdRegistry.cleanup());
                Threads.await(this.threads.cleanup());
                try {
                    LauncherUtils.release();
                } catch (Exception ex) {
                    logger.error(ex);
                }
                Threads.await(Logger.asyncLogStream().cleanup());
                ResourceTracker.exit();
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

    @Api public AnsiProvider ansi() {
        return AnsiProvider.Unsupported.instance();
    }

    @Api public boolean isOnOperatingSystem(Predicate<OperatingSystem> predicate) {
        return predicate.test(operatingSystem);
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
            Logger.system.setOutput(SelectiveStream.Output.ERR);
            PrintStream ps = new PrintStream(Logger.system, true);
            e.printStackTrace(ps);
            ps.flush();
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Logger.system.setOutput(SelectiveStream.Output.ERR);
            PrintStream ps = new PrintStream(Logger.system, true);
            e.printStackTrace(ps);
            ps.flush();
        }
        System.exit(-1);
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
        return serviceProvider.service(ServiceReference.TEXTURE_MANAGER);
    }

    /**
     * @return the {@link Threads Threads utility class} for this {@link GameLauncher}
     */
    @Api public Threads threads() {
        return this.threads;
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

    @Api public void keyboardVisible(boolean visible) throws GameException {
    }

    @Api public boolean keyboardVisible() throws GameException {
        return true;
    }

    @Api public EventManager eventManager() {
        return serviceProvider.service(ServiceReference.EVENT_MANAGER);
    }

    @Api public FileSystem embedFileSystem() {
        return serviceProvider.service(ServiceReference.EMBED_FILE_SYSTEM);
    }

    @Api public Logger logger() {
        return this.logger;
    }

    @Api public ModelLoader modelLoader() {
        return serviceProvider.service(ServiceReference.MODEL_LOADER);
    }

    @Api public ResourceLoader resourceLoader() {
        return serviceProvider.service(ServiceReference.RESOURCE_LOADER);
    }

    @Api public KeybindManager keybindManager() {
        return serviceProvider.service(ServiceReference.KEYBIND_MANAGER);
    }

    @Api public ModelIdRegistry modelIdRegistry() {
        return modelIdRegistry;
    }

    @Api public ImageDecoder imageDecoder() {
        return serviceProvider.service(ServiceReference.IMAGE_DECODER);
    }

    @Api public NetworkClient networkClient() {
        return serviceProvider.service(ServiceReference.NETWORK_CLIENT);
    }

    @Api public GuiManager guiManager() {
        return serviceProvider.service(ServiceReference.GUI_MANAGER);
    }

    @Api public OperatingSystem operatingSystem() {
        return this.operatingSystem;
    }

    @Api public GameRegistry gameRegistry() {
        return this.gameRegistry;
    }

    @Api public Path assets() {
        return assets;
    }

    @Api public PluginManager pluginManager() {
        return this.pluginManager;
    }

    @Api public LanguageManager languageManager() {
        return serviceProvider.service(ServiceReference.LANGUAGE_MANAGER);
    }

    @Api public GlyphProvider glyphProvider() {
        return serviceProvider.service(ServiceReference.GLYPH_PROVIDER);
    }

    @Api public Profiler profiler() {
        return this.profiler;
    }

    @Api public ShaderLoader shaderLoader() {
        return serviceProvider.service(ServiceReference.SHADER_LOADER);
    }

    @Api public ServiceProvider serviceProvider() {
        return serviceProvider;
    }

    @Api public Frame frame() {
        return frame;
    }

    @Api public Path dataDirectory() {
        return this.dataDirectory;
    }

    @Api public Path gameDirectory() {
        return this.gameDirectory;
    }

    @Api public ExecutorThreadHelper executorThreadHelper() {
        return serviceProvider.service(ServiceReference.EXECUTOR_THREAD_HELPER);
    }

    @Api public Path pluginsDirectory() {
        return this.pluginsDirectory;
    }

    @Api public FontFactory fontFactory() {
        return serviceProvider.service(ServiceReference.FONT_FACTORY);
    }

    /**
     * @return the {@link TickerThread}
     */
    @Api public TickerThread gameThread() {
        return this.gameThread;
    }

    /**
     * @return the {@link ContextProvider} to use for creating contexts.
     */
    @Api public ContextProvider contextProvider() {
        return serviceProvider.service(ServiceReference.CONTEXT_PROVIDER);
    }

    /**
     * @return the {@link GameRenderer}
     */
    @Api public GameRenderer renderer() {
        return this.gameRenderer;
    }

    public SettingSection settings() {
        return settings;
    }

    /**
     * Saves the current settings
     *
     * @throws GameException an exception
     */
    @Api public void saveSettings() throws GameException {
        Files.write(this.settingsFile, this.settingsGson.toJson(this.settings.serialize()).getBytes(Charsets.UTF_8));
    }

    protected void init() {
        try {
            loadOperatingSystem();
            this.gameDirectory(GameDirectoryResolver.resolve(this));
            Logger.Initializer.init(this);
            Logger.asyncLogStream().start();
            this.logger = Logger.logger(this.getClass());
            this.threads = new Threads();
            this.profiler = new Profiler();
            this.gameRegistry = new GameRegistry();

            serviceProvider.register(ServiceReference.EVENT_MANAGER, new EventManager(this));
            this.pluginManager = new PluginManager(this);
        } catch (Throwable t) {
            t.printStackTrace();
            try {
                if (Logger.asyncLogStream() != null) Logger.asyncLogStream().cleanup();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            System.exit(-1);
            throw new RuntimeException();
        }
    }

    protected void gameDirectory(Path path) {
        this.gameDirectory = path;
        this.dataDirectory = this.gameDirectory.resolve("data");
        this.settingsFile = this.gameDirectory.resolve("settings.json");
        this.pluginsDirectory = this.gameDirectory.resolve("plugins");
    }

    protected void loadCustomPlugins() {
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

    protected void registerSettingInsertions() {
    }

    protected void loadOperatingSystem() {
        String osName = System.getProperty("os.name");
        String vendor = System.getProperty("java.vendor");
        OperatingSystem os = null;
        if (osName == null) os = DefaultOperatingSystems.UNKNOWN;
        else if (osName.toLowerCase(Locale.ROOT).contains("win")) os = DefaultOperatingSystems.WINDOWS;
        if (os == null && "The Android Project".equals(vendor)) os = DefaultOperatingSystems.ANDROID;
        if (os == null) os = DefaultOperatingSystems.UNKNOWN;
        operatingSystem(os);
    }

    protected void operatingSystem(OperatingSystem operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    protected void frame(Frame frame) {
        this.frame = frame;
        this.frame.frameRenderer(this.gameRenderer);
    }

    protected void renderer(GameRenderer renderer) {
        this.gameRenderer = renderer;
        if (this.frame != null) {
            this.frame.frameRenderer(renderer);
        }
    }

    protected FileSystem createEmbedFileSystem() throws IOException {
        return EmbedFileSystemProvider.instance.newFileSystem((URI) null, null);
    }

    private void initializeSettings() throws GameException {
        this.registerSettingInsertions();
        this.settings = new MainSettingSection(eventManager());
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
                DateFormat format = SimpleDateFormat.getDateTimeInstance();
                Files.move(this.settingsFile, this.settingsFile.getParent().resolve(String.format("backup-settings-%s.json", format.format(new Date()).replace(':', '-'))));
                this.saveSettings();
            }
        }
    }

    private void initializeFileSystem() throws GameException {
        FileSystem embedFileSystem = serviceProvider.service(ServiceReference.EMBED_FILE_SYSTEM);
        if (embedFileSystem == null) {
            try {
                serviceProvider.register(ServiceReference.EMBED_FILE_SYSTEM, embedFileSystem = createEmbedFileSystem());
            } catch (ProviderNotFoundException ex) {
                this.logger.error(ex);
                this.startupFailed = true;
                return;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        this.assets = embedFileSystem.getPath("assets");
        Files.createDirectories(this.gameDirectory);
        Files.createDirectories(this.dataDirectory);
        Files.createDirectories(this.pluginsDirectory);
    }

    private boolean ensureSuccessfulStart() throws GameException {
        if (this.startupFailed) {
            logger.error("Failed to start");
            Threads.await(Logger.asyncLogStream().cleanup());
            Threads.await(this.threads.cleanup());
            System.exit(1);
            return true;
        }
        return false;
    }

    private void setupOutputStreams() {
        System.setOut(this.logger.createPrintStream(LogLevel.STDOUT));
        System.setErr(this.logger.createPrintStream(LogLevel.STDERR));
    }

    private void loadPlugins(StartCommandSettings scs) throws GameException {
        for (String internalPlugin : scs.internalPlugins) {
            this.pluginManager.loadPlugin(internalPlugin);
        }
        for (Path externalPlugin : scs.externalPlugins) {
            try {
                this.pluginManager.loadPlugin(externalPlugin);
            } catch (GameException exception) {
                logger.error(exception);
            }
        }
        this.pluginManager.loadPlugins(this.pluginsDirectory);
        loadCustomPlugins();
    }
}
