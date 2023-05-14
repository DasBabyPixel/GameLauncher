/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.gles.texture;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.data.DataUtil;
import gamelauncher.engine.render.texture.Texture;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.resource.ResourceStream;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.concurrent.ExecutorThread;
import gamelauncher.engine.util.concurrent.ExecutorThreadService;
import gamelauncher.engine.util.profiler.Profiler;
import gamelauncher.gles.GLES;
import gamelauncher.gles.gl.GLES20;
import gamelauncher.gles.gl.GLES30;
import gamelauncher.gles.states.StateRegistry;
import gamelauncher.gles.util.MemoryManagement;
import java8.util.concurrent.CompletableFuture;
import org.joml.Math;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static gamelauncher.gles.gl.GLES20.*;
import static gamelauncher.gles.gl.GLES30.GL_DRAW_FRAMEBUFFER;
import static gamelauncher.gles.gl.GLES30.GL_READ_FRAMEBUFFER;

public class GLESTexture extends AbstractGameResource implements Texture {

    public static final byte[] SIGNATURE_RAW = new byte[]{0x45, (byte) 0xFF, 0x61, 0x19};
    private final MemoryManagement memoryManagement;
    private final ExecutorThread owner;
    private final ExecutorThreadService service;
    private final AtomicReference<GLESTextureFormat> format = new AtomicReference<>(GLESTextureFormat.RGBA);
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    private final AtomicInteger textureId = new AtomicInteger(0);
    private final NumberValue width = NumberValue.withValue(0);
    private final NumberValue height = NumberValue.withValue(0);
    private final Profiler profiler;
    private final GLES gles;
    private final GLESTextureManager manager;
    private int cwidth = 0;
    private int cheight = 0;

    public GLESTexture(GLES gles, ExecutorThread owner, ExecutorThreadService service) {
        this.gles = gles;
        this.memoryManagement = gles.memoryManagement();
        this.manager = gles.textureManager();
        this.owner = owner;
        this.service = service;
        this.profiler = gles.launcher().profiler();
    }

    public GLES gles() {
        return gles;
    }

    private void upload0(ByteBuffer buffer, int x, int y, int width, int height) throws InvalidSizeException {
        profiler.begin("render", "upload");
        lock.writeLock().lock();
        int mintw = x + width;
        int minth = y + height;
        if (cwidth < mintw || cheight < minth) {
            throw new InvalidSizeException(cwidth + " < " + mintw + " || " + cheight + " < " + minth);
        }
        GLES20 cur = StateRegistry.currentGl();
        cur.glBindTexture(GL_TEXTURE_2D, textureId.get());
        profiler.check();
        cur.glTexSubImage2D(GL_TEXTURE_2D, 0, x, y, width, height, format.get().gl(), GL_UNSIGNED_BYTE, buffer);
        profiler.check();
        cur.glBindTexture(GL_TEXTURE_2D, 0);
        profiler.check();
        lock.writeLock().unlock();
        profiler.end();
    }

    private boolean isRaw(byte[] data) {
        for (int i = 0; i < SIGNATURE_RAW.length; i++) {
            if (data[i] != SIGNATURE_RAW[i]) {
                return false;
            }
        }
        return true;
    }

    private void resize0(int width, int height) {
        profiler.begin("render", "resize");
        try {
            lock.writeLock().lock();
            int owidth = cwidth;
            int oheight = cheight;
            int oid = textureId.get();
            if (oid == 0) {
                safeCreate();
                oid = textureId.get();
            }

            if (oheight == 0 || owidth == 0) {
                allocateCreated(width, height);
                return;
            }

            int nid = createTexture();
            cwidth = width;
            cheight = height;

            int copyw = Math.min(owidth, width);
            int copyh = Math.min(oheight, height);

            GLESTextureFormat format = this.format.get();

            GLES30 cur = StateRegistry.currentGl();

            cur.glBindTexture(GL_TEXTURE_2D, nid);
            cur.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            cur.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            cur.glTexImage2D(GL_TEXTURE_2D, 0, format.glInternal(), width, height, 0, format.gl(), GL_UNSIGNED_BYTE, null);
            cur.glBindTexture(GL_TEXTURE_2D, 0);

            CLTextureUtility u = manager.clTextureUtility.get();
            u.framebuffer1.bind();
            cur.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, nid, 0);
            u.framebuffer1.unbind();
            u.framebuffer2.bind();
            cur.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, oid, 0);
            u.framebuffer2.unbind();

            cur.glBindFramebuffer(GL_READ_FRAMEBUFFER, u.framebuffer2.getId());
            cur.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, u.framebuffer1.getId());
            cur.glBlitFramebuffer(0, 0, copyw, copyh, 0, 0, copyw, copyh, GL_COLOR_BUFFER_BIT, GL_NEAREST);
            cur.glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
            cur.glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);

            textureId.set(nid);
            cur.glDeleteTextures(1, new int[]{oid}, 0);
            cur.glFlush();
            cur.glFinish();
        } finally {
            lock.writeLock().unlock();
            profiler.end();
        }
    }

    public CompletableFuture<ByteBuffer> pixels() {
        return owner.submit(() -> {
            try {
                lock.writeLock().lock();
                ByteBuffer buf = memoryManagement.allocDirect(cwidth * cheight * DataUtil.BYTES_INT);
                if (cwidth == 0 || cheight == 0) return buf;
                GLES30 cur = StateRegistry.currentGl();
                CLTextureUtility u = manager.clTextureUtility.get();
                u.framebuffer1.bind();
                cur.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureId.get(), 0);
                cur.glReadPixels(0, 0, cwidth, cheight, GL_RGBA, GL_UNSIGNED_BYTE, buf);
                u.framebuffer1.unbind();
                return buf;
            } finally {
                lock.writeLock().unlock();
            }
        });
    }

    private void safeCreate() {
        lock.writeLock().lock();
        if (textureId.get() == 0) {
            textureId.set(createTexture());
        }
        lock.writeLock().unlock();
    }

    private void allocateCreated(int width, int height) {
        profiler.begin("render", "allocate_created");
        lock.writeLock().lock();
        cwidth = width;
        cheight = height;
        GLESTextureFormat format = this.format.get();
        GLES20 cur = StateRegistry.currentGl();
        cur.glBindTexture(GL_TEXTURE_2D, textureId.get());
        cur.glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        cur.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        cur.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        cur.glTexImage2D(GL_TEXTURE_2D, 0, format.glInternal(), cwidth, cheight, 0, format.gl(), GL_UNSIGNED_BYTE, null);
        cur.glBindTexture(GL_TEXTURE_2D, 0);
        lock.writeLock().unlock();
        profiler.end();
    }

    @Override public CompletableFuture<Void> cleanup0() throws GameException {
        return owner.submit(() -> {
            lock.writeLock().lock();
            int id = textureId.getAndSet(0);
            if (id != 0) {
                StateRegistry.currentGl().glDeleteTextures(1, new int[]{id}, 0);
            }
            lock.writeLock().unlock();
        });
    }

    @Override public CompletableFuture<Void> allocate(int width, int height) {
        this.width.number(width);
        this.height.number(height);
        return owner.submit(() -> {
            lock.writeLock().lock();
            safeCreate();
            allocateCreated(width, height);
            lock.writeLock().unlock();
        });
    }

    @Override public CompletableFuture<Void> resize(int width, int height) {
        this.width.number(width);
        this.height.number(height);
        return owner.submit(() -> {
            try {
                lock.writeLock().lock();
                resize0(width, height);
            } finally {
                lock.writeLock().unlock();
            }
        });
    }

    @Override public NumberValue width() {
        return width;
    }

    @Override public NumberValue height() {
        return height;
    }

    @Override public CompletableFuture<Void> uploadAsync(ResourceStream stream) {
        return uploadSubAsync(stream, 0, 0);
    }

    @Override public CompletableFuture<Void> uploadSubAsync(ResourceStream stream, int x, int y) {
        CompletableFuture<Void> fut = new CompletableFuture<>();
        service.submit(() -> {
            profiler.begin("render", "upload_texture_worker");
            try {
                byte[] data = stream.readAllBytes();
                stream.cleanup();
                ByteArrayInputStream bin = new ByteArrayInputStream(data);

                int width;
                int height;
                ByteBuffer buff;
                if (isRaw(data)) {
                    //noinspection ResultOfMethodCallIgnored
                    bin.skip(SIGNATURE_RAW.length);
                    ByteBuffer tbbuf = memoryManagement.alloc(2 * DataUtil.BYTES_INT);
                    tbbuf.put(data, SIGNATURE_RAW.length, 2 * DataUtil.BYTES_INT);
                    tbbuf.flip();
                    width = tbbuf.getInt();
                    height = tbbuf.getInt();
                    memoryManagement.free(tbbuf);
                    buff = memoryManagement.allocDirect(width * height * format.get().size());
                    buff.put(data, SIGNATURE_RAW.length + 2 * DataUtil.BYTES_INT, data.length - SIGNATURE_RAW.length - 2 * DataUtil.BYTES_INT);
                    buff.flip();
                } else {
                    PNGDecoder dec = new PNGDecoder(bin);
                    width = dec.getWidth();
                    height = dec.getHeight();
                    buff = memoryManagement.allocDirect(4 * width * height);
                    dec.decode(buff, width * 4, PNGDecoder.Format.RGBA);
                    buff.flip();
                }
                bin.close();

                if (this.width.intValue() < width) {
                    this.width.number(width);
                }
                if (this.height.intValue() < height) {
                    this.height.number(height);
                }

                owner.submit(() -> {
                    GLES20 cur = StateRegistry.currentGl();
                    profiler.begin("render", "upload_texture");
                    lock.writeLock().lock();

                    if (textureId.get() == 0) {
                        safeCreate();
                        allocateCreated(x + width, y + height);
                    }

                    upload0(buff, x, y, width, height);
                    memoryManagement.free(buff);
                    lock.writeLock().unlock();
                    cur.glFlush();
                    cur.glFinish();
                    gles.launcher().guiManager().redrawAll();
                    profiler.end();
                    fut.complete(null);
                }).exceptionally(throwable -> {
                    fut.completeExceptionally(throwable);
                    return null;
                });
            } catch (GameException | IOException ex) {
                fut.completeExceptionally(ex);
            } finally {
                profiler.end();
            }
        });
        return fut;
    }

    @Override public CompletableFuture<Void> copyTo(Texture other, int srcX, int srcY, int dstX, int dstY, int width, int height) throws GameException {
        if (!(other instanceof GLESTexture)) {
            ClassCastException cause = new ClassCastException("Texture passed is no LWJLTexture");
            GameException ge = new GameException(cause);
            ge.setStackTrace(new StackTraceElement[0]);
            throw ge;
        }
        final GLESTexture lwjgl = (GLESTexture) other;
        return owner.submit(() -> {
            lock.readLock().lock();
            lwjgl.lock.writeLock().lock();
            int id = textureId.get();
            int oid = lwjgl.textureId.get();

            int maxcopyw = Math.min(cwidth - srcX, lwjgl.cwidth - dstX);
            int copyw = Math.min(width, maxcopyw);
            int maxcopyh = Math.min(cheight - srcY, lwjgl.cheight - dstY);
            int copyh = Math.min(width, maxcopyh);

            if (copyw >= 1 && copyh >= 1) {
                StateRegistry.currentContext().context().gl32().glCopyImageSubData(id, GL_TEXTURE_2D, 0, srcX, srcY, 0, oid, GL_TEXTURE_2D, 0, dstX, dstY, 0, copyw, copyh, 1);
            }

            lwjgl.lock.writeLock().unlock();
            lock.readLock().unlock();
        });
    }

    private int createTexture() {
        return StateRegistry.currentGl().glGenTextures();
    }

    public int getTextureId() {
        return textureId.get();
    }

//    public CompletableFuture<Void> uploadAsync(int x, int y, int width, int
//            height, ByteBuffer bbuf) {
//        CompletableFuture<Void> fut = new
//                CompletableFuture<>();
//        manager.service.submit(() -> {
//            try {
//                AtomicInteger
//                        aibuf = new AtomicInteger();
//                AtomicReference<ByteBuffer> abuf = new
//                        AtomicReference<>();
//                CompletableFuture<Void> fut2 = submit(() -> {
//                    final int
//                            buf = glGenBuffers();
//                    aibuf.set(buf);
//                    GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, buf);
//                    glBufferData(GL_PIXEL_UNPACK_BUFFER, width * height * internalFormat.size,
//                            GL_STATIC_DRAW);
//                    ByteBuffer buf1 = glMapBufferRange(GL_PIXEL_UNPACK_BUFFER,
//                            0, width * height * internalFormat.size, GL_MAP_WRITE_BIT);
//                    GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);
//                    if (buf1 == null) {
//                        int error = glGetError();
//                        throw new GameException("Couldn't map buffer! (" +
//                                Integer.toHexString(error) + ")");
//                    }
//                    abuf.set(buf1);
//                });
//                Threads.waitFor(fut2);
//                abuf.get().put(bbuf);
//                bbuf.position(0);
//                abuf.get().flip();
//                CompletableFuture<Void> fut3 = submit((GameRunnable) () -> {
//                    int buf = aibuf.get();
//                    GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, buf);
//                    glUnmapBuffer(GL_PIXEL_UNPACK_BUFFER);
//                    bind();
//                    glTexParameteri(GL_TEXTURE_2D,
//                            GL_TEXTURE_MIN_FILTER, GL_LINEAR);
//                    glTexParameteri(GL_TEXTURE_2D,
//                            GL_TEXTURE_MAG_FILTER, GL_LINEAR);
//                    glTexSubImage2D(GL_TEXTURE_2D, 0, x, y,
//                            width, height, internalFormat.gl, GL_UNSIGNED_BYTE, 0);
//                    GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);
//                    glDeleteBuffers(buf);
//                    glFlush();
//                    glFinish();
//                    fut.complete(null);
//                });
//                Threads.waitFor(fut3);
//            } catch (Throwable ex) {
//                fut.completeExceptionally(ex);
//                manager.launcher.handleError(ex);
//            }
//        });
//        return
//                fut;
//    }
//
//    @Override
//    public CompletableFuture<Void> uploadAsync(ResourceStream stream)
//            throws GameException {
//        CompletableFuture<Void> fut = new
//                CompletableFuture<>();
//        manager.service.submit(() -> {
//            try {
//                PNGDecoder decoder =
//                        stream.newPNGDecoder();
//                int w = decoder.getWidth();
//                int h =
//                        decoder.getHeight();
//                AtomicInteger aibuf = new AtomicInteger();
//                AtomicReference<ByteBuffer> abuf = new AtomicReference<>();
//                CompletableFuture<Void> fut2 = submit(() -> {
//                    final int buf = glGenBuffers();
//                    aibuf.set(buf);
//                    GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, buf);
//                    modifyLock.readLock().lock();
//                    glBufferData(GL_PIXEL_UNPACK_BUFFER, w * h *
//                            internalFormat.size, GL_STATIC_DRAW);
//                    ByteBuffer buf1 =
//                            glMapBufferRange(GL_PIXEL_UNPACK_BUFFER, 0, w * h * internalFormat.size,
//                                    GL_MAP_WRITE_BIT);
//                    modifyLock.readLock().unlock();
//                    GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);
//                    if (buf1 == null) {
//                        int error = glGetError();
//                        throw new GameException("Couldn't map buffer! (" +
//                                Integer.toHexString(error) + ")");
//                    }
//                    abuf.set(buf1);
//                });
//                Threads.waitFor(fut2);
//                decoder.decode(abuf.get(), w * DataUtil.BYTES_INT,
//                        PNGDecoder.Format.RGBA);
//                stream.cleanup();
//                abuf.get().flip();
//                CompletableFuture<Void> fut3 = submit(new GameRunnable() {
//
//                    @Override
//                    public void run() {
//                        int buf = aibuf.get();
//                        GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, buf);
//                        glUnmapBuffer(GL_PIXEL_UNPACK_BUFFER);
//                        bind();
//                        glTexParameteri(GL_TEXTURE_2D,
//                                GL_TEXTURE_MIN_FILTER, GL_LINEAR);
//                        glTexParameteri(GL_TEXTURE_2D,
//                                GL_TEXTURE_MAG_FILTER, GL_LINEAR);
//                        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w,
//                                h, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
//                        GlStates.current().bindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);
//                        glDeleteBuffers(buf);
//                        glFlush();
//                        glFinish();
//                        fut.complete(null);
//                    }
//                });
//                Threads.waitFor(fut3);
//            } catch (Throwable ex) {
//                manager.launcher.handleError(ex);
//            }
//        }); return fut;
//    }

    public static class InvalidSizeException extends GameException {

        public InvalidSizeException(String message) {
            super(message);
        }
    }
}
