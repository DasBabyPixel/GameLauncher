package gamelauncher.engine.gui;

import gamelauncher.engine.util.collections.Collections;

import java.util.Deque;

/**
 * @author DasBabyPixel
 */
public class GuiStack {

    private final Deque<StackEntry> deque = Collections.newConcurrentDeque();

    /**
     * Pushes a {@link Gui} to the stack
     *
     * @param gui the gui to push
     * @return the {@link StackEntry} for this gui
     */
    public StackEntry pushGui(Gui gui) {
        StackEntry e = new StackEntry(gui);
        deque.offer(e);
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
         * @param gui the gui for this entry
         */
        public StackEntry(Gui gui) {
            this.gui = gui;
        }
    }
}
