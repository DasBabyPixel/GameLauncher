/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util.logging;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.util.Color;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.Key;
import gamelauncher.engine.util.concurrent.AbstractQueueSubmissionThread;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.i18n.Message;
import gamelauncher.engine.util.logging.SelectiveStream.Output;
import java8.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.ApiStatus;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Collection;
import java.util.IllegalFormatException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author DasBabyPixel
 */
public class AsyncLogStream extends AbstractQueueSubmissionThread<AsyncLogStream.LogEntry<?>> {

    private static final Pattern throwablePattern = Pattern.compile("\\(.+\\.java:[0-9]+\\)");
    private static final LogColor fgThread = new LogColor(new Color(200, 200, 200, 255));
    private static final LogColor fgLogger = new LogColor(new Color(0, 100, 255, 255));
    private static final LogColor fgTime = new LogColor(new Color(70, 255, 70, 255));
    final PrintStream out;
    final SelectiveStream system;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private final AnsiProvider ansi;
    private final LogColor C100 = new LogColor(100, 100, 100);
    private final Object asyncLock = new Object();
    private volatile boolean async;
    //			new DateTimeFormatterBuilder().appendPattern("HH:mm:ss.SSS").toFormatter();

    public AsyncLogStream(GameLauncher launcher) {
        super(launcher);
        this.ansi = launcher == null ? new AnsiProvider.Unsupported() : launcher.ansi();
        this.system = Logger.system;
        this.setName("AsyncLogStream");
        this.out = new PrintStream(this.system, true);
    }

    @ApiStatus.Experimental public boolean async() {
        return async;
    }

    @ApiStatus.Experimental public void async(boolean async) {
        this.async = async;
    }

    public CompletableFuture<Void> offerLog(Logger logger, LogLevel level, Object message) {
        if (!this.exit) {
            if (async) return this.submit(new LogEntry<>(logger, Thread.currentThread(), message, level));
            synchronized (asyncLock) {
                this.log(new LogEntry<>(logger, Thread.currentThread(), message, level));
            }
            return CompletableFuture.completedFuture(null);
        }
        try {
            Threads.await(exitFuture());
        } catch (GameException e) {
            throw new RuntimeException(e);
        }
        this.log(new LogEntry<>(logger, Thread.currentThread(), message, level));
        return CompletableFuture.completedFuture(null);
    }

    public void offerCalled(LogLevel level, StackTraceElement caller, Object message) {
        if (!this.exit) {
            if (async) this.submit(new LogEntry<>(null, Thread.currentThread(), message, level, caller));
            else synchronized (asyncLock) {
                log(new LogEntry<>(null, Thread.currentThread(), message, level, caller));
            }
            return;
        }
        this.log(new LogEntry<>(null, Thread.currentThread(), message, level, caller));
    }

    @Override public CompletableFuture<Void> cleanup0() throws GameException {
        return this.exit();
    }

    @SuppressWarnings("RedundantThrows") @Override protected void handleElement(LogEntry<?> element) throws GameException {
        this.log(element);
    }

    void setSystemLevel(LogLevel level) {
        if (level.level() > LogLevel.ERROR.level()) {
            this.system.setOutput(Output.ERR);
        } else {
            this.system.setOutput(Output.OUT);
        }
    }

    void log(LogEntry<?> entry) {
        Object message = entry.object;
        if (message == null) {
            message = "null";
        }
        if (message instanceof LogMessage) {
            LogMessage m = (LogMessage) message;
            String format = launcher().languageManager().selectedLanguage().translate(m.key(), m.args());
            Formatted f = new Formatted(format, m.args());
            log(entry.level, entry.time, entry.caller, entry.thread, entry.logger, f);
        } else if (message instanceof Formatted) {
            Formatted f = (Formatted) message;
            this.log(entry.level, entry.time, entry.caller, entry.thread, entry.logger, f);
        } else {
            this.logString(entry.level, entry.time, entry.caller, entry.thread, entry.logger, message.toString());
        }
    }

    void printThread(Thread thread) {
        this.out.printf(this.ansi0(AsyncLogStream.fgThread), thread.getName());
    }

    void printLoggerName(Logger logger) {
        this.out.printf(this.ansi0(AsyncLogStream.fgLogger), logger.toString());
    }

    void printTime(TemporalAccessor time) {
        this.out.printf(this.ansi0(AsyncLogStream.fgTime), this.formatter.format(time));
    }

    void printLevel(LogLevel level) {
        this.out.printf(this.ansi0(level.displayColor()), level.name());
    }

    private void log(LogLevel level, TemporalAccessor time, StackTraceElement caller, Thread thread, Logger logger, Formatted f) {
        Object[] data = new Object[f.getObjects().length];
        LogColor resetTo = level.textColor();
        LogColor[] logColors = new LogColor[data.length];
        String[] logColorReplacements = new String[data.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = f.getObjects()[i];
            if (data[i] instanceof Throwable) {
                Throwable t = (Throwable) data[i];
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                t.printStackTrace(pw);
                pw.flush();
                pw.close();
                String s = sw.getBuffer().toString();
                Matcher m = AsyncLogStream.throwablePattern.matcher(s);
                StringBuilder sb = new StringBuilder();
                int last = 0;
                while (m.find()) {
                    sb.append(s, last, m.start());
                    last = m.end();
                    sb.append(ansi.reset()).append(m.group());
                }
                data[i] = sb.toString();
            } else if (data[i] instanceof Collection<?>) {
                Collection<?> c = (Collection<?>) data[i];
                StringBuilder sb = new StringBuilder();
                sb.append(c.getClass().getName()).append("<?> (Size: ").append(c.size()).append(")");
                for (Object o : c) {
                    sb.append("\n - ").append(o);
                }
                data[i] = sb.toString();
            } else if (data[i].getClass().isArray()) {
                Object[] a = (Object[]) data[i];
                StringBuilder sb = new StringBuilder();
                sb.append(a.getClass().getComponentType().getName()).append('[').append(a.length).append(']');
                for (int j = 0; j < a.length; j++) {
                    sb.append("\n - ").append(j).append(": ").append(a[j]);
                }
                data[i] = sb.toString();
            } else if (data[i] instanceof LogColor) {
                LogColor c = (LogColor) data[i];
                if (c.color() == null) {
                    // Reset
                    c = resetTo;
                }
                logColors[i] = c;
                String r = String.format("%%lcr%n");
                logColorReplacements[i] = r;
                data[i] = r;
            } else if (data[i] instanceof Message) {
                data[i] = launcher().languageManager().selectedLanguage().translate((Message) data[i]);
            } else if (data[i] instanceof Key) {
                data[i] = launcher().languageManager().selectedLanguage().translate((Key) data[i]);
            }
        }
        String print;
        try {
            print = String.format(f.getFormat(), data);
        } catch (IllegalFormatException ex) {
            logString(level, time, caller, thread, logger, "Wrong format: \"" + f.getFormat() + "\", Arguments: " + Arrays.toString(data));
            return;
        }
        String[] lines = this.lines(print);

        int lci = 0;
        LogColor lastColor = resetTo;
        for (int i = 0; i < lines.length; i++) {
            lines[i] = this.ansi(lastColor) + lines[i];
            w1:
            while (true) {
                if (logColors.length <= lci) {
                    break;
                }
                while (logColors[lci] == null) {
                    lci++;
                    continue w1;
                }
                if (lines[i].contains(logColorReplacements[lci])) {
                    lines[i] = lines[i].replace(logColorReplacements[lci], this.ansi(logColors[lci]));
                    lastColor = logColors[lci];
                    lci++;
                    continue;
                }
                break;
            }
        }
        print = String.join("\n", lines);
        this.logString(level, time, caller, thread, logger, print);
    }

    private String ansi(LogColor color) {
        return ansi.ansi(color);
    }

    private String ansi0(LogColor color) {
        return ansi(C100) + "[" + ansi(color) + "%s" + ansi(C100) + "]" + ansi.reset() + " ";
    }

    private void logString(LogLevel level, TemporalAccessor time, StackTraceElement caller, Thread thread, Logger logger, String string) {
        this.setSystemLevel(level == null ? LogLevel.INFO : level);
        for (String object : this.lines(string)) {
            this.printTime(time);
            if (caller == null) {
                if (logger != null) this.printLoggerName(logger);
                this.printThread(thread);
                if (level != null) this.printLevel(level);
            } else {
                if (level != null) this.printLevel(level);
                this.printThread(thread);
                this.out.printf("[%s.%s:%s] ", caller.getClassName(), caller.getMethodName(), caller.getLineNumber());
            }
            assert level != null;
            this.out.printf(ansi.formatln(), ansi.ansi(level.textColor()) + object);
        }
    }

    private String[] lines(String input) {
        return input.split("(\\r\\n|\\r|\\n)");
    }

    protected static class LogEntry<T> {

        final Logger logger;
        final Thread thread;
        final T object;
        final LogLevel level;
        final StackTraceElement caller;
        final TemporalAccessor time;

        public LogEntry(Logger logger, Thread thread, T object, LogLevel level) {
            this(logger, thread, object, level, null);
        }

        public LogEntry(Logger logger, Thread thread, T object, LogLevel level, StackTraceElement caller) {
            this(logger, thread, object, level, caller, LocalDateTime.now());
        }

        public LogEntry(Logger logger, Thread thread, T object, LogLevel level, StackTraceElement caller, TemporalAccessor time) {
            this.logger = logger;
            this.thread = thread;
            this.object = object;
            this.level = level;
            this.caller = caller;
            this.time = time;
        }
    }
}
