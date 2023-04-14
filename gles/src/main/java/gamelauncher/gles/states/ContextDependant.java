package gamelauncher.gles.states;

import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.gles.gl.GLContext;
import gamelauncher.gles.gl.GLES31;

import java.util.concurrent.ConcurrentHashMap;

public class ContextDependant extends AbstractGameResource {

    public final ConcurrentHashMap<ContextLocal<?>, Object> contextLocals = new ConcurrentHashMap<>();
    private final GLContext context;

    public ContextDependant(GLContext context) {
        this.context = context;
    }

    @Override
    protected void cleanup0() throws GameException {
        for (ContextLocal<?> local : contextLocals.keySet()) {
            local.remove(this);
        }
    }

    public GLES31 gl() {
        return context.gl31();
    }

    public GLContext context() {
        return context;
    }
}
