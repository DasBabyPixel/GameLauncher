package gamelauncher.engine.util.text;

import gamelauncher.engine.util.text.format.Style;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

class TextComponentImpl extends AbstractComponent implements TextComponent {
    static final TextComponentImpl NEWLINE = createDirect("\n");
    static final TextComponentImpl EMPTY = createDirect("");
    static final TextComponentImpl SPACE = createDirect(" ");

    private final String content;

    private TextComponentImpl(Collection<Component> children, Style style, String content) {
        super(children, style);
        this.content = content;
    }

    private static TextComponentImpl create(Collection<Component> children, Style style, String content) {
        if (children.isEmpty() && style.isEmpty()) {
            if (content.isEmpty()) return EMPTY;
            if (content.equals(" ")) return SPACE;
            if (content.equals("\n")) return NEWLINE;
        }
        return new TextComponentImpl(children, style, content);
    }

    private static TextComponentImpl createDirect(String content) {
        return new TextComponentImpl(new ArrayList<>(), Style.empty(), content);
    }

    @Override public Builder toBuilder() {
        return new Builder(this);
    }

    @Override public @NotNull Component style(Style style) {
        return null;
    }

    @Override public @NotNull Component append(Component other) {
        return null;
    }

    @Override public String content() {
        return content;
    }

    @Override public TextComponent content(String content) {
        return null;
    }

    static class Builder extends AbstractComponentBuilder<TextComponent, TextComponent.Builder> implements TextComponent.Builder {

        private String content = "";

        Builder() {

        }

        Builder(TextComponentImpl component) {
            content(component.content);
            style(component.style());
            append(component.children());
        }

        @Override protected Builder instance() {
            return this;
        }

        @Override public String content() {
            return content;
        }

        @Override public TextComponent.Builder content(String content) {
            this.content = content;
            return this;
        }

        @Override public @NotNull TextComponent asComponent() {
            return create(children, style, content);
        }
    }
}
