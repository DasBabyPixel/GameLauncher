package gamelauncher.engine.util.text.format;

import java.util.HashMap;
import java.util.Map;

public class DecorationMap {

	private final Map<TextDecoration, TextDecoration.State> map;

	public DecorationMap(Map<TextDecoration, TextDecoration.State> map) {
		this.map = map;
	}

	public DecorationMap with(TextDecoration decoration, TextDecoration.State state) {
		Map<TextDecoration, TextDecoration.State> map = new HashMap<>(this.map);
		map.put(decoration, state);
		return new DecorationMap(map);
	}

	public DecorationMap with(Map<TextDecoration, TextDecoration.State> decorations) {
		Map<TextDecoration, TextDecoration.State> map = new HashMap<>(this.map);
		map.putAll(decorations);
		return new DecorationMap(map);
	}

	public boolean contains(TextDecoration decoration) {
		return map.containsKey(decoration);
	}

	public TextDecoration.State getState(TextDecoration decoration) {
		return map.get(decoration);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}
}
