package gamelauncher.engine.gui;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author DasBabyPixel
 */
public class GuiStack {

	private final Deque<Gui> deque = new ConcurrentLinkedDeque<>();

	/**
	 * Pushes a {@link Gui} to the stack
	 * @param gui
	 */
	public void pushGui(Gui gui) {
		deque.offer(gui);
	}

	/**
	 * Pops the last {@link Gui} of the stack
	 * 
	 * @return the last {@link Gui} of the stack
	 */
	public Gui popGui() {
		return deque.pollLast();
	}

	/**
	 * @return the last {@link Gui} of the stack
	 */
	public Gui peekGui() {
		return deque.peekLast();
	}

	/**
	 * @return the size of the stack
	 */
	public int size() {
		return deque.size();
	}
}
