package gamelauncher.engine.event;

public interface Cancellable {

	boolean isCancelled();
	
	void setCancelled(boolean cancel);
	
}
