package gamelauncher.engine.gui;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameFunction;
import gamelauncher.engine.util.function.GameSupplier;
import org.jetbrains.annotations.NotNull;

/**
 * @author DasBabyPixel
 */
@Api
public interface GuiManager extends GameResource {

    /**
     * Opens a {@link Gui}
     *
     * @param gui the gui to open, null to exit the current gui
     */
    @Api void openGui(@NotNull Gui gui) throws GameException;

    /**
     * @return the current gui for a window
     */
    @Api Gui currentGui() throws GameException;

    /**
     * Cleans up the guis for a framebuffer
     */
    @Override @Api void cleanup() throws GameException;

    /**
     * Opens a {@link Gui}
     */
    @Api default void openGuiByClass(GuiDistribution distribution, Class<? extends Gui> clazz) throws GameException {
        openGui(createGui(distribution, clazz));
    }

    @Api default void openGuiByClass(Class<? extends Gui> clazz) throws GameException {
        openGuiByClass(null, clazz);
    }

    @Api void redrawAll();

    @Api GuiDistribution preferredDistribution(Class<? extends Gui> clazz);

    @Api void preferredDistribution(Class<? extends Gui> clazz, GuiDistribution distribution);

    /**
     * @return the {@link GameLauncher}
     */
    @Api GameLauncher launcher();

    @Api void updateGuis() throws GameException;

    @Api default <T extends Gui> T createGui(Class<T> clazz) throws GameException {
        return createGui(null, clazz);
    }

    /**
     * Creates a Gui for the given {@link Class class}.
     *
     * @return the created {@link Gui}
     */
    @Api <T extends Gui> T createGui(GuiDistribution distribution, Class<T> clazz) throws GameException;

    /**
     * Registers a converter for {@link Gui}s. When a
     * {@link Gui} is created via {@link GuiManager#createGui(GuiDistribution, Class)},
     * this function will be called.
     */
    @Api <T extends Gui> void registerGuiConverter(Class<T> clazz, GameFunction<T, T> converter);

    /**
     * Registers a {@link Gui} creator. Used to create
     * {@link Gui}s via {@link GuiManager#createGui(GuiDistribution, Class)}
     */
    @Api <T extends Gui> void registerGuiCreator(GuiDistribution distribution, Class<T> clazz, GameSupplier<T> supplier);

    /**
     * This will look through the class and all its declared classes for information on how to create the wanted GUI.<br>
     * We will try to find a constructor from {@link GuiConstructorTemplates} for the GUI.<br>
     * Every implementation with a special constructor implementation must have a {@code public static final} {@link GuiConstructorTemplate} {@code TEMPLATE} declared which will be used to create the {@link Gui}<br>
     * A declared {@code TEMPLATE} will override the default {@link GuiConstructorTemplates}
     */
    @Api <T extends Gui> void registerGuiCreator(GuiDistribution distribution, Class<T> clazz);

    @Api <T extends Gui> void registerGuiCreator(GuiDistribution distribution, Class<T> clazz, GuiConstructorTemplate constructorTemplate);

    @Api <T extends Gui> void registerGuiCreator(GuiDistribution distribution, Class<T> clazz, Class<? extends T> implementationClass);

    @Api <T extends Gui> void registerGuiCreator(GuiDistribution distribution, Class<T> guiClass, Class<? extends T> implementationClass, GuiConstructorTemplate constructorTemplate);

}
