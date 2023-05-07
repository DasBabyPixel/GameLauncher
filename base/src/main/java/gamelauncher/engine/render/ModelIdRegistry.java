/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.render;

import de.dasbabypixel.annotations.Api;
import gamelauncher.engine.render.model.Model;
import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.engine.util.Key;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Random;

public class ModelIdRegistry extends AbstractGameResource {
    private static final Key idKey = new Key("model_id_registry");
    private final Object lock = new Object();
    private final Int2ObjectMap<WeakReference<Model>> i2o = new Int2ObjectOpenHashMap<>();
    private final Object2IntMap<WeakReference<Model>> o2i = new Object2IntOpenHashMap<>();
    private final Random random = new Random();
    private final ReferenceQueue<Model> referenceQueue = new ReferenceQueue<>();

    public ModelIdRegistry() {
        new CleanerThread();
    }

    @Api public int id(Model model) {
        synchronized (lock) {
            int id = model.storedValue(idKey, random::nextInt);
            if (!i2o.containsKey(id)) {
                WeakReference<Model> ref = new WeakReference<>(model, referenceQueue);
                i2o.put(id, ref);
                o2i.put(ref, id);
            }
            return id;
        }
    }

    @Api public Model findModel(int id) {
        synchronized (lock) {
            WeakReference<Model> ref = i2o.get(id);
            if (ref == null) return null;
            Model m = ref.get();
            if (m == null) {
                i2o.remove(id);
                o2i.removeInt(ref);
            }
            return m;
        }
    }

    @Override protected void cleanup0() throws GameException {
    }

    private class CleanerThread extends Thread {

        public CleanerThread() {
            super();
            setDaemon(true);
            setName("ModelIdRegistryCleaner");
            start();
        }

        @SuppressWarnings("InfiniteLoopStatement") @Override public void run() {
            while (true) {
                try {
                    Reference<? extends Model> ref = referenceQueue.remove();
                    synchronized (lock) {
                        int id = o2i.removeInt(ref);
                        i2o.remove(id);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
