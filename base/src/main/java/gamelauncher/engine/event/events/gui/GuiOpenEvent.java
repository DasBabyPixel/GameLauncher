package gamelauncher.engine.event.events.gui;

import gamelauncher.engine.event.Event;
import gamelauncher.engine.gui.Gui;

public class GuiOpenEvent extends Event {
	private Gui gui;

	public GuiOpenEvent(Gui gui) {
		this.gui = gui;
	}

	public Gui gui() {
		return gui;
	}

	public void gui(Gui gui) {
		this.gui = gui;
	}
}
