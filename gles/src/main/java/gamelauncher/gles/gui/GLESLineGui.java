/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.gles.gui;

import de.dasbabypixel.api.property.NumberInvalidationListener;
import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.gui.ParentableAbstractGui;
import gamelauncher.engine.gui.guis.LineGui;
import gamelauncher.engine.render.*;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.util.GameException;
import gamelauncher.gles.GLES;
import gamelauncher.gles.gl.GLES20;
import gamelauncher.gles.mesh.Mesh;
import gamelauncher.gles.model.MeshModel;
import org.joml.Math;
import org.joml.Vector2f;

public class GLESLineGui extends ParentableAbstractGui implements LineGui {
    private final GLES gles;
    private final NumberValue fromX = NumberValue.withValue(0D);
    private final NumberValue fromY = NumberValue.withValue(0D);
    private final NumberValue toX = NumberValue.withValue(0D);
    private final NumberValue toY = NumberValue.withValue(0D);
    private final NumberInvalidationListener invalidationListener;
    private final NumberValue lineWidth = NumberValue.withValue(0D);
    private DrawContext context;
    private GameItem gameItem;
    private Model model;

    public GLESLineGui(GLES gles) {
        super(gles.launcher());
        this.gles = gles;
        NumberValue x = NumberValue.withValue(0);
        NumberValue y = NumberValue.withValue(0);
        NumberValue w = NumberValue.withValue(0);
        NumberValue h = NumberValue.withValue(0);
        this.xProperty().bind(x);
        this.yProperty().bind(y);
        this.widthProperty().bind(w);
        this.heightProperty().bind(h);

        invalidationListener = property -> {
            Vector2f to = new Vector2f(toX.floatValue(), toY.floatValue());
            Vector2f from = new Vector2f(fromX.floatValue(), fromY.floatValue());
            Vector2f direction = to.sub(from, new Vector2f());
            float lineWidth = lineWidth().floatValue();

            if (gameItem != null) {
                gameItem.position(from.x, from.y, 0);
                gameItem.scale(lineWidth, direction.length(), 0);
                gameItem.rotation((float) 0, (float) 0, (float) Math.toDegrees(direction.angle(new Vector2f(0, 1))));
            }
            x.number(Math.min(from.x, to.x) - lineWidth);
            y.number(Math.min(from.y, to.y) - lineWidth);
            w.number(Math.abs(from.x - to.x) + lineWidth * 2);
            h.number(Math.abs(from.y - to.y) + lineWidth * 2);
            redraw();
        };
        fromX.addListener(invalidationListener);
        fromY.addListener(invalidationListener);
        toX.addListener(invalidationListener);
        toY.addListener(invalidationListener);
    }

    @Override protected void doCleanup(Framebuffer framebuffer) throws GameException {
        launcher().contextProvider().freeContext(context, ContextProvider.ContextType.HUD);
        model.cleanup();
    }

    @Override protected void doInit(Framebuffer framebuffer) throws GameException {
        context = launcher().contextProvider().loadContext(framebuffer, ContextProvider.ContextType.HUD);
        // @formatter:off
        Mesh mesh = new Mesh(gles, new float[] {
                -0.5F,  0.0F, 0.0F,
                 0.5F,  0.0F, 0.0F,
                -0.5F,  1.0F, 0.0F,
                 0.5F,  1.0F, 0.0F
        }, new float[] {
                0.0F, 0.0F,
                0.0F, 0.0F,
                0.0F, 0.0F,
                0.0F, 0.0F
        }, new int[] {
                0, 1, 3,
                0, 3, 2
        }, GLES20.GL_TRIANGLES, false);
        // @formatter:on
        gameItem = new GameItem(new MeshModel(mesh));
        gameItem.color().set(1, 1, 1, 1);
        model = gameItem.createModel();
        invalidationListener.invalidated(null);
    }

    @Override protected boolean doRender(Framebuffer framebuffer, float mouseX, float mouseY, float partialTick) throws GameException {
        context.update(EmptyCamera.instance());
        context.drawModel(model);
        context.program().clearUniforms();
        return true;
    }

    @Override public NumberValue fromX() {
        return fromX;
    }

    @Override public NumberValue fromY() {
        return fromY;
    }

    @Override public NumberValue toX() {
        return toX;
    }

    @Override public NumberValue toY() {
        return toY;
    }

    @Override public NumberValue lineWidth() {
        return lineWidth;
    }
}
