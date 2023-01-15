package gamelauncher.engine.util.i18n;

public interface Message {

	/**
	 * @return the key of this message to resolve the translation
	 */
	String key();

	/**
	 * @return the prefix modifier of this message. This will be put before the {@link #key()} to
	 * resolve the final key
	 */
	default String prefixModifier() {
		return "";
	}

}
