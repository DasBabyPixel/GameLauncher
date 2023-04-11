/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.gl;

import android.os.Handler;
import gamelauncher.engine.util.function.GameRunnable;

import java.util.concurrent.CompletableFuture;

public class AndroidNativeRenderThread implements IAndroidRenderThread {
    private final AndroidFrame frame;
    private final Handler handler;

    public AndroidNativeRenderThread(AndroidFrame frame, Handler handler) {
        this.frame = frame;
        this.handler = handler;
    }

    @Override
    public void scheduleDraw() {
        frame.launcher().view().requestRender();
    }

    @Override
    public void waitForFrame() {
        frame.launcher().view().requestRender();
    }

    @Override
    public void scheduleDrawWaitForFrame() {
        frame.launcher().view().requestRender();
    }

    @Override
    public AndroidFrame frame() {
        return frame;
    }

    @Override
    public String name() {
        return "UI-Thread";
    }

    @Override
    public CompletableFuture<Void> submitLast(GameRunnable runnable) {
        CompletableFuture<Void> fut = new CompletableFuture<>();
        if (!handler.post(() -> {
            try {
                runnable.toRunnable().run();
            } finally {
                fut.complete(null);
            }
        })) {
            fut.complete(null);
        }
        return fut;
    }

    @Override
    public CompletableFuture<Void> submitFirst(GameRunnable runnable) {
        // We cant do this lol, so we just submit as last
        return submitLast(runnable);
    }

    @Override
    public void park() {
        // We do not park... We will get an Application Not Responding message!
    }

    @Override
    public void park(long nanos) {
        // We do not park... We will get an Application Not Responding message!
    }

    @Override
    public void unpark() {
        // We do not park... We will get an Application Not Responding message!
    }

    @Override
    public void workQueue() {
        // Nothing to do here
    }
}
