/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util.service;

import de.dasbabypixel.annotations.Api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Api
public class ServiceProvider {

    private final Map<Class<?>, Object> services = new ConcurrentHashMap<>();

    @Api public <T> T service(Class<T> serviceClass) {
        Object o = services.get(serviceClass);
        if (o == null) return null;
        return serviceClass.cast(o);
    }

    @Api public <T> void register(Class<T> serviceClass, T service) {
        services.put(serviceClass, service);
    }
}
