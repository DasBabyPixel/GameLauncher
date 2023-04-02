package gamelauncher.engine.util.i18n;

import gamelauncher.engine.plugin.Plugin;

public class SimpleMessage implements Message {

	private final Plugin plugin;
	private final String key;

	public SimpleMessage(Plugin plugin, String key) {
		this.plugin = plugin;
		this.key = key;
	}

	public String key() {
		return key;
	}

	public Plugin plugin() {
		return plugin;
	}

	@Override
	public String prefixModifier() {
		return plugin.name() + "_";
	}
}
