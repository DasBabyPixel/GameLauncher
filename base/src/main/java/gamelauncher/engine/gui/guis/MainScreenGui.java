/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.gui.guis;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.gui.Gui;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.text.Component;

/**
 * The {@link MainScreenGui} when the {@link GameLauncher} is started
 *
 * @author DasBabyPixel
 */
public interface MainScreenGui extends Gui {

    @Api
    class Simple extends ParentableAbstractGui implements MainScreenGui {

        public Simple(GameLauncher launcher) throws GameException {
            super(launcher);

            ColorGui colorGui = launcher.guiManager().createGui(ColorGui.class);
            colorGui.xProperty().bind(xProperty());
            colorGui.yProperty().bind(yProperty());
            colorGui.widthProperty().bind(widthProperty());
            colorGui.heightProperty().bind(heightProperty());

            colorGui.color().set(1, 0.5F, 0F, 1);
            addGUI(colorGui);

            TextureGui textureGui = launcher.guiManager().createGui(TextureGui.class);
            textureGui.texture().uploadAsync(launcher.resourceLoader().resource(launcher.embedFileSystem().getPath("pixel64x64.png")).newResourceStream());
            textureGui.heightProperty().bind(heightProperty());
            textureGui.widthProperty().bind(heightProperty());
            textureGui.xProperty().bind(xProperty().add(widthProperty().subtract(textureGui.widthProperty()).divide(2D)));
            textureGui.yProperty().bind(yProperty());
            addGUI(textureGui);

            ButtonGui button = launcher.guiManager().createGui(ButtonGui.class);
            ((ButtonGui.Simple.TextForeground) button.foreground().value()).textGui().text().value(Component.text("test"));
            button.xProperty().bind(xProperty());
            button.yProperty().bind(yProperty().add(heightProperty().subtract(button.heightProperty()).divide(2)));
            button.widthProperty().bind(widthProperty());
            button.heightProperty().bind(heightProperty().divide(1.1));
            button.onButtonPressed(event -> launcher.guiManager().openGui(new Simple2(launcher)));
            addGUI(button);
        }
    }

    class Simple2 extends ParentableAbstractGui implements MainScreenGui {
        public Simple2(GameLauncher launcher) throws GameException {
            super(launcher);

            ColorGui colorGui = launcher.guiManager().createGui(ColorGui.class);
            colorGui.xProperty().bind(xProperty());
            colorGui.yProperty().bind(yProperty());
            colorGui.widthProperty().bind(widthProperty());
            colorGui.heightProperty().bind(heightProperty());

            colorGui.color().set(1, 0.5F, 0F, 1);
            addGUI(colorGui);

            TextureGui textureGui = launcher.guiManager().createGui(TextureGui.class);
            textureGui.texture().uploadAsync(launcher.resourceLoader().resource(launcher.embedFileSystem().getPath("pixel64x64.png")).newResourceStream());
            textureGui.heightProperty().bind(heightProperty());
            textureGui.widthProperty().bind(heightProperty());
            textureGui.xProperty().bind(xProperty().add(widthProperty().subtract(textureGui.widthProperty()).divide(2D)));
            textureGui.yProperty().bind(yProperty());
            addGUI(textureGui);

            ScrollGui sg = launcher.guiManager().createGui(ScrollGui.class);

            ButtonGui button = launcher.guiManager().createGui(ButtonGui.class);
            ((ButtonGui.Simple.TextForeground) button.foreground().value()).textGui().text().value(Component.text("testsdadasdas"));
            button.widthProperty().bind(widthProperty().multiply(2));
            button.heightProperty().bind(heightProperty().divide(4));
            button.onButtonPressed(event -> launcher.guiManager().openGui(new Simple(launcher)));
            sg.gui().value(button);
            sg.widthProperty().bind(widthProperty());
            sg.heightProperty().bind(heightProperty().subtract(100));
            sg.xProperty().bind(xProperty());
            sg.yProperty().bind(yProperty().add(100));
            addGUI(sg);
        }
    }
}
