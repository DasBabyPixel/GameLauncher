/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.lwjgl.render.font.bitmap;

import de.dasbabypixel.api.property.BooleanValue;
import de.dasbabypixel.api.property.NumberInvalidationListener;
import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.data.DataUtil;
import gamelauncher.engine.render.GameItem;
import gamelauncher.engine.render.GameItem.GameItemModel;
import gamelauncher.engine.render.font.Font;
import gamelauncher.engine.render.font.GlyphProvider;
import gamelauncher.engine.render.model.CombinedModelsModel;
import gamelauncher.engine.render.model.GlyphStaticModel;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.render.shader.ShaderProgram;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.Key;
import gamelauncher.engine.util.function.GameRunnable;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.engine.util.text.Component;
import gamelauncher.engine.util.text.serializer.PlainTextComponentSerializer;
import gamelauncher.gles.font.bitmap.*;
import gamelauncher.gles.model.GLESCombinedModelsModel;
import gamelauncher.gles.model.Texture2DModel;
import gamelauncher.gles.texture.GLESTexture;
import gamelauncher.gles.util.MemoryManagement;
import gamelauncher.lwjgl.LWJGLGameLauncher;
import gamelauncher.lwjgl.render.glfw.GLFWFrame;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java8.util.concurrent.CompletableFuture;
import org.joml.Math;
import org.joml.Vector4i;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LWJGLGlyphProvider extends AbstractGameResource implements GlyphProvider {
    private static final Logger logger = Logger.logger();
    private final GLFWFrame frame;
    private final DynamicSizeTextureAtlas textureAtlas;

    public LWJGLGlyphProvider(LWJGLGameLauncher launcher) throws GameException {
        this.frame = launcher.frame().newFrame();
        this.textureAtlas = new DynamicSizeTextureAtlas(launcher.gles(), launcher, this.frame.renderThread());
    }

    @Override public GlyphStaticModel loadStaticModel(Component text, int pixelHeight) throws GameException {
        GlyphModelWrapper wrapper = new GlyphModelWrapper(null, 0, 0, 0, 0);
        Key fkey = text.style().font();
        if (fkey == null) fkey = new Key("fonts/calibri.ttf");
        Font font = frame.launcher().fontFactory().createFont(frame.launcher().resourceLoader().resource(fkey.toPath(frame.launcher().assets())));
        font.dataFuture().thenAccept(fontData -> frame.launcher().threads().workStealing.submit(() -> {
            STBTTFontinfo finfo = STBTTFontinfo.malloc();
            if (!STBTruetype.stbtt_InitFont(finfo, fontData)) {
                font.cleanup();
                finfo.free();
                throw new GameException("Failed to initialize font");
            }
            float scale = STBTruetype.stbtt_ScaleForPixelHeight(finfo, pixelHeight);
            int[] aascent = new int[1];
            int[] adescent = new int[1];
            int[] alinegap = new int[1];
            STBTruetype.stbtt_GetFontVMetrics(finfo, aascent, adescent, alinegap);
            float descent = adescent[0] * scale;
            float ascent = aascent[0] * scale;
            wrapper.ascent().number(ascent);
            wrapper.descent().number(descent);
            char[] ar = PlainTextComponentSerializer.serialize(text).toCharArray();
            List<CompletableFuture<AtlasEntry>> futures = new ArrayList<>();
            for (char ch : ar) {
                GlyphKey key = new GlyphKey(scale, ch);
                CompletableFuture<AtlasEntry> fentry = this.requireGlyphKey(key, finfo, ch, scale);
                futures.add(fentry);
            }
            float[] kernAdvances = new float[ar.length];
            for (int i = 0; i < ar.length; i++) {
                int ch = ar[i];
                int nex = ar.length - 1 == i ? -1 : ar[i + 1];
                if (nex == -1) kernAdvances[i] = 0;
                else kernAdvances[i] = STBTruetype.stbtt_GetCodepointKernAdvance(finfo, ch, nex) * scale;
            }

            GameRunnable r = () -> {
                finfo.free();
                font.cleanup();

                Int2ObjectMap<AtlasEntry> entryByIndex = new Int2ObjectOpenHashMap<>();
                try {
                    int i = 0;
                    for (CompletableFuture<AtlasEntry> f : futures) {
                        entryByIndex.put(i++, f.getNow(null));
                    }
                } catch (Throwable t) {
                    logger.error(t);
                }
                Collection<Model> meshes = new ArrayList<>();
                float mwidth = 0;
                float mheight = 0;
                float xpos = 0;
                float z = 0;
                for (int i = 0; i < entryByIndex.size(); i++) {
                    AtlasEntry e = entryByIndex.get(i);
                    if (i == 0) xpos = -e.entry.data.bearingX;

                    Vector4i bd = e.bounds;

                    NumberValue tw = e.texture.width();
                    NumberValue th = e.texture.height();
                    NumberValue tl = NumberValue.constant((float) bd.x + .5F).divide(tw);
                    NumberValue tb = NumberValue.constant((float) bd.y + .5F).divide(th);
                    NumberValue tr = NumberValue.constant((float) bd.x + .5F).add(bd.z + .5F).divide(tw);
                    NumberValue tt = NumberValue.constant((float) bd.y + .5F).add(bd.w + .5F).divide(th);

                    GlyphData data = e.entry.data;
                    float pb = -data.bearingY - data.height;
                    float pt = pb + data.height;
                    float pl = xpos + data.bearingX;
                    float pr = pl + data.width;
                    float width = pr - pl;
                    float height = pt - pb;
                    float x = pl + width / 2;
                    float y = pt + height / 2;

                    mheight = Math.max(mheight, height);
                    mwidth = Math.max(mwidth, pl + width);
                    DynamicModel m = new DynamicModel(e, tl, tr, tt, tb);

                    GameItem gi = new GameItem(m);
                    gi.position(x, y, z);
                    gi.scale(width, height, 1);
                    meshes.add(gi.createModel());
                    xpos += e.entry.data.advance + kernAdvances[i];
                }

                CombinedModelsModel cmodel = new GLESCombinedModelsModel(meshes.toArray(new Model[0]));
                GameItem gi = new GameItem(cmodel);
                gi.addColor(1, 1, 1, 0);
                GameItemModel gim = gi.createModel();
                wrapper.width(mwidth);
                wrapper.height(mheight);
                wrapper.handle(gim);
                frame.launcher().guiManager().redrawAll();
            };

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(r.toRunnable());

        }));
        return wrapper;
    }

    private CompletableFuture<Void> releaseGlyphKey(GlyphKey key) {
        if (key.required.decrementAndGet() == 0) {
            return this.textureAtlas.removeGlyph(this.getId(key));
        }
        return CompletableFuture.completedFuture(null);
    }

    private CompletableFuture<AtlasEntry> requireGlyphKey(GlyphKey key, STBTTFontinfo finfo, int ch, float scale) throws GameException {
//        return this.frame.launcher().threads().cached.submit(() -> {
        int id = this.getId(key);

        DynamicSizeTextureAtlas.AddFuture af = this.textureAtlas.addGlyph(id, () -> {
            MemoryManagement memoryManagement = frame.launcher().memoryManagement();
            IntBuffer x0 = memoryManagement.allocInt(1);
            IntBuffer y0 = memoryManagement.allocInt(1);
            IntBuffer x1 = memoryManagement.allocInt(1);
            IntBuffer y1 = memoryManagement.allocInt(1);
            IntBuffer advance = memoryManagement.allocInt(1);
            IntBuffer lsb = memoryManagement.allocInt(1);
            GlyphData gdata = new GlyphData();
            STBTruetype.stbtt_GetCodepointBitmapBox(finfo, ch, scale, scale, x0, y0, x1, y1);
            STBTruetype.stbtt_GetCodepointHMetrics(finfo, ch, advance, lsb);
            gdata.advance = advance.get(0) * scale;
            gdata.bearingX = x0.get(0);
            gdata.bearingY = y1.get(0);
            gdata.width = x1.get(0) - x0.get(0);
            gdata.height = y1.get(0) - y0.get(0);
            IntBuffer bw = memoryManagement.allocInt(1);
            IntBuffer bh = memoryManagement.allocInt(1);
            ByteBuffer output = memoryManagement.calloc((gdata.width + 2) * (gdata.height + 2));
            output.position(gdata.width + 3);
            STBTruetype.stbtt_MakeCodepointBitmapSubpixel(finfo, output, gdata.width, gdata.height, gdata.width + 2, scale, scale, 0, 0, ch);

            // Setup for usage by GLESTexture
            ByteBuffer buf = memoryManagement.calloc(DataUtil.BYTES_INT * output.capacity() + DataUtil.BYTES_INT * 2 + GLESTexture.SIGNATURE_RAW.length);
            buf.put(GLESTexture.SIGNATURE_RAW);
            buf.putInt(gdata.width + 2);
            buf.putInt(gdata.height + 2);
            for (int i = 0; i < output.capacity(); i++) {
                buf.position(buf.position() + 3);
                byte b = output.get(i);
                buf.put(b);
            }
            buf.flip();

            memoryManagement.free(output);
            memoryManagement.free(bw);
            memoryManagement.free(bh);
            memoryManagement.free(advance);
            memoryManagement.free(lsb);
            memoryManagement.free(x0);
            memoryManagement.free(y0);
            memoryManagement.free(x1);
            memoryManagement.free(y1);
            return new GlyphEntry(gdata, key, buf);
        });
        af.future().exceptionally(ex -> {
            logger.error(ex);
            return null;
        });
        if (!af.suc()) {
            throw new GameException("Could not add glyph to texture atlas");
        }
        return af.future();
    }

    @Override public CompletableFuture<Void> cleanup0() throws GameException {
        List<CompletableFuture<?>> futs = new ArrayList<>();
        futs.add(textureAtlas.cleanup());
        futs.add(frame.cleanup());
        return CompletableFuture.allOf(futs.toArray(new CompletableFuture[0]));
    }

    public int getId(GlyphKey key) {
        return key.hashCode();
    }

    private class DynamicModel extends AbstractGameResource implements Model {

        private final AtlasEntry e;
        private final NumberValue tl;
        private final NumberValue tr;
        private final NumberValue tt;
        private final NumberValue tb;
        private final BooleanValue invalid = BooleanValue.trueValue();

        private Texture2DModel texture2DModel;

        public DynamicModel(AtlasEntry e, NumberValue tl, NumberValue tr, NumberValue tt, NumberValue tb) {
            this.e = e;
            this.tl = tl;
            this.tr = tr;
            this.tt = tt;
            this.tb = tb;
            NumberInvalidationListener invalidationListener = property -> invalid.value(true);
            tl.addListener(invalidationListener);
            tr.addListener(invalidationListener);
            tt.addListener(invalidationListener);
            tb.addListener(invalidationListener);
        }

        @Override public void render(ShaderProgram program) throws GameException {
            if (invalid.booleanValue()) {
                invalid.value(false);
                if (texture2DModel != null) {
                    texture2DModel.cleanup();
                }
                texture2DModel = new Texture2DModel(e.texture, tl.floatValue(), 1 - tb.floatValue(), tr.floatValue(), 1 - tt.floatValue());
            }
            texture2DModel.render(program);
        }

        @Override protected CompletableFuture<Void> cleanup0() throws GameException {
            CompletableFuture<Void> fut1 = releaseGlyphKey(e.entry.key);
            CompletableFuture<Void> fut2 = null;
            if (texture2DModel != null) {
                fut2 = texture2DModel.cleanup();
                texture2DModel = null;
            }
            return fut2 == null ? fut1 : CompletableFuture.allOf(fut1, fut2);
        }
    }

}
