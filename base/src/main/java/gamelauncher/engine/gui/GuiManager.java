package gamelauncher.engine.gui;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameFunction;
import gamelauncher.engine.util.function.GameSupplier;

/**
 * @author DasBabyPixel
 */
@Api
public interface GuiManager extends GameResource {

    /**
     * Opens a {@link Gui} for a window. Use null to exit the current gui.
     */
    @Api
    void openGui(Framebuffer framebuffer, Gui gui) throws GameException;

    /**
     * @return the current gui for a window
     */
    @Api
    Gui currentGui(Framebuffer framebuffer) throws GameException;

    @Api
    void cleanup(Framebuffer framebuffer) throws GameException;

    /**
     * Opens a {@link Gui} for a window.
     */
    @Api
    default void openGuiByClass(Framebuffer framebuffer, Class<? extends Gui> clazz) throws GameException {
        openGui(framebuffer, createGui(clazz));
    }

    @Api
    void redrawAll();

    /**
     * @return the {@link GameLauncher}
     */
    @Api
    GameLauncher launcher();

    @Api
    void updateGuis() throws GameException;

    /**
     * Creates a Gui for the given {@link Class class}.
     *
     * @return the created {@link Gui}
     */
    @Api
    <T extends Gui> T createGui(Class<T> clazz) throws GameException;

    /**
     * Registers a converter for {@link Gui}s. When a
     * {@link Gui} is created via {@link GuiManager#createGui(Class)},
     * this function will be called.
     */
    @Api
    <T extends Gui> void registerGuiConverter(Class<T> clazz, GameFunction<T, T> converter);

    /**
     * Registers a {@link Gui} creator. Used to create
     * {@link Gui}s via {@link GuiManager#createGui(Class)}
     */
    @Api
    <T extends Gui> void registerGuiCreator(Class<T> clazz, GameSupplier<T> supplier);

    /**
     * This will look through the class and all its declared classes for information on how to create the wanted GUI.<br>
     * We will try to find a constructor from {@link GuiConstructorTemplates} for the GUI.<br>
     * Every implementation with a special constructor implementation must have a {@code public static final} {@link GuiConstructorTemplate} {@code TEMPLATE} declared which will be used to create the {@link Gui}<br>
     * A declared {@code TEMPLATE} will override the default {@link GuiConstructorTemplates}
     */
    @Api
    <T extends Gui> void registerGuiCreator(Class<T> clazz);

    @Api
    <T extends Gui> void registerGuiCreator(Class<T> clazz, GuiConstructorTemplate constructorTemplate);

    @Api
    <T extends Gui> void registerGuiCreator(Class<T> clazz, Class<? extends T> implementationClass);

    @Api
    <T extends Gui> void registerGuiCreator(Class<T> guiClass, Class<? extends T> implementationClass, GuiConstructorTemplate constructorTemplate);

}
