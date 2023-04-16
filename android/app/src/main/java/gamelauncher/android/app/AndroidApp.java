/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.app;

import gamelauncher.android.AndroidGameLauncher;
import gamelauncher.android.AndroidLauncher;
import gamelauncher.engine.event.EventHandler;
import gamelauncher.engine.event.events.gui.GuiOpenEvent;
import gamelauncher.engine.gui.guis.MainScreenGui;

public class AndroidApp extends AndroidLauncher {
    @Override
    public void init(AndroidGameLauncher launcher) {

    }

    @EventHandler
    public void handle(GuiOpenEvent event) {
        if (event.gui() instanceof MainScreenGui) {

        }
    }
}
