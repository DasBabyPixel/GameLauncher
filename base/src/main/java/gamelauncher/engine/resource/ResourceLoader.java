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
import org.jetbrains.annotations.Contract;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DasBabyPixel
 */
public abstract class ResourceLoader extends AbstractGameResource {

    private final ConcurrentHashMap<Path, Resource> resources = new ConcurrentHashMap<>();

    /**
     * @param path the path
     * @return if this {@link ResourceLoader} has the given {@link Path}
     * @throws GameException an exception
     */
    @Contract(pure = true) @Api public final boolean hasResource(Path path) throws GameException {
        if (isResourceLoaded(path)) {
            return true;
        }
        return canLoadResource(path);
    }

    @Contract(pure = true) @Api protected abstract boolean canLoadResource(Path path) throws GameException;

    @Api protected abstract Resource loadResource(Path path) throws GameException;

    /**
     * @param path the path
     * @return if this {@link ResourceLoader} has loaded a {@link Resource} for the given
     * {@link Path}
     */
    @Api public final boolean isResourceLoaded(Path path) {
        return resources.containsKey(path);
    }

    /**
     * Loads a {@link Resource} (if neccessary) by the given {@link Path}
     *
     * @param path the path
     * @return the {@link Resource}
     * @throws GameException an exception
     */
    @Api public final Resource resource(Path path) throws GameException {
        Path fpath = path.toAbsolutePath();
        return resources.computeIfAbsent(fpath, path1 -> {
            try {
                return loadResource(fpath);
            } catch (GameException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override protected CompletableFuture<Void> cleanup0() throws GameException {
        List<CompletableFuture<Void>> futs = new ArrayList<>();
        for (Path path : resources.keySet()) {
            futs.add(resources.remove(path).cleanup());
        }
        return CompletableFuture.allOf(futs.toArray(new CompletableFuture[0]));
    }
}
