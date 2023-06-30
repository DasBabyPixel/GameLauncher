/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util.service;

import de.dasbabypixel.annotations.Api;

import java.util.Objects;

public class ServiceName {
    private final String name;

    @Api public ServiceName(String name) {
        this.name = name;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceName that = (ServiceName) o;
        return Objects.equals(name, that.name);
    }

    @Override public int hashCode() {
        return Objects.hash(name);
    }
}
