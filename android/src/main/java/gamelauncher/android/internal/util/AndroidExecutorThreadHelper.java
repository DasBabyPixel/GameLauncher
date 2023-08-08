/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.internal.util;

import gamelauncher.android.AndroidGameLauncher;
import gamelauncher.android.internal.gl.AndroidNativeRenderThread;
import gamelauncher.engine.render.Frame;
import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.engine.util.concurrent.ExecutorThreadHelper;

public class AndroidExecutorThreadHelper implements ExecutorThreadHelper {
    private final AndroidGameLauncher launcher;

    public AndroidExecutorThreadHelper(AndroidGameLauncher launcher) {
        this.launcher = launcher;
    }

    @Override public ExecutorThread currentThread() {
        if (Thread.currentThread() instanceof ExecutorThread) return (ExecutorThread) Thread.currentThread();
        Frame f = launcher.frame();
        if (f != null) {
            ExecutorThread rt = f.renderThread();
            if (rt instanceof AndroidNativeRenderThread) {
                AndroidNativeRenderThread anrt = (AndroidNativeRenderThread) rt;
                if (anrt.executor().isCurrentThread()) {
                    return anrt;
                }
            }
        }
        return null;
    }
}
