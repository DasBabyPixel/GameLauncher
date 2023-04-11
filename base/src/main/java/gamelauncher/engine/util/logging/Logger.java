package gamelauncher.engine.util.logging;

import gamelauncher.engine.GameLauncher;

import java.io.PrintStream;

/**
 * @author DasBabyPixel
 */
public abstract class Logger {

    /**
     * The System Streams ({@link System#in} and {@link System#out})
     */
    public static final SelectiveStream system = new SelectiveStream();

    /**
     * The {@link AsyncLogStream} for submitting to the {@link #system system
     * stream}
     */
    private static AsyncLogStream asyncLogStream;

    public static AsyncLogStream asyncLogStream() {
        return asyncLogStream;
    }

    /**
     * @param message
     */
    public void info(Object message) {
        this.log(LogLevel.INFO, message);
    }

    /**
     * @param message
     * @param args
     */
    public void infof(String message, Object... args) {
        this.logf(LogLevel.INFO, message, args);
    }

    /**
     * @param message
     */
    public void error(Object message) {
        this.log(LogLevel.ERROR, message);
    }

    /**
     * @param message
     * @param args
     */
    public void errorf(String message, Object... args) {
        this.logf(LogLevel.ERROR, message, args);
    }

    /**
     * @param message
     */
    public void debug(Object message) {
        this.log(LogLevel.DEBUG, message);
    }

    /**
     * @param message
     * @param args
     */
    public void debugf(String message, Object... args) {
        this.logf(LogLevel.DEBUG, message, args);
    }

    /**
     * @param message
     */
    public void warn(Object message) {
        this.log(LogLevel.WARN, message);
    }

    /**
     * @param message
     * @param args
     */
    public void warnf(String message, Object... args) {
        this.logf(LogLevel.WARN, message, args);
    }

    /**
     * @param level
     * @return if the {@link LogLevel} should be displayed
     */
    public abstract boolean shouldDisplay(LogLevel level);

    /**
     * @param level
     * @param message
     * @param args
     */
    public void logf(LogLevel level, String message, Object... args) {
        this.log0(level, new Formatted(message, args));
    }

    /**
     * @param level
     * @param message
     */
    public void log(LogLevel level, Object message) {
        this.log0(level, new Formatted("%s", message));
    }

    protected abstract void log0(LogLevel level, Formatted message);

    /**
     * @return the {@link PrintStream}
     */
    public abstract PrintStream createPrintStream();

    /**
     * @param level
     * @return the {@link PrintStream}
     */
    public abstract PrintStream createPrintStream(LogLevel level);

    /**
     * @return {@link Logger}
     */
    public static Logger logger() {
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
    public static Logger logger(Class<?> clazz) {
        return Logger.logger(clazz.getSimpleName());
    }

    /**
     * @param name
     * @return {@link Logger}
     */
    public static Logger logger(String name) {
        return new SimpleLogger(name, Logger.asyncLogStream);
    }

    public static class Initializer {
        public static void init(GameLauncher launcher) {
            Logger.system.addEntry(System.out, SelectiveStream.Output.OUT);
            Logger.system.addEntry(System.err, SelectiveStream.Output.ERR);
            asyncLogStream = new AsyncLogStream(launcher);
        }
    }

}
