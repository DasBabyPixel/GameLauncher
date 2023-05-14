package gamelauncher.gles.states;

import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.util.GameException;
import gamelauncher.gles.gl.GLContext;
import gamelauncher.gles.gl.GLES32;
import java8.util.concurrent.CompletableFuture;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ContextDependant extends AbstractGameResource {

    public final ConcurrentMap<ContextLocal<?>, Object> contextLocals = new ConcurrentHashMap<>();
    private final GLContext context;

    public ContextDependant(GLContext context) {
        this.context = context;
    }

    @Override protected CompletableFuture<Void> cleanup0() throws GameException {
        for (ContextLocal<?> local : contextLocals.keySet()) {
            local.remove(this);
        }
        return null;
    }

    public GLES32 gl() {
        return context.gl32();
    }

    public GLContext context() {
        return context;
    }
}
