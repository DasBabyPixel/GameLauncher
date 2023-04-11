package gamelauncher.android;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import com.lmax.disruptor.*;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.input.Input;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.keybind.KeybindManager;

public class AndroidInput implements Input, View.OnKeyListener, View.OnTouchListener {
    private static final ThreadLocal<QueueEntry> tempLocalEntry = ThreadLocal.withInitial(QueueEntry::new);
    private final RingBuffer<QueueEntry> ringBuffer;
    private final EventPoller<QueueEntry> poller;
    private final KeybindManager keybindManager;

    public AndroidInput(GameLauncher launcher) {
        this.keybindManager = launcher.keybindManager();
        this.ringBuffer = RingBuffer.createMultiProducer(new QueueEntry.Factory(), 1024, new SleepingWaitStrategy());
        this.poller = this.ringBuffer.newPoller();
    }

    @Override
    public void handleInput() throws GameException {
        Handle handle;
        while ((handle = nextValue()) != null) {
            System.out.println(handle);
        }
    }

    private Handle nextValue() throws GameException {
        Handle[] a = new Handle[1];
        try {
            poller.poll((event, sequence, endOfBatck) -> {
                a[0] = event.handle;
                return false;
            });
        } catch (Exception e) {
            throw new GameException(e);
        }
        return a[0];
    }

    public void offer(Handle handle) {
        this.ringBuffer.publishEvent((event, sequence, h) -> event.handle = h, handle);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (event.getAction()) {
            case KeyEvent.ACTION_DOWN:
                offer(new AndroidInput.KeyHandle(keyCode, AndroidInput.KeyHandle.ACTION_PRESS));
                break;
            case KeyEvent.ACTION_UP:
                offer(new AndroidInput.KeyHandle(keyCode, AndroidInput.KeyHandle.ACTION_RELEASE));
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        System.out.println(event);
        return false;
    }

    private static class QueueEntry {
        private Handle handle;

        public void set(QueueEntry other) {
            this.handle = other.handle;
        }

        private static class Factory implements EventFactory<QueueEntry> {
            @Override
            public QueueEntry newInstance() {
                return new QueueEntry();
            }
        }
    }

    public interface Handle {
    }

    public static class KeyHandle implements Handle {
        public static final int ACTION_PRESS = 0;
        public static final int ACTION_RELEASE = 1;
        private final int keyCode;
        private final int action;

        public KeyHandle(int keyCode, int action) {
            this.keyCode = keyCode;
            this.action = action;
        }

        @Override
        public String toString() {
            return "KeyHandle{" +
                    "keyCode=" + keyCode +
                    ", action=" + action +
                    '}';
        }
    }
}
