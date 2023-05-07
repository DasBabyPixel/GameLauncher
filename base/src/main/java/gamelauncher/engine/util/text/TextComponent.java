package gamelauncher.engine.util.text;

import org.jetbrains.annotations.NotNull;

public interface TextComponent extends Component, BuildableComponent<TextComponent, TextComponent.Builder> {
    String content();

    TextComponent content(String content);

    interface Builder extends ComponentBuilder<TextComponent, Builder> {
        String content();

        Builder content(String content);

        @Override @NotNull TextComponent asComponent();
    }
}
