/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.resource;

import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.Key;
import gamelauncher.engine.util.function.GameSupplier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

abstract class StorageResource implements GameResource {

    protected final Map<Key, Object> map = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked") @Override public <T> T storedValue(Key key) {
        return (T) map.get(key);
    }

    @SuppressWarnings("unchecked") @Override public <T> T storedValue(Key key, GameSupplier<T> defaultSupplier) {
        return (T) map.computeIfAbsent(key, key1 -> {
            try {
                return defaultSupplier.get();
            } catch (GameException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override public void storeValue(Key key, Object value) {
        if (value == null) map.remove(key);
        else map.put(key, value);
    }

}
