package gamelauncher.engine.util.logging;

import java.util.Arrays;

public class Formatted {

    private final String format;

    private final Object[] objects;

    public Formatted(String format, Object... objects) {
        this.format = format;
        this.objects = objects;
    }

    public String getFormat() {
        return this.format;
    }

    public Object[] getObjects() {
        return this.objects;
    }

    @Override public String toString() {
        return String.format(format,objects);
    }
}
