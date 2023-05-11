package gamelauncher.engine.gui;

import de.dasbabypixel.annotations.Api;
import de.dasbabypixel.api.property.BooleanValue;
import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.GameThread;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.function.GameConsumer;
import gamelauncher.engine.util.keybind.KeybindEvent;

import java.util.Collection;

/**
 * @author DasBabyPixel
 */
@Api
public interface Gui {

    /**
     * Checks if the given mouse position is inside the given rectangle
     *
     * @param x      the x position
     * @param y      the y position
     * @param width  the width
     * @param height the height
     * @param mouseX the mouse x position
     * @param mouseY the mouse y position
     * @return true if the mouse is inside the given rectangle
     */
    @Api static boolean hovering(float x, float y, float width, float height, float mouseX, float mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    /**
     * @return the width property of the gui
     */
    @Api NumberValue widthProperty();

    /**
     * @return the height property of the gui
     */
    @Api NumberValue heightProperty();

    /**
     * @return the x position property of the gui
     */
    @Api NumberValue xProperty();

    /**
     * @return the y position property of the gui
     */
    @Api NumberValue yProperty();

    @Api BooleanValue hovering();

    @Api NumberValue visibleXProperty();

    @Api NumberValue visibleYProperty();

    @Api NumberValue visibleWidthProperty();

    @Api NumberValue visibleHeightProperty();

    /**
     * @return the focused property of the gui
     */
    @Api BooleanValue focusedProperty();

    @Api <T extends KeybindEvent> void registerKeybindHandler(Class<T> clazz, GameConsumer<T> eventConsumer);

    @Api <T extends KeybindEvent> void unregisterKeybindHandler(Class<T> clazz, GameConsumer<T> eventConsumer);

    @Api <T extends KeybindEvent> Collection<GameConsumer<? super T>> keybindHandlers(Class<T> clazz);

    /**
     * Called when the contents of the window should be initialized
     *
     * @param framebuffer the framebuffer to initialize
     * @throws GameException an exception
     */
    @Api void init(Framebuffer framebuffer) throws GameException;

    /**
     * Called when this {@link Gui} is rendered
     *
     * @param framebuffer the framebuffer to render to
     * @param mouseX      the mouse x position
     * @param mouseY      the mouse y position
     * @param partialTick the partial tick
     * @throws GameException an exception
     */
    @Api void render(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick) throws GameException;

    /**
     * Called when the contents of this {@link Gui} should be cleaned up
     *
     * @param framebuffer the framebuffer to clean up
     * @throws GameException an exception
     */
    @Api void cleanup(Framebuffer framebuffer) throws GameException;

    /**
     * Called when this {@link Gui} is closed
     *
     * @throws GameException an exception
     */
    @Api void onClose() throws GameException;

    /**
     * Called when this {@link Gui} is opened
     *
     * @throws GameException an exception
     */
    @Api void onOpen() throws GameException;

    /**
     * Called when this {@link Gui} is focused
     *
     * @throws GameException an exception
     */
    @Api void focus() throws GameException;

    /**
     * Called when this {@link Gui} is unfocused
     *
     * @throws GameException an exception
     */
    @Api void unfocus() throws GameException;

    /**
     * Called when this {@link Gui} is updated in the {@link GameThread}
     *
     * @throws GameException an exception
     */
    @Api void update() throws GameException;

    /**
     * Called when a {@link KeybindEvent} is handled by this {@link Gui}
     *
     * @param entry the KeybindEntry to handle
     * @throws GameException an exception
     */
    @Api void handle(KeybindEvent entry) throws GameException;

    /**
     * @return the {@link GameLauncher}
     */
    @Api GameLauncher launcher();

    /**
     * @return if this {@link Gui} has been initialized
     */
    @Api boolean initialized();

    /**
     * @return if this gui is focused
     */
    @Api default boolean focused() {
        return focusedProperty().value();
    }

    /**
     * Should be true if this {@link Gui} pauses the game when opened. For {@link Gui}s like
     * settings or the pause menu
     *
     * @return if this {@link Gui} pauses the game
     */
    @Api default boolean doesPauseGame() {
        return false;
    }

    /**
     * @return the width of this {@link Gui}
     */
    @Api default float width() {
        return widthProperty().floatValue();
    }

    /**
     * Sets the width of this {@link Gui}
     *
     * @param width the new width
     */
    @Api default void width(float width) {
        widthProperty().number(width);
    }

    /**
     * @return the height of this {@link Gui}
     */
    @Api default float height() {
        return heightProperty().floatValue();
    }

    /**
     * Sets the height of this {@link Gui}
     *
     * @param height the new height
     */
    @Api default void height(float height) {
        heightProperty().number(height);
    }

    /**
     * @return the x position of this {@link Gui}
     */
    @Api default float x() {
        return xProperty().floatValue();
    }

    /**
     * Sets the x position of this {@link Gui}
     *
     * @param x the new x position
     */
    @Api default void x(float x) {
        xProperty().number(x);
    }

    /**
     * @return the y position of this {@link Gui}
     */
    @Api default float y() {
        return yProperty().floatValue();
    }

    /**
     * Sets the y position of this {@link Gui}
     *
     * @param y the new y position
     */
    @Api default void y(float y) {
        yProperty().number(y);
    }

    /**
     * Checks if the given mouse position is inside this {@link Gui}
     *
     * @param mouseX the mouse x position
     * @param mouseY the mouse y position
     * @return true if the mouse is inside this {@link Gui}
     */
    @Api default boolean hovering(float mouseX, float mouseY) {
        return hovering(visibleXProperty().floatValue(), visibleYProperty().floatValue(), visibleWidthProperty().floatValue(), visibleHeightProperty().floatValue(), mouseX, mouseY);
    }

    /**
     * @return if the user may exit this gui
     */
    @Api default boolean mayExit() {
        return true;
    }

    /**
     * Sets the position of this {@link Gui}
     *
     * @param x the new x position
     * @param y the new y position
     * @return this Gui
     */
    @Api default Gui position(float x, float y) {
        this.x(x);
        this.y(y);
        return this;
    }

    /**
     * Sets the size of this {@link Gui}
     *
     * @param width  the new width
     * @param height the new height
     * @return this Gui
     */
    @Api default Gui size(float width, float height) {
        this.width(width);
        this.height(height);
        return this;
    }
}
