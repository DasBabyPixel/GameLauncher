/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util.keybind;

import de.dasbabypixel.annotations.Api;

/**
 * @author DasBabyPixel
 */
public interface KeyboardKeybindEvent extends KeybindEvent {

    /**
     * @return the {@link Type} of this {@link KeyboardKeybindEvent}
     */
    Type type();

    /**
     * @author DasBabyPixel
     */
    enum Type {
        PRESS, RELEASE, HOLD, REPEAT, CHARACTER
    }

    interface CharacterKeybindEvent extends KeyboardKeybindEvent {
        @Api char character();

        /**
         * Used for things like backspace
         */
        interface Special extends CharacterKeybindEvent {
        }
    }
}
