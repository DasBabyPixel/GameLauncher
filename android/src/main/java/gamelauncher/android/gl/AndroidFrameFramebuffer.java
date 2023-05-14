/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.android.gl;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.render.Framebuffer;
import gamelauncher.engine.render.RenderThread;
import gamelauncher.engine.render.ScissorStack;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.gles.GLESScissorStack;
import java8.util.concurrent.CompletableFuture;

public class AndroidFrameFramebuffer extends AbstractGameResource implements Framebuffer {
    private final AndroidFrame frame;
    private final NumberValue width = NumberValue.withValue(0);
    private final NumberValue height = NumberValue.withValue(0);
    private final ScissorStack scissor;

    public AndroidFrameFramebuffer(AndroidFrame frame) {
        this.frame = frame;
        this.scissor = new GLESScissorStack(this);
    }

    @Override public void beginFrame() {
    }

    @Override public void endFrame() {
    }

    @Override public NumberValue width() {
        return width;
    }

    @Override public NumberValue height() {
        return height;
    }

    @Override public RenderThread renderThread() {
        return frame.renderThread();
    }

    @Override public ScissorStack scissorStack() {
        return scissor;
    }

    @Override public void scheduleRedraw() {
        frame.scheduleDraw();
    }

    @Override protected CompletableFuture<Void> cleanup0() {
        return null;
    }
}
