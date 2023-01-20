package gamelauncher.engine.render;

/**
 * @author DasBabyPixel
 */
public interface GameRenderer extends FrameRenderer {

	/**
	 * Sets the {@link Renderer}
	 * 
	 * @param renderer
	 */
	void setRenderer(Renderer renderer);

	/**
	 * @return the {@link Renderer}
	 */
	Renderer renderer();

}
