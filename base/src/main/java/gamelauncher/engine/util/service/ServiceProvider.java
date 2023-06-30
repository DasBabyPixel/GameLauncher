/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util.service;

import de.dasbabypixel.annotations.Api;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Api
public class ServiceProvider {
    public static final ServiceName DEFAULT = new ServiceName("default");
    private final Map<ServiceName, Map<Class<?>, Object[]>> services = new HashMap<>();
    private final Map<Class<?>, Object[]> compiled = new ConcurrentHashMap<>();

    @Api public <T> T service(Class<T> serviceClass) {
        T[] o = services(serviceClass);
        return o.length == 0 ? null : o[0];
    }

    @Api public <T> T service(ServiceName serviceName, Class<T> serviceClass) {
        Object[] o = services.computeIfAbsent(serviceName, t -> new ConcurrentHashMap<>()).get(serviceClass);
        return o == null ? null : o.length == 0 ? null : serviceClass.cast(o[0]);
    }

    @Api public <T> T service(ServiceReference<T> serviceReference) {
        return service(serviceReference.serviceName(), serviceReference.serviceClass());
    }

    @SuppressWarnings("unchecked") @Api public <T> T[] services(Class<T> serviceClass) {
        if (compiled.containsKey(serviceClass)) return (T[]) compiled.get(serviceClass);
        synchronized (this) {
            List<T> l = new ArrayList<>();
            for (ServiceName serviceName : services.keySet()) {
                Map<Class<?>, Object[]> m = services.get(serviceName);
                Object[] o = m.get(serviceClass);
                if (o != null) for (Object value : o) l.add(serviceClass.cast(value));
            }
            T[] a = l.toArray(i -> (T[]) Array.newInstance(serviceClass, i));
            compiled.put(serviceClass, a);
            return a;
        }
    }

    @Api public <T> void unregister(Class<T> serviceClass, T service) {
        unregister(DEFAULT, serviceClass, service);
    }

    @Api public synchronized <T> void unregister(ServiceName serviceName, Class<T> serviceClass, T service) {
        if (!services.containsKey(serviceName)) return;
        Map<Class<?>, Object[]> m = services.get(serviceName);
        Object[] c = m.get(serviceClass);
        List<Object> l = new ArrayList<>(Arrays.asList(c));
        l.remove(service);
        c = l.toArray();
        m.put(serviceClass, c);
        compiled.remove(serviceClass);
        if (c.length > 0) return;
        m.remove(serviceClass);
        if (m.isEmpty()) services.remove(serviceName);
    }

    @Api public <T> void unregister(ServiceReference<T> reference, T service) {
        unregister(reference.serviceName(), reference.serviceClass(), service);
    }

    @Api public <T> void register(ServiceReference<T> reference, T service) {
        register(reference.serviceName(), reference.serviceClass(), service);
    }

    @Api public <T> void register(Class<T> serviceClass, T service) {
        register(DEFAULT, serviceClass, service);
    }

    @Api public synchronized <T> void register(ServiceName serviceName, Class<T> serviceClass, T service) {
        Map<Class<?>, Object[]> m = services.computeIfAbsent(serviceName, t -> new ConcurrentHashMap<>());
        if (m.containsKey(serviceClass)) {
            Object[] o = m.get(serviceClass);
            o = Arrays.copyOf(o, o.length + 1);
            o[o.length - 1] = service;
            m.put(serviceClass, o);
        } else {
            m.put(serviceClass, new Object[]{service});
        }
    }
}
