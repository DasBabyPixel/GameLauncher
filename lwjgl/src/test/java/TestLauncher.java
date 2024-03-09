/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

import gamelauncher.engine.network.Connection;
import gamelauncher.engine.util.GameException;
import gamelauncher.lwjgl.LWJGLGameLauncher;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class TestLauncher {

    public static void main(String[] args) throws URISyntaxException, IOException, GameException {
        LWJGLGameLauncher launcher = new LWJGLGameLauncher();
        launcher.start(args);
        Connection con = launcher.networkClient().connect(new URL("https://ssh.darkcube.eu/orbits/").toURI());
        System.out.println(launcher.networkClient().proxy().hostname() + " " + launcher.networkClient().proxy().port());
        con.ensureState(Connection.State.CONNECTED).await();
        System.out.println(con.state());
    }
}
