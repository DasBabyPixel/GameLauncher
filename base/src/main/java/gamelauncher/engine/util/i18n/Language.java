package gamelauncher.engine.util.i18n;

import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * @author DasBabyPixel
 */
public class Language {

	private final HashMap<String, String> map = new HashMap<>();
	private final Charset charset;

	private final Language[] fallbacks;

	Language(Charset charset, Language... fallbacks) {
		this.charset = charset;
		this.fallbacks = fallbacks;
	}
// TODO
	public String translate(Message message) {
		return null;
	}

	public Charset charset() {
		return charset;
	}

}
