/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.lwjgl.util;

import gamelauncher.engine.util.logging.LoggingProvider;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class LWJGLLoggingProvider implements LoggingProvider {
    @Override public PrintStream createFileWriterPrintStream(OutputStream outputStream) {
        return new PrintStream(outputStream, false, StandardCharsets.UTF_8);
    }
}
