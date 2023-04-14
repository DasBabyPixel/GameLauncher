package gamelauncher.engine.util.logging;

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

}
