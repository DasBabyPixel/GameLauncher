package gamelauncher.engine.util.text;

import gamelauncher.engine.util.text.format.Style;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public abstract class AbstractComponent implements Component {
    private final Collection<Component> children;
    private final Style style;

    public AbstractComponent(Collection<Component> children, Style style) {
        this.children = Collections.unmodifiableCollection(new ArrayList<>(children));
        this.style = style;
    }

    @Override public @NotNull Style style() {
        return style;
    }

    @Override public @NotNull Collection<Component> children() {
        return children;
    }
}
