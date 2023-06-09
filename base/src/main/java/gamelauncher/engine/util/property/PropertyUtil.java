/*
 * Copyright (C) 2023 Lorenz Wrobel. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

package gamelauncher.engine.util.property;

import de.dasbabypixel.api.property.Property;

public class PropertyUtil {
    public static <T> Property<T> unmodifiable(Property<? extends T> property) {
        Property<T> p = Property.withStorage(new SupplierReadOnlyStorage<>(property::value));
        p.addDependencies(property);
        return p;
    }
}
