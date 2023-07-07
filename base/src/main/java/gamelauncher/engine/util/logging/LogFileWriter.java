/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util.logging;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.data.Files;
import gamelauncher.engine.util.GameException;
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class LogFileWriter {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private final Path logs;
    private final Path latest;
    private final AnsiProvider ansi;
    private final FastByteArrayOutputStream preInitStream = new FastByteArrayOutputStream();
    private PrintStream latestOut;

    public LogFileWriter(@Nullable GameLauncher launcher, @NotNull AnsiProvider ansi) {
        this.ansi = ansi;
        this.logs = launcher == null ? Paths.get("logs") : launcher.gameDirectory().resolve("logs");
        this.latest = logs.resolve("latest.log");
        this.latestOut = new PrintStream(preInitStream, false, StandardCharsets.UTF_8);
    }

    public void init() throws GameException {
        Files.createDirectories(logs);
        latestOut = new PrintStream(Files.newOutputStream(latest, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE, StandardOpenOption.WRITE), false, StandardCharsets.UTF_8);
        latestOut.write(preInitStream.array, 0, preInitStream.length);
    }

    public void writeToFiles(@Nullable LogLevel level, @NotNull TemporalAccessor time, @Nullable StackTraceElement caller, @NotNull Thread thread, @Nullable Logger logger, @NotNull String string) {
        if (latestOut == null) return;
        latestOut.printf("[%s] ", dateTimeFormatter.format(time));
        if (caller == null) {
            if (logger != null) latestOut.printf("[%s] ", logger);
            latestOut.printf("[%s] ", thread.getName());
            if (level != null) latestOut.printf("[%s] ", level.name());
        } else {
            if (level != null) latestOut.printf("[%s] ", level.name());
            latestOut.printf("[%s] [%s.%s:%s] ", thread.getName(), caller.getClassName(), caller.getMethodName(), caller.getLineNumber());
        }
        latestOut.printf("%s%n", ansi.strip(string));
        latestOut.flush();
    }

    public void close() {
        latestOut.flush();
        latestOut.close();
        latestOut = null;
    }
}
