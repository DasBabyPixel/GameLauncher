package gamelauncher.engine.util.text;

import gamelauncher.engine.util.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Objects;

public interface Component extends ComponentLike {

    static @NotNull Component empty() {
        return TextComponentImpl.EMPTY;
    }

    static @NotNull Component newline() {
        return TextComponentImpl.NEWLINE;
    }

    static @NotNull Component space() {
        return TextComponentImpl.SPACE;
    }

    static @NotNull TextComponent.Builder text() {
        return new TextComponentImpl.Builder();
    }

    static @NotNull TextComponent text(Object text) {
        return text().content(Objects.toString(text)).asComponent();
    }

    @Unmodifiable @NotNull Collection<Component> children();

    @NotNull Style style();

    @NotNull Component style(Style style);

    @NotNull Component append(Component other);

    @Override default @NotNull Component asComponent() {
        return this;
    }

}
