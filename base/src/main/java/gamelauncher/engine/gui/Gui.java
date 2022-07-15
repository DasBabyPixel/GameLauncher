package gamelauncher.engine.gui;

import de.dasbabypixel.api.property.BooleanValue;
import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.GameThread;
import gamelauncher.engine.render.Window;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.KeybindEntry;

/**
 * @author DasBabyPixel
 */
public interface Gui {

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

	/**
	 * @return the focused property of the gui
	 */
	BooleanValue getFocusedProperty();

	/**
	 * Called when the contents of the window should be initialized
	 * 
	 * @param window
	 * @throws GameException
	 */
	void init(Window window) throws GameException;

	/**
	 * Called when this {@link Gui} is rendered
	 * 
	 * @param window
	 * @param mouseX
	 * @param mouseY
	 * @param partialTick
	 * @throws GameException
	 */
	void render(Window window, float mouseX, float mouseY, float partialTick) throws GameException;

	/**
	 * Called when the contents of this {@link Gui} should be cleaned up
	 * 
	 * @param window
	 * @throws GameException
	 */
	void cleanup(Window window) throws GameException;

	/**
	 * Called when this {@link Gui} is closed
	 * 
	 * @throws GameException
	 */
	void onClose() throws GameException;

	/**
	 * Called when this {@link Gui} is opened
	 * 
	 * @throws GameException
	 */
	void onOpen() throws GameException;

	/**
	 * Called when this {@link Gui} is focused
	 * 
	 * @throws GameException
	 */
	void focus() throws GameException;

	/**
	 * Called when this {@link Gui} is unfocused
	 * 
	 * @throws GameException
	 */
	void unfocus() throws GameException;

	/**
	 * Called when this {@link Gui} is updated in the {@link GameThread}
	 * 
	 * @throws GameException
	 */
	void update() throws GameException;

	/**
	 * Called when a {@link KeybindEntry} is handled by this {@link Gui}
	 * 
	 * @param entry
	 * @throws GameException
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
	 * Should be true if this {@link Gui} pauses the game when opened. For
	 * {@link Gui}s like settings or the pause menu
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
	 * @return the height of this {@link Gui}
	 */
	default float getHeight() {
		return getHeightProperty().floatValue();
	}

	/**
	 * Sets the width of this {@link Gui}
	 * 
	 * @param width
	 */
	default void setWidth(float width) {
		getWidthProperty().setNumber(width);
	}

	/**
	 * Sets the height of this {@link Gui}
	 * 
	 * @param height
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
	 * @return the y position of this {@link Gui}
	 */
	default float getY() {
		return getYProperty().floatValue();
	}

	/**
	 * Sets the x position of this {@link Gui}
	 * 
	 * @param x
	 */
	default void setX(float x) {
		getXProperty().setNumber(x);
	}

	/**
	 * Sets the y position of this {@link Gui}
	 * 
	 * @param y
	 */
	default void setY(float y) {
		getYProperty().setNumber(y);
	}

	/**
	 * Checks if the given mouse position is inside this {@link Gui}
	 * @param mouseX
	 * @param mouseY
	 * @return true if the mouse is inside this {@link Gui}
	 */
	default boolean isHovering(float mouseX, float mouseY) {
		return isHovering(getX(), getY(), getWidth(), getHeight(), mouseX, mouseY);
	}

	/**
	 * Sets the position of this {@link Gui}
	 * @param x
	 * @param y
	 * @return this Gui
	 */
	default Gui setPosition(float x, float y) {
		this.setX(x);
		this.setY(y);
		return this;
	}

	/**
	 * Sets the size of this {@link Gui}
	 * @param width
	 * @param height
	 * @return this Gui
	 */
	default Gui setSize(float width, float height) {
		this.setWidth(width);
		this.setHeight(height);
		return this;
	}

	/**
	 * Checks if the given mouse position is inside the given rectangle
	 * @param x 
	 * @param y 
	 * @param width 
	 * @param height 
	 * @param mouseX
	 * @param mouseY
	 * @return true if the mouse is inside the given rectangle
	 */
	static boolean isHovering(float x, float y, float width, float height, float mouseX, float mouseY) {
		return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
	}
}
