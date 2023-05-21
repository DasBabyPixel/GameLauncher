/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.lwjgl.input;

import com.lmax.disruptor.EventPoller;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import gamelauncher.engine.input.Input;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.*;
import gamelauncher.lwjgl.render.glfw.GLFWFrame;
import gamelauncher.lwjgl.util.keybind.*;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author DasBabyPixel
 */
public class LWJGLInput implements Input {
    private static final int ALL_MOUSE_PRESSED = -1;

    private final List<Entry> pressed = new ArrayList<>();
    private final RingBuffer<QueueEntry> ringBuffer = RingBuffer.createMultiProducer(QueueEntry::new, 1024, new SleepingWaitStrategy());
    private final EventPoller<QueueEntry> poller = ringBuffer.newPoller();
    private final List<Entry> mousePressed = new ArrayList<>();
    private final KeybindManager keybindManager;

    {
        ringBuffer.addGatingSequences(poller.getSequence());
    }

    public LWJGLInput(GLFWFrame frame) {
        this.keybindManager = frame.launcher().keybindManager();
    }

    @Override public void handleInput() throws GameException {
        try {
            poller.poll((qe, sequence, endOfBatch) -> {
                this.event(qe.entry, qe.type);
                switch (qe.type) {
                    case PRESSED:
                        this.pressed.add(qe.entry);
                        if (qe.entry.type == DeviceType.MOUSE) {
                            this.mousePressed.add(qe.entry);
                        }
                        break;
                    case RELEASED:
                        int index = this.pressed.indexOf(qe.entry);
                        if (index == -1) {
                            break;
                        }
                        Entry inPressed = this.pressed.remove(index);
                        if (inPressed.type == DeviceType.MOUSE) {
                            this.mousePressed.remove(inPressed);
                        }
                    default:
                        break;
                }
                qe.clear();
                return true;
            });
            for (int i = 0; i < pressed.size(); i++) {
                event(pressed.get(i), InputType.HELD);
            }
        } catch (Exception e) {
            throw new GameException(e);
        }
    }

    private void event(Entry entry, InputType input) throws GameException {
        switch (entry.type) {
            case KEYBOARD:
                this.keyEvent(input, entry.key, entry.scancode, entry.ch);
                break;
            case MOUSE:
                if (input == InputType.MOVE) { // Generate a moveEvent for every single pressed mouse button, if no button is pressed then generate a moveEvent with keybind -1.
                    if (mousePressed.isEmpty()) {
                        this.mouseEvent(input, -1, entry.omx, entry.omy, entry.mx, entry.my);
                    } else {
                        for (Entry pressed : mousePressed) {
                            this.mouseEvent(input, pressed.key, entry.omx, entry.omy, entry.mx, entry.my);
                        }
                    }
                    break;
                }
                this.mouseEvent(input, entry.key, entry.omx, entry.omy, entry.mx, entry.my);
                break;
        }
    }

    private void mouseEvent(InputType inputType, int mouseButton, float omx, float omy, float mx, float my) throws GameException {
        if (inputType == InputType.SCROLL) {
            keybindManager.post(new LWJGLScrollKeybindEvent(keybindManager.keybind(LWJGLKeybindManager.SCROLL), mx, my));
        } else {
            if (inputType == InputType.MOVE) {
                for (Entry e : this.mousePressed) {
                    e.mx = mx;
                    e.my = my;
                }
            }
            Keybind keybind = keybindManager.keybind(LWJGLKeybindManager.MOUSE_ADD + mouseButton);
            KeybindEvent event;
            switch (inputType) {
                case HELD:
                    event = new LWJGLMouseButtonKeybindEvent(keybind, mouseButton, mx, my, MouseButtonKeybindEvent.Type.HOLD);
                    break;
                case MOVE:
                    event = new LWJGLMouseMoveKeybindEvent(keybind, omx, omy, mx, my);
                    break;
                case PRESSED:
                    event = new LWJGLMouseButtonKeybindEvent(keybind, mouseButton, mx, my, MouseButtonKeybindEvent.Type.PRESS);
                    break;
                case RELEASED:
                    event = new LWJGLMouseButtonKeybindEvent(keybind, mouseButton, mx, my, MouseButtonKeybindEvent.Type.RELEASE);
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
            this.keybindManager.post(event);
        }
    }

    private void keyEvent(InputType inputType, int key, int scancode, char c) throws GameException {
        int id = key == GLFW.GLFW_KEY_UNKNOWN ? scancode == GLFW.GLFW_KEY_UNKNOWN ? LWJGLKeybindManager.KEYBOARD_CODEPOINT_ADD + c : LWJGLKeybindManager.KEYBOARD_SCANCODE_ADD + scancode : LWJGLKeybindManager.KEYBOARD_ADD + key;
        Keybind keybind = keybindManager.keybind(id);
        KeybindEvent event;
        switch (inputType) {
            case HELD:
                event = new LWJGLKeyboardKeybindEvent(keybind, KeyboardKeybindEvent.Type.HOLD);
                break;
            case PRESSED:
                event = new LWJGLKeyboardKeybindEvent(keybind, KeyboardKeybindEvent.Type.PRESS);
                break;
            case RELEASED:
                event = new LWJGLKeyboardKeybindEvent(keybind, KeyboardKeybindEvent.Type.RELEASE);
                break;
            case REPEAT:
                event = new LWJGLKeyboardKeybindEvent(keybind, KeyboardKeybindEvent.Type.REPEAT);
                break;
            case CHARACTER:
                event = new LWJGLKeyboardKeybindEvent.Character(keybind, KeyboardKeybindEvent.Type.CHARACTER, c);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        keybindManager.post(event);
    }

    public void scroll(float xoffset, float yoffset) throws GameException {
        enqueue(this.newEntry(0, 0, 0, xoffset, yoffset), InputType.SCROLL);
    }

    public void mouseMove(float omx, float omy, float mx, float my) {
        enqueue(this.newEntry(ALL_MOUSE_PRESSED, omx, omy, mx, my), InputType.MOVE);
    }

    public void mousePress(int key, float mx, float my) {
        enqueue(this.newEntry(key, 0, 0, mx, my), InputType.PRESSED);
    }

    public void mouseRelease(int key, float mx, float my) {
        enqueue(this.newEntry(key, 0, 0, mx, my), InputType.RELEASED);
    }

    public void keyRepeat(int key, int scancode, char ch) {
        enqueue(this.newEntry(key, scancode, ch), InputType.REPEAT);
    }

    public void keyPress(int key, int scancode, char ch) {
        enqueue(this.newEntry(key, scancode, ch), InputType.PRESSED);
    }

    public void keyRelease(int key, int scancode, char ch) {
        enqueue(this.newEntry(key, scancode, ch), InputType.RELEASED);
    }

    public void character(char ch) {
        enqueue(this.newEntry(GLFW.GLFW_KEY_UNKNOWN, GLFW.GLFW_KEY_UNKNOWN, ch), InputType.CHARACTER);
    }

    public void enqueue(Entry entry, InputType type) {
        ringBuffer.publishEvent((event, sequence, arg0, arg1) -> event.set(arg0, arg1), entry, type);
    }

    private Entry newEntry(int key, int scancode, char ch) {
        return new Entry(key, scancode, ch, DeviceType.KEYBOARD);
    }

    private Entry newEntry(int key, float omx, float omy, float mx, float my) {
        return new Entry(key, DeviceType.MOUSE, omx, omy, mx, my);
    }

    /**
     * @author DasBabyPixel
     */
    public enum InputType {
        /**
         * When a mouse button or key is pressed
         */
        PRESSED,
        /**
         * When a mouse button or key is being held
         */
        HELD,
        /**
         * When a mouse button or key is released
         */
        RELEASED,
        /**
         * When a key is being held for text input
         */
        REPEAT,
        /**
         * When the mouse is being moved
         */
        MOVE,
        /**
         * When scrolling
         */
        SCROLL,
        /**
         * When a character is pressed. For text input
         */
        CHARACTER
    }

    /**
     * @author DasBabyPixel
     */
    public enum DeviceType {
        /**
         * The Mouse
         */
        MOUSE,
        /**
         * The Keyboard
         */
        KEYBOARD
    }

    private static class QueueEntry {

        private Entry entry;
        private InputType type;

        public QueueEntry() {
        }

        public void set(Entry entry, InputType inputType) {
            this.entry = entry;
            this.type = inputType;
        }

        @Override public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (this.getClass() != obj.getClass()) return false;
            QueueEntry other = (QueueEntry) obj;
            return Objects.equals(this.entry, other.entry) && this.type == other.type;
        }

        public void clear() {
            entry = null;
            type = null;
        }
    }

    private static class Entry {

        private final int key;
        private final DeviceType type;
        private int scancode;
        private float mx, my;

        private float omx;

        private float omy;

        private char ch;

        public Entry(int key, int scancode, char ch, DeviceType type) {
            this.key = key;
            this.scancode = scancode;
            this.ch = ch;
            this.type = type;
        }

        public Entry(int key, DeviceType type, float omx, float omy, float mx, float my) {
            this.key = key;
            this.type = type;
            this.mx = mx;
            this.my = my;
            this.omx = omx;
            this.omy = omy;
        }

        @Override public int hashCode() {
            return Objects.hash(this.key, this.ch, this.scancode, this.type);
        }

        @Override public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (this.getClass() != obj.getClass()) return false;
            Entry other = (Entry) obj;
            return this.key == other.key && this.type == other.type && this.scancode == other.scancode && this.ch == other.ch;
        }

        @Override public String toString() {
            return "Entry [key=" + this.key + ", type=" + this.type + ", mx=" + this.mx + ", my=" + this.my + "]";
        }

    }

}
