package gamelauncher.engine.util.text;

public interface BuildableComponent<C extends BuildableComponent<C, B>, B extends ComponentBuilder<C, B>>
		extends Component {
	B toBuilder();
}
