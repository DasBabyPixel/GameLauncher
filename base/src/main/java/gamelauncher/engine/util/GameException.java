/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util;

import de.dasbabypixel.annotations.Api;

/**
 * @author DasBabyPixel
 */
@Api
public class GameException extends Exception {

    @Api public GameException() {
        super();
    }

    @Api public GameException(String message, Throwable cause) {
        super(message, cause);
    }

    @Api public GameException(String message) {
        super(message);
    }

    @Api public GameException(Throwable cause) {
        super(cause);
    }

    @Api public static GameException wrap(Throwable throwable) {
        if (throwable instanceof GameException) return (GameException) throwable;
        return new GameException(throwable);
    }

    @Api
    public static class RuntimeGameException extends RuntimeException {
        @Api public RuntimeGameException() {
        }

        @Api public RuntimeGameException(String message) {
            super(message);
        }

        @Api public RuntimeGameException(String message, Throwable cause) {
            super(message, cause);
        }

        @Api public RuntimeGameException(Throwable cause) {
            super(cause);
        }
    }
}
