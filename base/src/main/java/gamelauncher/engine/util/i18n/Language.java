package gamelauncher.engine.util.i18n;

import java.util.Locale;

/**
 * @author DasBabyPixel
 */
public class Language {

	final Locale locale;

	final Language[] fallbacks;

	Language(Locale locale, Language[] fallbacks) {
		this.locale = locale;
		this.fallbacks = fallbacks;
	}

}
