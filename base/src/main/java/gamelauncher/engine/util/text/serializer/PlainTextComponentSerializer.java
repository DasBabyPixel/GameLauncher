package gamelauncher.engine.util.text.serializer;

import gamelauncher.engine.util.text.Component;
import gamelauncher.engine.util.text.flattener.ComponentFlattener;

public class PlainTextComponentSerializer {

    public static String serialize(Component component) {
        StringBuilder b = new StringBuilder();
        ComponentFlattener.textOnly().flatten(component, b::append);
        return b.toString();
    }

    public static Component deserialize(String text) {
        return Component.text(text);
    }

}
