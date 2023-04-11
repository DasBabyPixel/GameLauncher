/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.gl;

import gamelauncher.engine.render.RenderThread;

public interface IAndroidRenderThread extends RenderThread {
    void scheduleDraw();

    void waitForFrame();

    void scheduleDrawWaitForFrame();
}
