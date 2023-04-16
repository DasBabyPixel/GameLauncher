/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.util.keybind;

import gamelauncher.engine.util.keybind.Keybind;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEvent;
import gamelauncher.engine.util.keybind.MouseMoveKeybindEvent;
import org.jetbrains.annotations.NotNull;

public class AndroidMouse {

    public static class MoveEvent extends AbstractKeybindEvent implements MouseMoveKeybindEvent {
        private final float oldMouseX;
        private final float oldMouseY;
        private final float mouseX;
        private final float mouseY;

        public MoveEvent(Keybind keybind, float oldMouseX, float oldMouseY, float mouseX, float mouseY) {
            super(keybind);
            this.oldMouseX = oldMouseX;
            this.oldMouseY = oldMouseY;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }

        @Override
        public float mouseX() {
            return mouseX;
        }

        @Override
        public float mouseY() {
            return mouseY;
        }

        @Override
        public float oldMouseX() {
            return oldMouseX;
        }

        @Override
        public float oldMouseY() {
            return oldMouseY;
        }

        @Override
        public @NotNull String toString() {
            return "MoveEvent{" +
                    "oldMouseX=" + oldMouseX +
                    ", oldMouseY=" + oldMouseY +
                    ", mouseX=" + mouseX +
                    ", mouseY=" + mouseY +
                    ", keybind=" + keybind().name() +
                    '}';
        }
    }

    public static class ButtonEvent extends AbstractKeybindEvent implements MouseButtonKeybindEvent {
        private final int buttonId;
        private final float mouseX;
        private final float mouseY;
        private final Type type;

        public ButtonEvent(Keybind keybind, int buttonId, float mouseX, float mouseY, Type type) {
            super(keybind);
            this.buttonId = buttonId;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.type = type;
        }

        @Override
        public int buttonId() {
            return buttonId;
        }

        @Override
        public float mouseX() {
            return mouseX;
        }

        @Override
        public float mouseY() {
            return mouseY;
        }

        @Override
        public Type type() {
            return type;
        }

        @Override
        public MouseButtonKeybindEvent withType(Type type) {
            return new ButtonEvent(keybind(), buttonId, mouseX, mouseY, type);
        }

        @Override
        public @NotNull String toString() {
            return "ButtonEvent{" +
                    "mouseX=" + mouseX +
                    ", mouseY=" + mouseY +
                    ", type=" + type +
                    ", keybind=" + keybind().name() +
                    '}';
        }
    }

}
