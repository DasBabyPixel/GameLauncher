package gamelauncher.engine.util.logging;

import java.io.PrintStream;

/**
 * @author DasBabyPixel
 */
public class SimpleLogger extends Logger {

    private final String name;
    private int level = LogLevel.DEBUG.level();

    public SimpleLogger(String name) {
        this.name = name;
    }

    @Override
    public boolean shouldDisplay(LogLevel level) {
        return level.level() >= this.level;
    }

    @Override
    public void log0(LogLevel level, Formatted message) {
        if (this.shouldDisplay(level)) {
            Logger.asyncLogStream().offerLog(this, level, message);
        }
    }

    @Override
    public PrintStream createPrintStream() {
        return this.createPrintStream(LogLevel.STDOUT);
    }

    @Override
    public PrintStream createPrintStream(LogLevel level) {
        return new CallerPrintStream(level, Logger.asyncLogStream());
    }

    @Override
    public String toString() {
        return this.name;
    }

}
