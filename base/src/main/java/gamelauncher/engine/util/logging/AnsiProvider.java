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

    class Unsupported implements AnsiProvider {

        @Override
        public String ansi(LogColor color) {
            return "";
        }

        @Override
        public String formatln() {
            return "%s%n";
        }

        @Override
        public String reset() {
            return "";
        }
    }
}
