/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.netty;

import gamelauncher.engine.util.logging.Logger;

import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collection;

public class NettySecurityProviderHandler {
    private final Logger logger;
    private final Collection<Handle> handles = new ArrayList<>();

    public NettySecurityProviderHandler(Logger logger) {
        this.logger = logger;
    }

    public void addProvider(Provider provider) {
        handles.add(new Handle(provider));
    }

    public void load() {
        for (Handle handle : handles) {
            handle.load();
        }
    }

    public void unload() {
        for (Handle handle : handles) {
            handle.unload();
        }
    }

    private class Handle {
        private final Provider provider;
        private Provider oldProvider;

        public Handle(Provider provider) {
            this.provider = provider;
        }

        private void load() {
            oldProvider = Security.getProvider(provider.getName());
            if (oldProvider != null) {
                logger.infof("Unloaded old Provider: " + oldProvider.getName());
                Security.removeProvider(provider.getName());
            }
            Security.addProvider(provider);
        }

        private void unload() {
            Security.removeProvider(provider.getName());
            if (oldProvider != null) {
                Security.addProvider(oldProvider);
                oldProvider = null;
            }
        }
    }
}
