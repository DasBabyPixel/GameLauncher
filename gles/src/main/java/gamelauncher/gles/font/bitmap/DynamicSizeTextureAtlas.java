/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.gles.font.bitmap;

import gamelauncher.engine.GameLauncher;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.ByteBufferBackedInputStream;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.engine.util.concurrent.Threads;
import gamelauncher.engine.util.logging.Logger;
import gamelauncher.gles.GLES;
import gamelauncher.gles.gl.GLES20;
import gamelauncher.gles.states.StateRegistry;
import gamelauncher.gles.texture.GLESTexture;
import java8.util.concurrent.CompletableFuture;
import org.joml.Vector4i;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
    volatile int maxTextureSize;
    private CompletableFuture<?> last = CompletableFuture.completedFuture(null);
    private final GLES gles;

    public DynamicSizeTextureAtlas(GLES gles, GameLauncher launcher, ExecutorThread owner) {
        this.gles = gles;
        this.launcher = launcher;
        this.owner = owner;
        this.owner.submit(() -> {
            this.maxTextureSize = StateRegistry.currentGl().glGetInteger(GLES20.GL_MAX_TEXTURE_SIZE);
        });
    }

    public Map<GLESTexture, Collection<AtlasEntry>> byTexture() {
        return byTexture;
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

    public AtlasEntry getGlyph(int id) {
        try {
            lock.readLock().lock();
            return glyphs.get(id);
        } finally {
            lock.readLock().unlock();
        }
    }

    public CompletableFuture<Void> removeGlyph(int glyphId) {
        return launcher.threads().cached.submit(() -> {
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
        });
    }

    public AddFuture addGlyph(int glyphId, GlyphEntry entry) {
        if (cleanedUp()) {
            return new AddFuture(false, CompletableFuture.completedFuture(null));
        }
        lock.readLock().lock();
        if (glyphs.containsKey(glyphId)) {
            lock.readLock().unlock();
            return new AddFuture(true, CompletableFuture.completedFuture(null));
        }
        lock.readLock().unlock();
        try {
            lock.writeLock().lock();
            if (glyphs.containsKey(glyphId)) {
                lock.writeLock().unlock();
                return new AddFuture(true, CompletableFuture.completedFuture(null));
            }

            AtlasEntry e = new AtlasEntry(null, entry, new Vector4i(0, 0, entry.data.width, entry.data.height));
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
                e.texture.allocate(64, 64);
                byTexture.put(e.texture, new HashSet<>());
                af = add(glyphId, e);
            }
            lock.writeLock().unlock();
            return af;
        } catch (GameException e) {
            throw new RuntimeException(e);
        }
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
            ResourceStream stream = new ResourceStream(null, false, new ByteBufferBackedInputStream(e.entry.buffer), null);
            CompletableFuture<Void> glf = new CompletableFuture<>();
            last = last.thenRunAsync(() -> {
                e.texture.uploadSubAsync(stream, e.bounds.x, e.bounds.y).thenRun(() -> {
                    launcher.guiManager().redrawAll();
                    glf.complete(null);
                });
                //					e.texture.write();
                //					e.texture.write();
            }, launcher.threads().cached.executor());
            byTexture.get(e.texture).add(e);
            glyphs.put(glyphId, e);
            return new AddFuture(true, glf);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static class AddFuture {
        private final boolean suc;
        private final CompletableFuture<Void> glFuture;

        public AddFuture(boolean suc, CompletableFuture<Void> glFuture) {
            this.suc = suc;
            this.glFuture = glFuture;
        }

        public boolean suc() {
            return suc;
        }

        public CompletableFuture<Void> glFuture() {
            return glFuture;
        }
    }

    private Vector4i scaledBounds(Vector4i textureBounds) {
        boolean same = textureBounds.z == textureBounds.w;
        int newWidth = same ? textureBounds.z * 2 : textureBounds.z;
        if (newWidth > maxTextureSize) {
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

    @Override public void cleanup0() throws GameException {
        Threads.waitFor(owner.submit(() -> {
            lock.writeLock().lock();
            for (GLESTexture texture : byTexture.keySet()) {
                texture.cleanup();
            }
            glyphs.clear();
            byTexture.clear();
            lock.writeLock().unlock();
        }));
    }

}
