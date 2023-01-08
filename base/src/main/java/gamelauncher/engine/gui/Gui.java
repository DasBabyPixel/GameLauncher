package gamelauncher.engine.gui;

import de.dasbabypixel.api.property.BooleanValue;
import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.GameThread;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.KeybindEntry;

/**
 * @author DasBabyPixel
 */
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
	 *
	 * @return true if the mouse is inside the given rectangle
	 */
	static boolean isHovering(float x, float y, float width, float height, float mouseX,
			float mouseY) {
		return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
	}

	/**
	 * @return the width property of the gui
	 */
	NumberValue getWidthProperty();

	/**
	 * @return the height property of the gui
	 */
	NumberValue getHeightProperty();

	/**
	 * @return the x position property of the gui
	 */
	NumberValue getXProperty();

	/**
	 * @return the y position property of the gui
	 */
	NumberValue getYProperty();

	NumberValue getVisibleXProperty();

	NumberValue getVisibleYProperty();

	NumberValue getVisibleWidthProperty();

	NumberValue getVisibleHeightProperty();

	/**
	 * @return the focused property of the gui
	 */
	BooleanValue getFocusedProperty();

	/**
	 * Called when the contents of the window should be initialized
	 *
	 * @param framebuffer the framebuffer to initialize
	 *
	 * @throws GameException an exception
	 */
	void init(Framebuffer framebuffer) throws GameException;

	/**
	 * Called when this {@link Gui} is rendered
	 *
	 * @param framebuffer the framebuffer to render to
	 * @param mouseX      the mouse x position
	 * @param mouseY      the mouse y position
	 * @param partialTick the partial tick
	 *
	 * @throws GameException an exception
	 */
	void render(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick)
			throws GameException;

	/**
	 * Called when the contents of this {@link Gui} should be cleaned up
	 *
	 * @param framebuffer the framebuffer to clean up
	 *
	 * @throws GameException an exception
	 */
	void cleanup(Framebuffer framebuffer) throws GameException;

	/**
	 * Called when this {@link Gui} is closed
	 *
	 * @throws GameException an exception
	 */
	void onClose() throws GameException;

	/**
	 * Called when this {@link Gui} is opened
	 *
	 * @throws GameException an exception
	 */
	void onOpen() throws GameException;

	/**
	 * Called when this {@link Gui} is focused
	 *
	 * @throws GameException an exception
	 */
	void focus() throws GameException;

	/**
	 * Called when this {@link Gui} is unfocused
	 *
	 * @throws GameException an exception
	 */
	void unfocus() throws GameException;

	/**
	 * Called when this {@link Gui} is updated in the {@link GameThread}
	 *
	 * @throws GameException an exception
	 */
	void update() throws GameException;

	/**
	 * Called when a {@link KeybindEntry} is handled by this {@link Gui}
	 *
	 * @param entry the KeybindEntry to handle
	 *
	 * @throws GameException an exception
	 */
	void handle(KeybindEntry entry) throws GameException;

	/**
	 * @return the {@link GameLauncher}
	 */
	GameLauncher getLauncher();

	/**
	 * @return if this {@link Gui} has been initialized
	 */
	boolean isInitialized();

	/**
	 * @return if this gui is focused
	 */
	default boolean isFocused() {
		return getFocusedProperty().getValue();
	}

	/**
	 * Should be true if this {@link Gui} pauses the game when opened. For {@link Gui}s like
	 * settings or the pause menu
	 *
	 * @return if this {@link Gui} pauses the game
	 */
	default boolean doesPauseGame() {
		return false;
	}

	/**
	 * @return the width of this {@link Gui}
	 */
	default float getWidth() {
		return getWidthProperty().floatValue();
	}

	/**
	 * Sets the width of this {@link Gui}
	 *
	 * @param width the new width
	 */
	default void setWidth(float width) {
		getWidthProperty().setNumber(width);
	}

	/**
	 * @return the height of this {@link Gui}
	 */
	default float getHeight() {
		return getHeightProperty().floatValue();
	}

	/**
	 * Sets the height of this {@link Gui}
	 *
	 * @param height the new height
	 */
	default void setHeight(float height) {
		getHeightProperty().setNumber(height);
	}

	/**
	 * @return the x position of this {@link Gui}
	 */
	default float getX() {
		return getXProperty().floatValue();
	}

	/**
	 * Sets the x position of this {@link Gui}
	 *
	 * @param x the new x position
	 */
	default void setX(float x) {
		getXProperty().setNumber(x);
	}

	/**
	 * @return the y position of this {@link Gui}
	 */
	default float getY() {
		return getYProperty().floatValue();
	}

	/**
	 * Sets the y position of this {@link Gui}
	 *
	 * @param y the new y position
	 */
	default void setY(float y) {
		getYProperty().setNumber(y);
	}

	/**
	 * Checks if the given mouse position is inside this {@link Gui}
	 *
	 * @param mouseX the mouse x position
	 * @param mouseY the mouse y position
	 *
	 * @return true if the mouse is inside this {@link Gui}
	 */
	default boolean isHovering(float mouseX, float mouseY) {
		return isHovering(getVisibleXProperty().floatValue(), getVisibleYProperty().floatValue(),
				getVisibleWidthProperty().floatValue(), getVisibleHeightProperty().floatValue(),
				mouseX, mouseY);
	}

	/**
	 * @return if the user may exit this gui
	 */
	default boolean mayExit() {
		return true;
	}

	/**
	 * Sets the position of this {@link Gui}
	 *
	 * @param x the new x position
	 * @param y the new y position
	 *
	 * @return this Gui
	 */
	default Gui setPosition(float x, float y) {
		this.setX(x);
		this.setY(y);
		return this;
	}

	/**
	 * Sets the size of this {@link Gui}
	 *
	 * @param width  the new width
	 * @param height the new height
	 *
	 * @return this Gui
	 */
	default Gui setSize(float width, float height) {
		this.setWidth(width);
		this.setHeight(height);
		return this;
	}
}
