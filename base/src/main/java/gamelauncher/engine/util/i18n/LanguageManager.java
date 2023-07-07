package gamelauncher.engine.util.i18n;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.util.GameException;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author DasBabyPixel
 */
public class LanguageManager {

    private final GameLauncher launcher;
    private final List<Language> languages = new CopyOnWriteArrayList<>();
    private final List<Language> languagesUnmodifiable = Collections.unmodifiableList(this.languages);

    private volatile Language selectedLanguage;

    public LanguageManager(GameLauncher launcher) throws GameException {
        this.launcher = launcher;
        language(Locale.ENGLISH).load(launcher.assets().resolve("languages").resolve("gamelauncher").resolve("en.json"));
    }

    /**
     * @return an unmodifiable collection of all languages
     */
    @Api public List<Language> languages() {
        return this.languagesUnmodifiable;
    }

    @Api public Language selectedLanguage() {
        return selectedLanguage;
    }

    @Api public void selectedLanguage(Language selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
    }

    @Api public Language language(Locale locale) throws GameException {
        for (Language lang : languages) if (lang.locale().equals(locale)) return lang;
        return addLanguage(locale);
    }

    private Language addLanguage(Locale locale, Language... fallbacks) throws GameException {
        Language language = new Language(launcher, locale, fallbacks);
        languages.add(language);
        if (selectedLanguage == null) selectedLanguage = language;
        return language;
    }
}
