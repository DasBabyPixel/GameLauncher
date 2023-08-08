/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.internal.util.keybind;

import gamelauncher.engine.util.keybind.Keybind;
import gamelauncher.engine.util.keybind.KeyboardKeybindEvent;
import org.jetbrains.annotations.NotNull;

public class AndroidKeyboardKeybindEvent extends AbstractKeybindEvent implements KeyboardKeybindEvent {
    private final Type type;

    public AndroidKeyboardKeybindEvent(Keybind keybind, Type type) {
        super(keybind);
        this.type = type;
    }

    @Override public Type type() {
        return type;
    }

    @Override public @NotNull String toString() {
        return "AndroidKeyboardKeybindEvent{" + "type=" + type + ", keybind=" + keybind().name() + "}";
    }

    public static class Character extends AndroidKeyboardKeybindEvent implements CharacterKeybindEvent {
        private final char character;

        public Character(Keybind keybind, Type type, char character) {
            super(keybind, type);
            this.character = character;
        }

        @Override public char character() {
            return character;
        }

        @Override public @NotNull String toString() {
            return "Character{" + "character='" + character + "'} " + super.toString();
        }
    }
}
