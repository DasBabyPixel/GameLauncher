package gamelauncher.engine.util.text.format;

import gamelauncher.engine.util.Key;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class StyleImpl implements Style {

    public static final StyleImpl EMPTY = new StyleImpl(null, null, new DecorationMap(new HashMap<>()));
    private final Key font;
    private final TextColor color;
    private final DecorationMap decorations;

    private StyleImpl(Key font, TextColor color, DecorationMap decorations) {
        this.font = font;
        this.color = color;
        this.decorations = decorations;
    }

    static StyleImpl create(Key font, TextColor color, DecorationMap decorations) {
        if (font == null && color == null && decorations.isEmpty()) return EMPTY;
        return new StyleImpl(font, color, decorations);
    }

    @Override public boolean isEmpty() {
        return font == null && color == null && decorations.isEmpty();
    }

    @Override public TextColor color() {
        return color;
    }

    @Override public Style color(TextColor color) {
        if (Objects.equals(color, this.color)) return this;
        return create(font, color, decorations);
    }

    @Override public DecorationMap decorations() {
        return decorations;
    }

    @Override public Style decoration(TextDecoration decoration) {
        return decoration(decoration, decoration.activeState());
    }

    @Override public Style decoration(TextDecoration... decorations) {
        Map<TextDecoration, TextDecoration.State> map = new HashMap<>();
        for (TextDecoration decoration : decorations)
            if (!this.decorations.contains(decoration) || this.decorations.getState(decoration) != decoration.activeState()) map.put(decoration, decoration.activeState());
        if (map.isEmpty()) return this;
        return create(font, color, this.decorations.with(map));
    }

    @Override public Style decoration(TextDecoration decoration, TextDecoration.State state) {
        if (decorations.contains(decoration) && decorations.getState(decoration) == state) return this;
        return create(font, color, decorations.with(decoration, state));
    }

    @Override public boolean hasDecoration(TextDecoration decoration) {
        return decorations.contains(decoration);
    }

    @Override public TextDecoration.State decorationState(TextDecoration decoration) {
        return decorations.getState(decoration);
    }

    @Override public @NotNull Key font() {
        return font;
    }

    @Override public Style font(@NotNull Key font) {
        if (Objects.equals(font, this.font)) return this;
        return create(font, color, decorations);
    }
}
