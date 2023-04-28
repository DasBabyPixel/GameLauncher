package gamelauncher.engine.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * @author DasBabyPixel
 */
public class SimpleSetting<T> implements Setting<T> {

    protected final AtomicReference<T> value = new AtomicReference<>(null);
    protected final Type type;
    protected final Supplier<T> defaultSupplier;
    protected final Gson gson;

    public SimpleSetting(Type type, Supplier<T> defaultSupplier) {
        this.type = type;
        this.defaultSupplier = defaultSupplier;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public SimpleSetting(Type type, T defaultValue) {
        this(type, () -> defaultValue);
    }

    @Override public T getValue() {
        T val = this.value.get();
        if (val == null) {
            val = this.defaultSupplier.get();
            this.value.set(val);
        }
        return val;
    }

    @Override public void setValue(T value) {
        this.value.set(value);
    }

    @Override public JsonElement serialize() {
        return this.gson.toJsonTree(this.getValue());
    }

    @Override public void deserialize(JsonElement json) {
        this.setValue(this.gson.fromJson(json, this.type));
    }

    @Override public void setDefaultValue() {
        this.setValue(this.defaultSupplier.get());
    }
}
