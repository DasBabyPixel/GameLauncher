package gamelauncher.engine.util.logging;

import gamelauncher.engine.util.Color;

/**
 * @author DasBabyPixel
 */
public class LogColor {

    @SuppressWarnings("javadoc") public static final LogColor RED = new LogColor(new Color(255, 0, 0)), GREEN = new LogColor(new Color(0, 255, 0)), BLUE = new LogColor(new Color(0, 0, 255)), RESET = new LogColor(null), LIGHT_GRAY = new LogColor(160, 160, 160);

    private final Color color;

    /**
     * @param color
     */
    public LogColor(Color color) {
        this.color = color;
    }

    /**
     * @param r
     * @param g
     * @param b
     * @see Color#Color(int, int, int)
     */
    public LogColor(int r, int g, int b) {
        this(new Color(r, g, b));
    }

    /**
     * @return the color
     */
    public Color color() {
        return this.color;
    }

}
