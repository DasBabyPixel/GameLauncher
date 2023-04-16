package gamelauncher.engine.plugin;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.i18n.Message;
import gamelauncher.engine.util.i18n.SimpleMessage;
import gamelauncher.engine.util.logging.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;

/**
 * A plugin must extend this.
 * <p>
 * A plugin must also have the {@link GameException} annotation
 *
 * @author DasBabyPixel
 */
@Api
public abstract class Plugin {

    private final String name;
    private final Logger logger;
    private GameLauncher launcher;
    private final Message displayName;

    /**
     * @param name the plugins name (identity)
     */
    @Api
    public Plugin(String name) {
        this.logger = Logger.logger(this.getClass());
        this.name = name;
        this.displayName = new SimpleMessage(this, name);
    }

    @Api
    public Message displayName() {
        return displayName;
    }

    /**
     * Called when the plugin is enabled
     *
     * @throws GameException an exception
     */
    @Api
    public void onEnable() throws GameException {
    }

    /**
     * Called when the plugin is disabled
     *
     * @throws GameException an exception
     */
    @Api
    public void onDisable() throws GameException {
    }

    /**
     * Sets the {@link GameLauncher}
     *
     * @param launcher sets the launcher
     */
    @ApiStatus.Internal
    public void launcher(GameLauncher launcher) {
        this.launcher = launcher;
    }

    /**
     * @return the {@link GameLauncher}
     */
    @Api
    public GameLauncher launcher() {
        return this.launcher;
    }

    /**
     * @return the {@link Logger} of this plugin
     */
    @Api
    public Logger logger() {
        return this.logger;
    }

    /**
     * @return the name of this plugin
     */
    @Api
    public String name() {
        return this.name;
    }

    /**
     * Add this to every plugin
     *
     * @author DasBabyPixel
     */
    @Api
    @Retention(RetentionPolicy.RUNTIME)
    public @interface GamePlugin {

    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        Plugin other = (Plugin) obj;
        return Objects.equals(this.name, other.name);
    }
}
