package gamelauncher.lwjgl.render.font.sdf;

import java.awt.*;

public interface MSDFGen {


	/// Generates a conventional single-channel signed distance field.
	void generateSDF(BitmapRef<float, 1>output, Shape shape, Projection projection, double range,
			GeneratorConfig config=GeneratorConfig());

	/// Generates a single-channel signed pseudo-distance field.
	void generatePseudoSDF(BitmapRef<float, 1>output, Shape shape, Projection projection,
			double range, GeneratorConfig config=GeneratorConfig());

	/// Generates a multi-channel signed distance field. Edge colors must be assigned first! (See edgeColoringSimple)
	void generateMSDF(const BitmapRef<float, 3>&output, const Shape &shape, const
			Projection &projection, double range, const
			MSDFGeneratorConfig &config=MSDFGeneratorConfig());

	/// Generates a multi-channel signed distance field with true distance in the alpha channel. Edge colors must be assigned first.
	void generateMTSDF(const BitmapRef<float, 4>&output, const Shape &shape, const
			Projection &projection, double range, const
			MSDFGeneratorConfig &config=MSDFGeneratorConfig());

	// Old version of the function API's kept for backwards compatibility
	void generateSDF(const BitmapRef<float, 1>&output, const Shape &shape, double range, const
			Vector2 &scale, const Vector2 &translate, bool overlapSupport =true);

	void generatePseudoSDF(const BitmapRef<float, 1>&output, const Shape &shape, double range, const
			Vector2 &scale, const Vector2 &translate, bool overlapSupport =true);

	void generateMSDF(const BitmapRef<float, 3>&output, const Shape &shape, double range, const
			Vector2 &scale, const Vector2 &translate, const
			ErrorCorrectionConfig &errorCorrectionConfig=ErrorCorrectionConfig(),bool overlapSupport =true);

	void generateMTSDF(const BitmapRef<float, 4>&output, const Shape &shape, double range, const
			Vector2 &scale, const Vector2 &translate, const
			ErrorCorrectionConfig &errorCorrectionConfig=ErrorCorrectionConfig(),bool overlapSupport =true);

	// Original simpler versions of the previous functions, which work well under normal circumstances, but cannot deal with overlapping contours.
	void generateSDF_legacy(const BitmapRef<float, 1>&output, const Shape &shape, double range, const
			Vector2 &scale, const Vector2 &translate);

	void generatePseudoSDF_legacy(const BitmapRef<float, 1>&output, const Shape &shape,
			double range, const Vector2 &scale, const Vector2 &translate);

	void generateMSDF_legacy(const BitmapRef<float, 3>&output, const Shape &shape, double range, const
			Vector2 &scale, const Vector2 &translate,
			ErrorCorrectionConfig errorCorrectionConfig =ErrorCorrectionConfig());

	void generateMTSDF_legacy(const BitmapRef<float, 4>&output, const Shape &shape, double range, const
			Vector2 &scale, const Vector2 &translate,
			ErrorCorrectionConfig errorCorrectionConfig =ErrorCorrectionConfig());


}
