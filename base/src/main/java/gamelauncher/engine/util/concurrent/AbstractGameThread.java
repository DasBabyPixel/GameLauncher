package gamelauncher.engine.util.concurrent;

import java.util.concurrent.CompletableFuture;

import gamelauncher.engine.resource.AbstractGameResource;
import gamelauncher.engine.resource.GameResource;
import gamelauncher.engine.util.GameException;

/**
 * @author DasBabyPixel
 */
public abstract class AbstractGameThread extends Thread implements GameResource {

	protected volatile boolean cleanedUp = false;

	protected final CompletableFuture<Void> cleanupFuture = new CompletableFuture<>();

	/**
	 * 
	 */
	public AbstractGameThread() {
		super();
		AbstractGameResource.create(this);
	}

	/**
	 * @param target
	 * @param name
	 */
	public AbstractGameThread(Runnable target, String name) {
		super(target, name);
		AbstractGameResource.create(this);
	}

	/**
	 * @param target
	 */
	public AbstractGameThread(Runnable target) {
		super(target);
		AbstractGameResource.create(this);
	}

	/**
	 * @param name
	 */
	public AbstractGameThread(String name) {
		super(name);
		AbstractGameResource.create(this);
	}

	/**
	 * @param group
	 * @param target
	 * @param name
	 * @param stackSize
	 */
	public AbstractGameThread(ThreadGroup group, Runnable target, String name, long stackSize) {
		super(group, target, name, stackSize);
		AbstractGameResource.create(this);
	}

	/**
	 * @param group
	 * @param target
	 * @param name
	 */
	public AbstractGameThread(ThreadGroup group, Runnable target, String name) {
		super(group, target, name);
		AbstractGameResource.create(this);
	}

	/**
	 * @param group
	 * @param target
	 */
	public AbstractGameThread(ThreadGroup group, Runnable target) {
		super(group, target);
		AbstractGameResource.create(this);
	}

	/**
	 * @param group
	 * @param name
	 */
	public AbstractGameThread(ThreadGroup group, String name) {
		super(group, name);
		AbstractGameResource.create(this);
	}

	@Override
	public final void cleanup() throws GameException {
		if (!this.cleanedUp) {
			this.cleanup0();
			this.cleanupFuture.complete(null);
			this.cleanedUp = true;
			AbstractGameResource.logCleanup(this);
		}
	}

	@Override
	public CompletableFuture<Void> cleanupFuture() {
		return this.cleanupFuture;
	}

	@Override
	public boolean isCleanedUp() {
		return this.cleanedUp;
	}

	protected abstract void cleanup0() throws GameException;

}
