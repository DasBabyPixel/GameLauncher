package gamelauncher.engine.util.i18n;

import gamelauncher.engine.util.Key;

public class SimpleMessage implements Message {

    private final Key key;

    public SimpleMessage(Key key) {
        this.key = key;
    }

    @Override public Key key() {
        return key;
    }
}
