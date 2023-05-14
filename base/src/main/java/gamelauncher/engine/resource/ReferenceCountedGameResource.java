/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.resource;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.util.GameException;
import java8.util.concurrent.CompletableFuture;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class ReferenceCountedGameResource extends StorageResource {
    private final CompletableFuture<Void> cleanupFuture = new CompletableFuture<>();
    private final AtomicInteger referenceCount = new AtomicInteger(1);

    public ReferenceCountedGameResource() {
        startTracking();
    }

    /**
     * @return the cleanup future, completes immediately if this has still remaining references
     * @throws GameException
     */
    @Override public final CompletableFuture<Void> cleanup() throws GameException {
        int ref = release();
        return ref > 0 ? CompletableFuture.completedFuture(null) : cleanupFuture;
    }

    protected abstract CompletableFuture<Void> cleanup0() throws GameException;

    private void doCleanup() throws GameException {
        CompletableFuture<Void> fut = cleanup0();
        if (fut == null) {
            cleanupFuture.complete(null);
            stopTracking();
        } else {
            fut.thenRun(() -> {
                cleanupFuture.complete(null);
                stopTracking();
            }).exceptionally(throwable -> {
                cleanupFuture.completeExceptionally(throwable);
                stopTracking();
                return null;
            });
        }
    }

    @Override public final boolean cleanedUp() {
        return cleanupFuture.isDone();
    }

    /**
     * Adds one reference to the reference count.
     *
     * @return the new reference count
     * @throws IllegalStateException when the referenceCount was 0 before
     */
    public final int require() throws GameException {
        return referenceCount.updateAndGet(operand -> {
            if (operand <= 0) throw new IllegalStateException("Reference count was 0 when it was required");
            return operand + 1;
        });
    }

    /**
     * Removes one reference from the reference count
     *
     * @return the new reference count
     */
    public final int release() throws GameException {
        int newRefCount = referenceCount.decrementAndGet();
        if (newRefCount == 0) doCleanup();
        return newRefCount;
    }

    @Api public final int referenceCount() {
        int rc = referenceCount.get();
        return Math.max(rc, 0);
    }

    /**
     * @return the cleanup future for the entire resource. This complete once all references have been released
     */
    @Override public final CompletableFuture<Void> cleanupFuture() {
        return cleanupFuture;
    }
}
