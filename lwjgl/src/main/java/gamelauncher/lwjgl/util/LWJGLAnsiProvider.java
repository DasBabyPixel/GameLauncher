/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.lwjgl.util;

import gamelauncher.engine.util.logging.AnsiProvider;
import gamelauncher.engine.util.logging.LogColor;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

public class LWJGLAnsiProvider implements AnsiProvider {

    private static final LWJGLAnsiProvider instance = new LWJGLAnsiProvider();

    static {
        if (System.console() != null) {
            AnsiConsole.systemInstall();
            Runtime.getRuntime().addShutdownHook(new Thread(AnsiConsole::systemUninstall));
        }
    }

    private LWJGLAnsiProvider() {
    }

    public static LWJGLAnsiProvider instance() {
        return instance;
    }

    @Override public String ansi(LogColor color) {
        return Ansi.ansi().reset().fgRgb(color.color().ired(), color.color().igreen(), color.color().iblue()).toString();
    }

    @Override public String formatln() {
        return Ansi.ansi().reset().a("%s").reset().a("%n").toString();
    }

    @Override public String reset() {
        return Ansi.ansi().reset().a("").toString();
    }

    @Override public String strip(String input) {
        return input.replaceAll("\u001B\\[[;\\d]*m", "");
    }
}
