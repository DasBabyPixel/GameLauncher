/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.gles.font.bitmap;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.render.texture.TextureFilter;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.function.GameFunction;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.gles.GLES;
import gamelauncher.gles.GLESCompat;
import gamelauncher.gles.texture.GLESTexture;
import java8.util.concurrent.CompletableFuture;
import java8.util.function.Consumer;
import org.joml.Vector4i;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class DynamicSizeTextureAtlas extends AbstractGameResource {

    final Map<GLESTexture, Collection<AtlasEntry>> byTexture = new HashMap<>();
    private final Logger logger = Logger.logger();
    private final Map<Integer, AtlasEntry> glyphs = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
    private final GameLauncher launcher;
    private final ExecutorThread owner;
    private final GLES gles;

    public DynamicSizeTextureAtlas(GLES gles, GameLauncher launcher, ExecutorThread owner) {
        this.gles = gles;
        this.launcher = launcher;
        this.owner = owner;
    }

    private static boolean intersects(Vector4i v1, Vector4i v2) {
        int tw = v1.z;
        int th = v1.w;
        int rw = v2.z;
        int rh = v2.w;
        if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
            return false;
        }
        int tx = v1.x;
        int ty = v1.y;
        int rx = v2.x;
        int ry = v2.y;
        rw += rx;
        rh += ry;
        tw += tx;
        th += ty;
        //      overflow || intersect
        return ((rw < rx || rw > tx) && (rh < ry || rh > ty) && (tw < tx || tw > rx) && (th < ty || th > ry));
    }

    @Api public AtlasEntry getGlyph(int id) {
        try {
            lock.readLock().lock();
            return glyphs.get(id);
        } finally {
            lock.readLock().unlock();
        }
    }

    public CompletableFuture<Void> removeGlyph(int glyphId) {
        if (cleanedUp()) return CompletableFuture.completedFuture(null);
        return launcher.threads().workStealing.submit(() -> {
            try {
                lock.writeLock().lock();
                AtlasEntry entry = glyphs.remove(glyphId);
                if (entry != null) {
                    Collection<AtlasEntry> col = byTexture.get(entry.texture);
                    col.remove(entry);
                    if (col.isEmpty()) {
                        byTexture.remove(entry.texture);
                        entry.texture.cleanup();
                    }
                } else {
                    logger.error("Already cleaned up glyph " + glyphId);
                    GameException ex = Threads.buildStacktrace();
                    ex.initCause(new GameException());
                    logger.error(ex);
                }
            } finally {
                lock.writeLock().unlock();
            }
            return null;
        });
    }

    public <T> AddFuture addGlyph(Queue<Consumer<T>> tasks, int glyphId, GameFunction<T, GlyphEntry> dataSupplier) {
        if (cleanedUp()) {
            return new AddFuture(false, CompletableFuture.completedFuture(null));
        }
        CompletableFuture<AtlasEntry> fut = new CompletableFuture<>();

        tasks.offer(obj -> {
            try {
                lock.writeLock().lock();
                if (glyphs.containsKey(glyphId)) {
                    glyphs.get(glyphId).entry.key.required.incrementAndGet();
                    fut.complete(glyphs.get(glyphId));
                    lock.writeLock().unlock();
                    return;
                }
                GlyphEntry entry = dataSupplier.apply(obj);

                AtlasEntry e = new AtlasEntry(null, entry, new Vector4i(0, 0, entry.data.width, entry.data.height));
                entry.key.required.incrementAndGet();
                AddFuture af = null;
                for (GLESTexture texture : byTexture.keySet()) {
                    e.texture = texture;
                    af = add(glyphId, e);
                    if (af.suc) {
                        break;
                    }
                    e.texture = null;
                }
                if (af == null) {
                    e.texture = gles.textureManager().createTexture(owner);
                    e.texture.filter(TextureFilter.FilterType.MINIFICATION, TextureFilter.Filter.LINEAR);
                    e.texture.filter(TextureFilter.FilterType.MAGNIFICATION, TextureFilter.Filter.NEAREST);
                    e.texture.allocate(64, 64);
                    byTexture.put(e.texture, new HashSet<>());
                    af = add(glyphId, e);
                }
                af.glFuture.thenRun(() -> {
                    fut.complete(e);
                }).exceptionally(t -> {
                    fut.completeExceptionally(t);
                    return null;
                });
                lock.writeLock().unlock();
            } catch (GameException e) {
                throw new RuntimeException(e);
            }
        });
        return new AddFuture(true, fut);
    }

    private AddFuture add(int glyphId, AtlasEntry e) throws GameException {
        try {
            //			Thread.dumpStack();
            lock.writeLock().lock();
            Vector4i textureBounds = new Vector4i(0, 0, e.texture.width().intValue(), e.texture.height().intValue());
            Vector4i currentBounds = textureBounds;
            boolean glyphTooLarge = false;
            while (true) {
                if (findFit(e, currentBounds)) {
                    break;
                }
                Vector4i newBounds = scaledBounds(currentBounds);
                if (!newBounds.equals(currentBounds)) {
                    currentBounds = newBounds;
                } else {
                    glyphTooLarge = true;
                    break;
                }
            }
            if (!textureBounds.equals(currentBounds)) {
                e.texture.resize(currentBounds.z, currentBounds.w);
            }
            if (glyphTooLarge) {
                if (byTexture.get(e.texture).isEmpty()) {
                    logger.warnf("Glyph too large: ID: %s, CodePoint: %s, Scale: %s, Width: %s, Height: %s", glyphId, e.entry.key.codepoint, e.entry.key.scale, e.bounds.z, e.bounds.w);
                } else {
                    logger.warnf("Texture not empty???");
                    return new AddFuture(false, CompletableFuture.completedFuture(null));
                }
            }
            ResourceStream stream = new ResourceStream(null, false, new ByteArrayInputStream(e.entry.pixels), null);
            CompletableFuture<AtlasEntry> glf = new CompletableFuture<>();
            e.texture.uploadSubAsync(stream, e.bounds.x, e.bounds.y).thenRun(() -> glf.complete(e)).exceptionally(t -> {
                glf.completeExceptionally(t);
                return null;
            });
            //					e.texture.write();
            //					e.texture.write();
            byTexture.get(e.texture).add(e);
            glyphs.put(glyphId, e);
            return new AddFuture(true, glf);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private Vector4i scaledBounds(Vector4i textureBounds) {
        boolean same = textureBounds.z == textureBounds.w;
        int newWidth = same ? textureBounds.z * 2 : textureBounds.z;
        if (newWidth > GLESCompat.MAX_TEXTURE_SIZE) {
            return textureBounds;
        }
        int newHeight = same ? textureBounds.w : textureBounds.w * 2;
        return new Vector4i(0, 0, newWidth, newHeight);
    }

    private boolean findFit(AtlasEntry entry, Vector4i bounds) {
        Vector4i rect = entry.bounds;
        rect = new Vector4i(rect.x, rect.y, rect.z + 2, rect.w + 2);
        rect.y = 0;
        boolean found = false;
        Collection<Vector4i> check = byTexture.get(entry.texture).stream().map(e -> e.bounds).map(r -> new Vector4i(r.x - 1, r.y - 1, r.z + 2, r.w + 2)).collect(Collectors.toSet());
        Collection<Vector4i> remove = new HashSet<>();
        yl:
        for (; rect.y < bounds.w - rect.w; rect.y++) {
            rect.x = 0;
            for (Vector4i r : check) {
                if (r.y + r.w < rect.y) {
                    remove.add(r);
                }
            }
            check.removeAll(remove);
            remove.clear();

            xl:
            for (; rect.x < bounds.z - rect.z; rect.x++) {
                for (Vector4i r : check) {
                    if (intersects(r, rect)) {
                        rect.x = r.x + r.z - 1;
                        continue xl;
                    }
                }
                found = true;
                break yl;
            }
        }
        if (found) {
            entry.bounds.x = rect.x + 1;
            entry.bounds.y = rect.y + 1;
        }
        return found;
    }

    @Override public CompletableFuture<Void> cleanup0() throws GameException {
        return owner.submit(() -> {
            lock.writeLock().lock();
            for (GLESTexture texture : byTexture.keySet()) {
                texture.cleanup();
            }
            glyphs.clear();
            byTexture.clear();
            lock.writeLock().unlock();
        });
    }

    public static class AddFuture {
        private final boolean suc;
        private final CompletableFuture<AtlasEntry> glFuture;

        public AddFuture(boolean suc, CompletableFuture<AtlasEntry> glFuture) {
            this.suc = suc;
            this.glFuture = glFuture;
        }

        public boolean suc() {
            return suc;
        }

        public CompletableFuture<AtlasEntry> future() {
            return glFuture;
        }
    }

}
