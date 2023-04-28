/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util.collections;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Stream;

public class Collections {

    /**
     * Returns a synchronized (thread-safe) list backed by the specified
     * list.  In order to guarantee serial access, it is critical that
     * <strong>all</strong> access to the backing list is accomplished
     * through the returned list.<p>
     * <p>
     * It is imperative that the user manually synchronize on the returned
     * list when traversing it via {@link Iterator}, {@link Spliterator}
     * or {@link Stream}:
     * <pre>
     *  List list = Collections.synchronizedList(new ArrayList());
     *      ...
     *  synchronized (list) {
     *      Iterator i = list.iterator(); // Must be in synchronized block
     *      while (i.hasNext())
     *          foo(i.next());
     *  }
     * </pre>
     * Failure to follow this advice may result in non-deterministic behavior.
     *
     * <p>The returned list will be serializable if the specified list is
     * serializable.
     *
     * @param <T>  the class of the objects in the list
     * @param list the list to be "wrapped" in a synchronized list.
     * @return a synchronized view of the specified list.
     */
    public static <T> List<T> synchronizedList(List<T> list) {
        return java.util.Collections.synchronizedList(list);
    }

    public static <T> List<T> emptyList() {
        return java.util.Collections.emptyList();
    }

    public static <T> Deque<T> newConcurrentDeque() {
        return new LinkedBlockingDeque<>();
    }

    public static <T> Queue<T> newConcurrentQueue() {
        return new ConcurrentLinkedQueue<>();
    }

}
