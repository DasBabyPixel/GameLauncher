/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.app;

import android.annotation.TargetApi;
import gamelauncher.android.AndroidGameLauncher;
import gamelauncher.android.AndroidLauncher;
import gamelauncher.engine.event.EventHandler;
import gamelauncher.engine.event.events.gui.GuiOpenEvent;
import gamelauncher.engine.gui.guis.MainScreenGui;
import gamelauncher.engine.util.concurrent.Threads;

@TargetApi(1)
public class AndroidApp extends AndroidLauncher {
    private AndroidGameLauncher launcher;
    @Override public void init(AndroidGameLauncher launcher) {
        this.launcher = launcher;
        launcher.eventManager().registerListener(this);
    }

    @EventHandler public void handle(GuiOpenEvent event) {
    }
}
