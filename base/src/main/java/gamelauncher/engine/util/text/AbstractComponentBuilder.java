package gamelauncher.engine.util.text;

import gamelauncher.engine.util.text.format.Style;

import java.util.Collections;
import java.util.List;

abstract class AbstractComponentBuilder<C extends BuildableComponent<C, B>, B extends ComponentBuilder<C, B>>
		implements ComponentBuilder<C, B> {
	protected List<Component> children = Collections.emptyList();
	protected Style style = Style.empty();

	public AbstractComponentBuilder() {

	}

	public AbstractComponentBuilder(Component component) {
		children.addAll(component.children());
		style = component.style();
	}

	@Override
	public B append(Component... components) {
		for (Component component : components) {
			append(component);
		}
		return instance();
	}

	@Override
	public B append(Component component) {
		children.add(component);
		return instance();
	}

	@Override
	public B append(Iterable<? extends Component> components) {
		for (Component component : components) {
			append(component);
		}
		return instance();
	}

	@Override
	public B style(Style style) {
		this.style = style;
		return instance();
	}

	@Override
	public Style style() {
		return style;
	}

	protected abstract B instance();
}
