package software.bernie.geckolib.loading.definition.geometry.object;

import net.minecraft.util.Mth;
import software.bernie.geckolib.GeckoLibConstants;

/// Per-face UV rotation enum
public enum UvFaceRotation {
	NONE,
	CLOCKWISE_90,
	CLOCKWISE_180,
	CLOCKWISE_270;

	/// @return The rotation enum value matching the degrees-rotation value provided
	public static UvFaceRotation fromDegrees(int degrees) {
		degrees = correctNegativeRotation(degrees);

		try {
			return UvFaceRotation.values()[(degrees % 360) / 90];
		}
		catch (Exception e) {
			GeckoLibConstants.LOGGER.error("Invalid Face UV rotation: {}", degrees);

			return fromDegrees(Mth.floor(Math.abs(degrees) / 90f) * 90);
		}
	}

	/// Check and correct for a potentially negative input value
	///
	/// This would normally be considered an invalid input, but we handle it anyway
	private static int correctNegativeRotation(int degrees) {
		if (degrees >= 0)
			return degrees;

		return 360 - (-degrees % 360);
	}

	/// Rotate the UV coordinates of a face by this rotation enum value
	public double[] createRotatedUvs(double u, double v, double uWidth, double vHeight) {
		return switch (this) {
			case NONE -> new double[] {u, v, uWidth, v, uWidth, vHeight, u, vHeight};
			case CLOCKWISE_90 -> new double[] {uWidth, v, uWidth, vHeight, u, vHeight, u, v};
			case CLOCKWISE_180 -> new double[] {uWidth, vHeight, u, vHeight, u, v, uWidth, v};
			case CLOCKWISE_270 -> new double[] {u, vHeight, u, v, uWidth, v, uWidth, vHeight};
		};
	}
}