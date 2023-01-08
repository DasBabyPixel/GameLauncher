package gamelauncher.engine.event.events.gui;

import gamelauncher.engine.event.Event;
import gamelauncher.engine.gui.Gui;

public class RequestGuiExitEvent extends Event {
	private final Gui gui;
	private boolean mayExit = true;

	public RequestGuiExitEvent(Gui gui) {
		this.gui = gui;
	}

	public Gui gui() {
		return gui;
	}

	public boolean mayExit() {
		return mayExit;
	}

	public void mayExit(boolean mayExit) {
		this.mayExit = mayExit;
	}
}
