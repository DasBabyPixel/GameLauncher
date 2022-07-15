package gamelauncher.engine.gui;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author DasBabyPixel
 */
public class GuiStack {

	private final Deque<StackEntry> deque = new ConcurrentLinkedDeque<>();

	/**
	 * Pushes a {@link Gui} to the stack
	 * 
	 * @param gui
	 * @return the {@link StackEntry} for this gui
	 */
	public StackEntry pushGui(Gui gui) {
		StackEntry e = new StackEntry(gui);
		deque.offerLast(e);
		return e;
	}

	/**
	 * Pops the last {@link Gui} of the stack
	 * 
	 * @return the last {@link Gui} of the stack
	 */
	public StackEntry popGui() {
		return deque.pollLast();
	}

	/**
	 * @return the last {@link Gui} of the stack
	 */
	public StackEntry peekGui() {
		return deque.peekLast();
	}

	/**
	 * @return the size of the stack
	 */
	public int size() {
		return deque.size();
	}

	/**
	 * @author DasBabyPixel
	 */
	public static class StackEntry {

		/**
		 * The gui
		 */
		public final Gui gui;

		/**
		 * @param gui
		 */
		public StackEntry(Gui gui) {
			this.gui = gui;
		}
	}
}
