package gamelauncher.engine.util.text.format;

import gamelauncher.engine.util.Key;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

class StyleBuilderImpl implements Style.Builder {

    private final Map<TextDecoration, TextDecoration.State> decorations = new HashMap<>();
    private Key font;
    private TextColor color;

    @Override public Style.Builder decoration(TextDecoration decoration) {
        return decoration(decoration, decoration.activeState());
    }

    @Override public Style.Builder decoration(TextDecoration... decorations) {
        for (TextDecoration decoration : decorations) {
            decoration(decoration);
        }
        return this;
    }

    @Override public Style.Builder decoration(TextDecoration decoration, TextDecoration.State state) {
        decorations.put(decoration, state);
        return this;
    }

    @Override public Style.Builder font(@NotNull Key font) {
        this.font = font;
        return this;
    }

    @Override public Style.Builder color(TextColor color) {
        this.color = color;
        return this;
    }

    public Style build() {
        return StyleImpl.create(font, color, new DecorationMap(decorations));
    }
}
