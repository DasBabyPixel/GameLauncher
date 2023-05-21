/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.gles.gui;

import de.dasbabypixel.api.property.Property;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.gui.guis.TextureGui;
import gamelauncher.engine.render.*;
import gamelauncher.engine.render.texture.Texture;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.property.PropertyVector4f;
import gamelauncher.gles.GLES;
import gamelauncher.gles.model.Texture2DModel;
import gamelauncher.gles.texture.GLESTexture;

public class GLESTextureGui extends ParentableAbstractGui implements TextureGui {

    private final Property<Texture> texture = Property.empty();
    private final PropertyVector4f color = new PropertyVector4f(1, 1, 1, 1);
    private final Camera camera;
    private final GLES gles;
    private boolean generated = false;
    private GameItem.GameItemModel model;
    private DrawContext context;

    public GLESTextureGui(GLES gles) throws GameException {
        super(gles.launcher());
        this.gles = gles;
        this.camera = EmptyCamera.instance();
    }

    @Override protected void doCleanup() throws GameException {
        if (generated) texture.value().cleanup();
        model.cleanup();
        launcher().contextProvider().freeContext(context, ContextProvider.ContextType.HUD);
    }

    @Override protected void doInit() throws GameException {

        context = launcher().contextProvider().loadContext(launcher().frame().framebuffer(), ContextProvider.ContextType.HUD);
        Texture2DModel t2d = new Texture2DModel(texture());
        GameItem item = new GameItem(t2d);

        item.position().x.bind(xProperty().add(widthProperty().divide(2)));
        item.position().y.bind(yProperty().add(heightProperty().divide(2)));
        item.scale().x.bind(widthProperty());
        item.scale().y.bind(heightProperty());
        item.color().bind(color);
        model = item.createModel();
    }

    @Override protected boolean doRender(float mouseX, float mouseY, float partialTick) throws GameException {
        context.update(camera);
        context.drawModel(model);
        context.program().clearUniforms();
        return super.doRender(mouseX, mouseY, partialTick);
    }

    @Override public GLESTexture texture() throws GameException {
        Texture tex = texture.value();
        if (tex == null) {
            generated = true;
            this.texture.value(tex = gles.textureManager().createTexture());
        }
        return (GLESTexture) tex;
    }

    @Override public PropertyVector4f color() {
        return color;
    }

    @Override public void texture(Texture texture) throws GameException {
        this.texture.value(texture);
    }

    @Override public Property<Texture> textureProperty() {
        return texture;
    }
}
