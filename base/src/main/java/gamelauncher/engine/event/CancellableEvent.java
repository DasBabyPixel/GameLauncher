package gamelauncher.engine.event;

public abstract class CancellableEvent extends Event implements Cancellable {

	private boolean cancelled = false;

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}
}
