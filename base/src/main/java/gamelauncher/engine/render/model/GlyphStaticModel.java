package gamelauncher.engine.render.model;

/**
 * @author DasBabyPixel
 */
public interface GlyphStaticModel extends Model {

	/**
	 * @return the width
	 */
	int getWidth();

	/**
	 * @return the height
	 */
	int getHeight();

	/**
	 * @return the descent
	 */
	float getDescent();

	/**
	 * @return the ascent
	 */
	float getAscent();

}
