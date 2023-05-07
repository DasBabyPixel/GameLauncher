package gamelauncher.engine.util.text;

import gamelauncher.engine.util.text.format.Style;

public interface ComponentBuilder<C extends Component, B extends ComponentBuilder<C, B>> extends ComponentLike {

    B append(Component component);

    B append(Component... components);

    B append(Iterable<? extends Component> components);

    B style(Style style);

    Style style();

    default B appendNewline() {
        return append(Component.newline());
    }

    default B appendSpace() {
        return append(Component.space());
    }

}
