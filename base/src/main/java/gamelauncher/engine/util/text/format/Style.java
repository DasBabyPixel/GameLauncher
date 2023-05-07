package gamelauncher.engine.util.text.format;

import gamelauncher.engine.util.Key;
import org.jetbrains.annotations.NotNull;

public interface Style {

    static Style.Builder style() {
        return new StyleBuilderImpl();
    }

    static Style empty() {
        return StyleImpl.EMPTY;
    }

    DecorationMap decorations();

    Style decoration(TextDecoration decoration);

    Style decoration(TextDecoration... decorations);

    Style decoration(TextDecoration decoration, TextDecoration.State state);

    boolean hasDecoration(TextDecoration decoration);

    TextDecoration.State decorationState(TextDecoration decoration);

    Key font();

    Style font(Key font);

    TextColor color();

    Style color(TextColor color);

    boolean isEmpty();

    interface Builder {

        Builder decoration(TextDecoration decoration);

        Builder decoration(TextDecoration... decorations);

        Builder decoration(TextDecoration decoration, TextDecoration.State state);

        Builder font(@NotNull Key font);

        Builder color(TextColor color);

        Style build();
    }

}
