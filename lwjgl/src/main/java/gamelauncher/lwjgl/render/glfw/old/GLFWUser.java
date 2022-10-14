package gamelauncher.lwjgl.render.glfw.old;

import java.util.concurrent.CompletableFuture;

/**
 * @author DasBabyPixel
 */
public interface GLFWUser {

	/**
	 * @return the {@link #destroyFuture()}
	 */
	CompletableFuture<Void> destroy();
	
	/**
	 * @return the future for when this {@link GLFWUser} is done
	 */
	CompletableFuture<Void> destroyFuture();
	
}
