package gamelauncher.engine.util.logging;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.util.Key;
import gamelauncher.engine.util.i18n.Message;

import java.io.PrintStream;

/**
 * @author DasBabyPixel
 */
@Api
public abstract class Logger {

    /**
     * The System Streams ({@link System#in} and {@link System#out})
     */
    @Api public static final SelectiveStream system = new SelectiveStream();

    /**
     * The {@link AsyncLogStream} for submitting to the {@link #system system
     * stream}
     */
    private static AsyncLogStream asyncLogStream;

    @Api public static AsyncLogStream asyncLogStream() {
        return asyncLogStream;
    }

    /**
     * @return {@link Logger}
     */
    @Api public static Logger logger() {
        StackTraceElement[] st = Thread.currentThread().getStackTrace();
        StackTraceElement caller = null;
        String cname = Logger.class.getName();
        boolean next = false;
        for (StackTraceElement t : st) {
            if (t.getClassName().equals(cname)) {
                next = true;
            } else if (next) {
                next = false;
                caller = t;
            }
        }
        assert caller != null;
        int index = caller.getClassName().lastIndexOf('.');
        return Logger.logger(caller.getClassName().substring(index == -1 ? 0 : index + 1));
    }

    /**
     * * @param clazz
     * * @return {@link Logger}
     */
    @Api public static Logger logger(Class<?> clazz) {
        return Logger.logger(clazz.getSimpleName());
    }

    /**
     * @return {@link Logger}
     */
    @Api public static Logger logger(String name) {
        return new SimpleLogger(name);
    }

    @Api public void info(Object message) {
        this.log(LogLevel.INFO, message);
    }

    @Api public void infof(String message, Object... args) {
        this.logf(LogLevel.INFO, message, args);
    }

    @Api public void infof(Key message, Object... args) {
        this.logf(LogLevel.INFO, message, args);
    }

    @Api public void infof(Message message, Object... args) {
        this.logf(LogLevel.INFO, message, args);
    }

    @Api public void error(Object message) {
        this.log(LogLevel.ERROR, message);
    }

    @Api public void errorf(String message, Object... args) {
        this.logf(LogLevel.ERROR, message, args);
    }

    @Api public void errorf(Key message, Object... args) {
        this.logf(LogLevel.ERROR, message, args);
    }

    @Api public void errorf(Message message, Object... args) {
        this.logf(LogLevel.ERROR, message, args);
    }

    @Api public void debug(Object message) {
        this.log(LogLevel.DEBUG, message);
    }

    @Api public void debugf(String message, Object... args) {
        this.logf(LogLevel.DEBUG, message, args);
    }

    @Api public void debugf(Key message, Object... args) {
        this.logf(LogLevel.DEBUG, message, args);
    }

    @Api public void debugf(Message message, Object... args) {
        this.logf(LogLevel.DEBUG, message, args);
    }

    @Api public void warn(Object message) {
        this.log(LogLevel.WARN, message);
    }

    @Api public void warnf(String message, Object... args) {
        this.logf(LogLevel.WARN, message, args);
    }

    @Api public void warnf(Key message, Object... args) {
        this.logf(LogLevel.WARN, message, args);
    }

    @Api public void warnf(Message message, Object... args) {
        this.logf(LogLevel.WARN, message, args);
    }

    /**
     * @return if the {@link LogLevel} should be displayed
     */
    @Api public abstract boolean shouldDisplay(LogLevel level);

    @Api public void logf(LogLevel level, String message, Object... args) {
        this.log0(level, new Formatted(message, args));
    }

    @Api public void logf(LogLevel level, Message message, Object... args) {
        logf(level, message.key(), args);
    }

    @Api public void logf(LogLevel level, Key key, Object... args) {
        log0(level, new LogMessage(key, args));
    }

    @Api public void log(LogLevel level, Object message) {
        this.log0(level, new Formatted("%s", message));
    }

    @Api protected abstract void log0(LogLevel level, Object message);

    /**
     * @return the {@link PrintStream}
     */
    @Api public abstract PrintStream createPrintStream();

    /**
     * @return the {@link PrintStream}
     */
    public abstract PrintStream createPrintStream(LogLevel level, LoggerFlags... flags);

    @Api
    public enum LoggerFlags {
        DONT_PRINT_SOURCE
    }

    public static class Initializer {
        public static void init(GameLauncher launcher) {
            Logger.system.addEntry(System.out, SelectiveStream.Output.OUT);
            Logger.system.addEntry(System.err, SelectiveStream.Output.ERR);
            asyncLogStream = new AsyncLogStream(launcher);
        }
    }
}
