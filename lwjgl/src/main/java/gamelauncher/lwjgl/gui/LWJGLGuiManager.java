package gamelauncher.lwjgl.gui;

import gamelauncher.engine.gui.SimpleGuiManager;
import gamelauncher.engine.gui.launcher.LineGui;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.gui.launcher.LWJGLLineGui;

/**
 * @author DasBabyPixel
 */
public class LWJGLGuiManager extends SimpleGuiManager {

    public LWJGLGuiManager(LWJGLGameLauncher launcher) {
        super(launcher);
        this.registerGuiCreator(LineGui.class, () -> new LWJGLLineGui(launcher));
    }
}
