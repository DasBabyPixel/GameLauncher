/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.gles.gui;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.gui.guis.ColorGui;
import gamelauncher.engine.render.*;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.property.PropertyVector4f;
import gamelauncher.gles.GLES;
import gamelauncher.gles.mesh.Mesh;
import gamelauncher.gles.mesh.PlaneMesh;
import gamelauncher.gles.model.MeshModel;
import org.joml.Vector4f;

public class GLESColorGui extends ParentableAbstractGui implements ColorGui {
    private final PropertyVector4f color;
    private final GLES gles;
    private GameItem.GameItemModel model;
    private DrawContext context;

    public GLESColorGui(GLES gles) {
        super(gles.launcher());
        this.gles = gles;
        this.color = new PropertyVector4f(0, 0, 0, 0);
        this.color.x.addListener((NumberValue v) -> this.redraw());
        this.color.y.addListener((NumberValue v) -> this.redraw());
        this.color.z.addListener((NumberValue v) -> this.redraw());
        this.color.w.addListener((NumberValue v) -> this.redraw());
    }

    @Override
    protected void doCleanup(Framebuffer framebuffer) throws GameException {
        this.launcher().contextProvider().freeContext(this.context, ContextProvider.ContextType.HUD);
        this.model.cleanup();
    }

    @Override
    protected void doInit(Framebuffer framebuffer) throws GameException {
        this.context =
                this.launcher().contextProvider().loadContext(framebuffer, ContextProvider.ContextType.HUD);

        Mesh mesh = new PlaneMesh(gles);
        Mesh.Material mat = mesh.material();
        mat.ambientColour = mat.diffuseColour = mat.specularColour = new Vector4f(1, 0, 0, 1);
        MeshModel model = new MeshModel(mesh);
        GameItem item = new GameItem(model);
        item.position().x.bind(this.xProperty().add(this.widthProperty().divide(2)));
        item.position().y.bind(this.yProperty().add(this.heightProperty().divide(2)));
        item.scale().x.bind(this.widthProperty());
        item.scale().y.bind(this.heightProperty());
        this.model = item.createModel();

        item.color().x.bind(this.color.x);
        item.color().y.bind(this.color.y);
        item.color().z.bind(this.color.z);
        item.color().w.bind(this.color.w);
    }

    @Override
    protected boolean doRender(Framebuffer framebuffer, float mouseX, float mouseY,
                               float partialTick) throws GameException {
        this.context.update(EmptyCamera.instance());
        this.context.drawModel(this.model);
        this.context.program().clearUniforms();
        return super.doRender(framebuffer, mouseX, mouseY, partialTick);
    }

    @Override
    public PropertyVector4f color() {
        return this.color;
    }

}
