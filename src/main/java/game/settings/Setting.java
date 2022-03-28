package game.settings;

public interface Setting<T> {

	T getValue();
	
	void setValue(T value);
	
	
	
}
