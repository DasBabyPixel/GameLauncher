package gamelauncher.engine.util.property;

import de.dasbabypixel.api.property.ReadOnlyExternalStorage;

import java.util.function.Supplier;

public class SupplierReadOnlyStorage<T> implements ReadOnlyExternalStorage<T> {

    private final Supplier<T> supplier;

    public SupplierReadOnlyStorage(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override public T read() {
        return this.supplier.get();
    }

}
