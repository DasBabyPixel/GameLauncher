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
            GUIs.add(colorGui);

            TextureGui textureGui = launcher.guiManager().createGui(TextureGui.class);
            textureGui.texture().uploadAsync(launcher.resourceLoader().resource(launcher.embedFileSystem().getPath("pixel64x64.png")).newResourceStream());
            textureGui.heightProperty().bind(heightProperty());
            textureGui.widthProperty().bind(heightProperty());
            textureGui.xProperty().bind(xProperty().add(widthProperty().subtract(textureGui.widthProperty()).divide(2D)));
            textureGui.yProperty().bind(yProperty());
            GUIs.add(textureGui);

            ButtonGui button = launcher.guiManager().createGui(ButtonGui.class);
            button.text().value(Component.text("test"));
            button.xProperty().bind(xProperty());
            button.yProperty().bind(yProperty());
            button.widthProperty().bind(widthProperty());
            button.heightProperty().bind(heightProperty().divide(2));

        }
    }
}
