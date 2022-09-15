package gamelauncher.engine.util.i18n;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author DasBabyPixel
 */
public class LanguageManager {

	private final Collection<Language> languages = new CopyOnWriteArrayList<>();
	private final Collection<Language> languagesUnmodifiable = Collections.unmodifiableCollection(languages);

	/**
	 * @return an unmodifiable collection of all languages
	 */
	public Collection<Language> getLanguages() {
		return languagesUnmodifiable;
	}

}
