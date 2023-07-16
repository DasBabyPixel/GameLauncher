/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util.logging;

import gamelauncher.engine.util.GameException;

import java.io.OutputStream;
import java.io.PrintStream;

public interface LoggingProvider {
    PrintStream createFileWriterPrintStream(OutputStream outputStream) throws GameException;
}
