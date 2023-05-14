/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.gl;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ThreadSpecificExecutor;
import gamelauncher.engine.util.function.GameRunnable;
import java8.util.concurrent.CompletableFuture;

public class AndroidNativeRenderThread implements IAndroidRenderThread {
    private final AndroidFrame frame;
    private volatile ThreadSpecificExecutor executor;

    public AndroidNativeRenderThread(AndroidFrame frame) {
        this.frame = frame;
    }

    public ThreadSpecificExecutor executor() {
        return executor;
    }

    public void executor(ThreadSpecificExecutor executor) {
        this.executor = executor;
    }

    @Override public void scheduleDraw() {
        frame.launcher().view().requestRender();
    }

    @Override public void waitForFrame() {
        frame.launcher().view().requestRender();
    }

    @Override public void scheduleDrawWaitForFrame() {
        frame.launcher().view().requestRender();
    }

    @Override public AndroidFrame frame() {
        return frame;
    }

    @Override public String name() {
        return "UI-Thread";
    }

    @Override public CompletableFuture<Void> submit(GameRunnable runnable) {
        if (executor.isCurrentThread()) {
            try {
                runnable.run();
            } catch (GameException e) {
                CompletableFuture<Void> f = new CompletableFuture<>();
                f.completeExceptionally(e);
                return f;
            }
            return CompletableFuture.completedFuture(null);
        }
        CompletableFuture<Void> fut = new CompletableFuture<>();
        if (!executor.post(() -> {
            try {
                runnable.run();
            } catch (Throwable t) {
                fut.completeExceptionally(t);
            } finally {
                fut.complete(null);
            }
        })) {
            fut.completeExceptionally(new Throwable("Failed to submit task"));
        }
        return fut;
    }

    @Override public void workQueue() {
        // Nothing to do here
    }
}
