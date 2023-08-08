/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.internal.util;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.logging.LoggingProvider;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class AndroidLoggingProvider implements LoggingProvider {
    @Override public PrintStream createFileWriterPrintStream(OutputStream outputStream) throws GameException {
        try {
            //noinspection CharsetObjectCanBeUsed
            return new PrintStream(outputStream, false, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw GameException.wrap(e);
        }
    }
}
