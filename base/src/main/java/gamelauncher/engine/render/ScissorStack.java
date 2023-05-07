package gamelauncher.engine.render;

import de.dasbabypixel.api.property.NumberValue;
import gamelauncher.engine.gui.Gui;
import gamelauncher.engine.util.collections.Collections;
import org.joml.Math;

import java.util.Deque;

/**
 * @author DasBabyPixel
 */
public abstract class ScissorStack {

    private final Deque<Scissor> stack = Collections.newConcurrentDeque();

    public ScissorStack() {
    }

    public Scissor pushScissor(Gui gui) {
        Scissor scissor = pushScissor(gui.xProperty(), gui.yProperty(), gui.widthProperty(), gui.heightProperty());
        gui.visibleXProperty().number(scissor.x);
        gui.visibleYProperty().number(scissor.y);
        gui.visibleWidthProperty().number(scissor.w);
        gui.visibleHeightProperty().number(scissor.h);
        return scissor;
    }

    public Scissor pushScissor(int x, int y, int w, int h) {
        if (stack.isEmpty()) {
            enableScissor();
        }
        Scissor scissor = createScissor(x, y, w, h, last());
        stack.addLast(scissor);
        setScissor(scissor);
        return scissor;
    }

    protected Scissor createScissor(int x, int y, int w, int h, Scissor last) {
        int l = x;
        int b = y;
        int r = x + w;
        int t = y + h;
        if (last != null) {
            l = Math.max(l, last.x());
            r = Math.max(l, Math.min(r, last.x() + last.w()));
            b = Math.max(b, last.y());
            t = Math.max(b, Math.min(t, last.y() + last.h()));

            return new Scissor(l, b, r - l, t - b);
            //			nx = Math.max(x, last.x);
            //			ny = Math.max(y, last.y);
            //			int maxh = last.h - Math.abs(last.y - y);
            //			Logger.getLogger().info(maxh);
            //			w = Math.max(0, Math.min(w, last.w - Math.abs(nx - x)));
            //			h = Math.max(0, Math.min(h, maxh));
        }
        return new Scissor(x, y, w, h);
    }

    public Scissor pushScissor(NumberValue x, NumberValue y, NumberValue w, NumberValue h) {
        float fsx0 = x.floatValue();
        float fsy0 = y.floatValue();
        float fsx1 = fsx0 + w.floatValue();
        float fsy1 = fsy0 + h.floatValue();
        int sx = (int) Math.floor(fsx0);
        int sy = (int) Math.floor(fsy0);
        int sw = Math.round(fsx1 - sx);
        int sh = Math.round(fsy1 - sy);
        return pushScissor(sx, sy, sw, sh);
        //		return pushScissor(x.intValue(), y.intValue(), w.intValue(), h.intValue());
    }

    /**
     *
     */
    public void popScissor() {
        stack.removeLast();
        if (stack.isEmpty()) {
            disableScissor();
        }
    }

    /**
     * @return the last scissor element on the stack, or null of no scissor is set
     */
    public Scissor last() {
        return stack.peekLast();
    }

    protected abstract void enableScissor();

    protected abstract void setScissor(Scissor scissor);

    protected abstract void disableScissor();

    /**
     * @author DasBabyPixel
     */
    public static class Scissor {
        private final int x, y, w, h;

        public Scissor(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        public int x() {
            return x;
        }

        public int y() {
            return y;
        }

        public int w() {
            return w;
        }

        public int h() {
            return h;
        }

        @Override public String toString() {
            return "Scissor{" + "x=" + x + ", y=" + y + ", w=" + w + ", h=" + h + '}';
        }
    }
}
