package gamelauncher.android;

import android.annotation.SuppressLint;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventPoller;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import gamelauncher.android.util.keybind.AbstractKeybindEvent;
import gamelauncher.android.util.keybind.AndroidKeybindManager;
import gamelauncher.android.util.keybind.AndroidKeyboardKeybindEvent;
import gamelauncher.android.util.keybind.AndroidMouse;
import gamelauncher.engine.input.Input;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.Keybind;
import gamelauncher.engine.util.keybind.KeybindEvent;
import gamelauncher.engine.util.keybind.KeyboardKeybindEvent;
import gamelauncher.engine.util.keybind.MouseButtonKeybindEvent;

import java.util.HashMap;
import java.util.Map;

public class AndroidInput implements Input, View.OnKeyListener, View.OnTouchListener {
    private final Map<Integer, KeyboardKeybindEvent> keyboardPressed = new HashMap<>();
    private final Map<Integer, PointerEntry> mousePressed = new HashMap<>();
    private final RingBuffer<QueueEntry> ringBuffer;
    private final EventPoller<QueueEntry> poller;
    private final AndroidKeybindManager keybindManager;
    private boolean hasDeadChar = false;
    private int deadChar;

    public AndroidInput(AndroidGameLauncher launcher) {
        this.keybindManager = launcher.keybindManager();
        this.ringBuffer = RingBuffer.createMultiProducer(new QueueEntry.Factory(), 1024, new SleepingWaitStrategy());
        this.poller = this.ringBuffer.newPoller();
        launcher.eventManager().registerListener(this);
    }

    @Override
    public void handleInput() throws GameException {
        for (KeyboardKeybindEvent event : keyboardPressed.values()) {
            keybindManager.post(new AndroidKeyboardKeybindEvent(event.keybind(), KeyboardKeybindEvent.Type.HOLD));
        }
        for (Map.Entry<Integer, PointerEntry> entry : mousePressed.entrySet()) {
            keybindManager.post(new AndroidMouse.ButtonEvent(keybindManager.getKeybind(entry.getKey()), entry.getValue().buttonId, entry.getValue().x, entry.getValue().y, MouseButtonKeybindEvent.Type.HOLD));
        }
        try {
            poller.poll((queueEvent, sequence, endOfBatch) -> {
                KeybindEvent event = queueEvent.event;
                if (event instanceof MouseHandle) {
                    MotionEvent e = ((MouseHandle) event).event();
                    try {
                        int action = e.getActionMasked();
                        int index = e.getActionIndex();
                        int id = e.getPointerId(index);
                        int keybindId = id | AndroidKeybindManager.BITS_TOUCH;
                        float pressure = e.getPressure(index);
                        float x = e.getX(index);
                        float y = e.getY(index);
//                    System.out.println(id + "   " + e);
                        Keybind keybind = keybindManager.getKeybind(keybindId);
                        if (action == MotionEvent.ACTION_MOVE) {
                            for (index = 0; index < e.getPointerCount(); index++) {
                                id = e.getPointerId(index);
                                keybindId = id | AndroidKeybindManager.BITS_TOUCH;
                                if (!mousePressed.containsKey(keybindId)) continue;
                                PointerEntry entry = mousePressed.get(keybindId);
                                x = e.getX(index);
                                y = e.getY(index);
                                pressure = e.getPressure(index);
                                //noinspection DataFlowIssue
                                if (entry.x == x && entry.y == y) continue;
                                keybind = keybindManager.getKeybind(keybindId);
                                keybindManager.post(new AndroidMouse.MoveEvent(keybind, entry.x, entry.y, x, y));
                                entry.x = x;
                                entry.y = y;
                                entry.pressure = pressure;
                            }
                        } else if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
                            mousePressed.put(keybindId, new PointerEntry(id, x, y, pressure));
                            keybindManager.post(new AndroidMouse.ButtonEvent(keybind, id, x, y, MouseButtonKeybindEvent.Type.PRESS));
                        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
                            keybindManager.post(new AndroidMouse.ButtonEvent(keybind, id, x, y, MouseButtonKeybindEvent.Type.RELEASE));
                            mousePressed.remove(keybindId);
                        }
                    } finally {
                        e.recycle();
                    }
                    return true;
                }
                keybindManager.post(event);
                if (event instanceof KeyboardKeybindEvent) {
                    int id = event.keybind().uniqueId();
                    KeyboardKeybindEvent.Type type = ((KeyboardKeybindEvent) event).type();
                    if (type == KeyboardKeybindEvent.Type.PRESS) {
                        keyboardPressed.put(id, (KeyboardKeybindEvent) event);
                    } else if (type == KeyboardKeybindEvent.Type.RELEASE) {
                        keyboardPressed.remove(id);
                    }
                }
                return true;
            });
        } catch (Exception e) {
            throw new GameException(e);
        }
    }

    public void offer(KeybindEvent keybindEvent) {
        this.ringBuffer.publishEvent((event, sequence, h) -> event.event = h, keybindEvent);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        keybindManager.lastKeyEvent = event;
        Keybind keybind = keyCode == 0 ? null : keybindManager.getKeybind(keyCode | AndroidKeybindManager.BITS_KEY);
        switch (event.getAction()) {
            case KeyEvent.ACTION_DOWN:
                offer(new AndroidKeyboardKeybindEvent(keybind, KeyboardKeybindEvent.Type.PRESS));
                int c = event.getUnicodeChar();
                if (hasDeadChar) {
                    c = KeyCharacterMap.getDeadChar(deadChar, c);
                }
                if ((c & KeyCharacterMap.COMBINING_ACCENT) == KeyCharacterMap.COMBINING_ACCENT) {
                    deadChar = (char) (c & KeyCharacterMap.COMBINING_ACCENT_MASK);
                    hasDeadChar = true;
                } else if (c != 0) {
                    for (char ch : Character.toChars(c)) {
                        offer(new AndroidKeyboardKeybindEvent.Character(keybindManager.getKeybind(ch | AndroidKeybindManager.BITS_CHARACTER), KeyboardKeybindEvent.Type.CHARACTER, ch));
                    }
                    break;
                }
            case KeyEvent.ACTION_UP:
                offer(new AndroidKeyboardKeybindEvent(keybind, KeyboardKeybindEvent.Type.RELEASE));
                break;
            case KeyEvent.ACTION_MULTIPLE:
                String s = event.getCharacters();
                for (char ch : s.toCharArray()) {
                    offer(new AndroidKeyboardKeybindEvent.Character(keybindManager.getKeybind(ch | AndroidKeybindManager.BITS_CHARACTER), KeyboardKeybindEvent.Type.CHARACTER, ch));
                }
                break;
            default:
                break;
        }
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        offer(new MouseHandle(MotionEvent.obtain(event)));
        return true;
    }

    private static class QueueEntry {
        private KeybindEvent event;

        private static class Factory implements EventFactory<QueueEntry> {
            @Override
            public QueueEntry newInstance() {
                return new QueueEntry();
            }
        }
    }

    private static class MouseHandle extends AbstractKeybindEvent {
        private final MotionEvent event;

        public MouseHandle(MotionEvent event) {
            super(null);
            this.event = event;
        }

        public MotionEvent event() {
            return event;
        }
    }

    private static class PointerEntry {
        private int buttonId;
        private float x, y;
        private float pressure;

        public PointerEntry(int buttonId, float x, float y, float pressure) {
            this.buttonId = buttonId;
            this.x = x;
            this.y = y;
            this.pressure = pressure;
        }
    }
}