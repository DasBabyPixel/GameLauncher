/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.gl;

import gamelauncher.android.AndroidGameLauncher;
import gamelauncher.engine.util.GameException;
import gamelauncher.gles.gl.GLFactory;

import java.util.concurrent.CopyOnWriteArraySet;

public class AndroidGLFactory implements GLFactory {
    private final AndroidGameLauncher launcher;

    public AndroidGLFactory(AndroidGameLauncher launcher) {
        this.launcher = launcher;
    }

    @Override
    public AndroidGLContext createContext() throws GameException {
        return new AndroidGLContext(launcher, new CopyOnWriteArraySet<>()).create(null);
    }
}
