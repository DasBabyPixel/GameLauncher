package gamelauncher.engine.util.i18n;

import gamelauncher.engine.plugin.Plugin;

public record SimpleMessage(Plugin plugin, String key) implements Message {

	@Override
	public String key() {
		return key;
	}

	@Override
	public String prefixModifier() {
		return plugin.name() + "_";
	}
}
