package gamelauncher.engine.event;

public interface Node {

	int priority();
	
	void invoke(Event event);
	
}
