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
import gamelauncher.engine.network.Connection;
import gamelauncher.engine.network.NetworkClient;
import gamelauncher.engine.util.GameException;
import gamelauncher.netty.NettyNetworkClient;
import gamelauncher.netty.standalone.StandaloneServer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

public class AndroidApp extends AndroidLauncher {
    private AndroidGameLauncher launcher;

    @Override public void init(AndroidGameLauncher launcher) {
        this.launcher = launcher;
        launcher.eventManager().registerListener(this);
    }

    @EventHandler public void handle(GuiOpenEvent event) {
    }
}
