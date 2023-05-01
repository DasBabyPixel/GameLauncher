/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.gles.gui;

import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.gui.guis.TextureGui;
import gamelauncher.engine.render.*;
import gamelauncher.engine.render.texture.Texture;
import gamelauncher.engine.util.GameException;
import gamelauncher.gles.GLES;
import gamelauncher.gles.model.Texture2DModel;
import gamelauncher.gles.texture.GLESTexture;

public class GLESTextureGui extends ParentableAbstractGui implements TextureGui {

    private final GLESTexture texture;
    private final Camera camera;
    private GameItem.GameItemModel model;
    private DrawContext context;

    public GLESTextureGui(GLES gles) throws GameException {
        super(gles.launcher());
        this.camera = EmptyCamera.instance();
        this.texture = gles.textureManager().createTexture();
    }

    @Override protected void doCleanup(Framebuffer framebuffer) throws GameException {
        texture.cleanup();
        model.cleanup();
        launcher().contextProvider().freeContext(context, ContextProvider.ContextType.HUD);
    }

    @Override protected void doInit(Framebuffer framebuffer) throws GameException {
        context = launcher().contextProvider().loadContext(framebuffer, ContextProvider.ContextType.HUD);
        Texture2DModel t2d = new Texture2DModel(texture);
        GameItem item = new GameItem(t2d);

        item.position().x.bind(xProperty().add(widthProperty().divide(2)));
        item.position().y.bind(yProperty().add(heightProperty().divide(2)));
        item.scale().x.bind(widthProperty());
        item.scale().y.bind(heightProperty());

        model = item.createModel();
    }

    @Override protected boolean doRender(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick) throws GameException {
        context.update(camera);
        context.drawModel(model);
        context.program().clearUniforms();
        return super.doRender(framebuffer, mouseX, mouseY, partialTick);
    }

    @Override public Texture texture() {
        return texture;
    }
}
