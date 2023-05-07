/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.gui;

import gamelauncher.android.AndroidGameLauncher;
import gamelauncher.android.gui.launcher.AndroidLineGui;
import gamelauncher.engine.gui.SimpleGuiManager;
import gamelauncher.engine.gui.launcher.LineGui;
import gamelauncher.gles.GLES;

public class AndroidGuiManager extends SimpleGuiManager {

    public AndroidGuiManager(AndroidGameLauncher launcher, GLES gles) {
        super(launcher);
        this.registerGuiCreator(null, LineGui.class, () -> new AndroidLineGui(launcher, gles));
    }
}
