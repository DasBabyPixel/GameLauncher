/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util.logging;

public interface AnsiProvider {
    String ansi(LogColor color);

    String formatln();

    String reset();

    String strip(String input);

    class Unsupported implements AnsiProvider {
        private static final Unsupported instance = new Unsupported();

        private Unsupported() {
        }

        public static Unsupported instance() {
            return instance;
        }

        @Override public String ansi(LogColor color) {
            return "";
        }

        @Override public String formatln() {
            return "%s%n";
        }

        @Override public String reset() {
            return "";
        }

        @Override public String strip(String input) {
            return input;
        }
    }
}
