package software.bernie.geckolib3.util;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;

public final class RenderUtils {
	/**
	 * Use {@link RenderUtils#translateToPivotPoint(MatrixStack, GeoCube)} <br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	public static void moveToPivot(GeoCube cube, MatrixStack stack) {
		translateToPivotPoint(stack, cube);
	}

	/**
	 * Use {@link RenderUtils#translateAwayFromPivotPoint(MatrixStack, GeoCube)} <br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	public static void moveBackFromPivot(GeoCube cube, MatrixStack stack) {
		translateAwayFromPivotPoint(stack, cube);
	}

	/**
	 * Use {@link RenderUtils#translateToPivotPoint(MatrixStack, GeoBone)} <br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	public static void moveToPivot(GeoBone bone, MatrixStack stack) {
		translateToPivotPoint(stack, bone);
	}

	/**
	 * Use {@link RenderUtils#translateAwayFromPivotPoint(MatrixStack, GeoBone)} <br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	public static void moveBackFromPivot(GeoBone bone, MatrixStack stack) {
		translateAwayFromPivotPoint(stack, bone);
	}

	/**
	 * Use {@link RenderUtils#scaleMatrixForBone(MatrixStack, GeoBone)} <br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	public static void scale(GeoBone bone, MatrixStack stack) {
		scaleMatrixForBone(stack, bone);
	}

	/**
	 * Use {@link RenderUtils#translateMatrixToBone(MatrixStack, GeoBone)} <br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	public static void translate(GeoBone bone, MatrixStack stack) {
		translateMatrixToBone(stack, bone);
	}

	/**
	 * Use {@link RenderUtils#rotateMatrixAroundBone(MatrixStack, GeoBone)} <br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	public static void rotate(GeoBone bone, MatrixStack stack) {
		rotateMatrixAroundBone(stack, bone);
	}

	/**
	 * Use {@link RenderUtils#rotateMatrixAroundCube(MatrixStack, GeoCube)} <br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	public static void rotate(GeoCube bone, MatrixStack stack) {
		rotateMatrixAroundCube(stack, bone);
	}

	public static void translateMatrixToBone(MatrixStack poseStack, GeoBone bone) {
		poseStack.translate(-bone.getPositionX() / 16f, bone.getPositionY() / 16f, bone.getPositionZ() / 16f);
	}

	public static void rotateMatrixAroundBone(MatrixStack poseStack, GeoBone bone) {
		if (bone.getRotationZ() != 0.0F) {
			poseStack.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(bone.getRotationZ()));
		}

		if (bone.getRotationY() != 0.0F) {
			poseStack.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(bone.getRotationY()));
		}

		if (bone.getRotationX() != 0.0F) {
			poseStack.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(bone.getRotationX()));
		}
	}

	public static void rotateMatrixAroundCube(MatrixStack poseStack, GeoCube cube) {
		Vec3f rotation = cube.rotation;

		poseStack.multiply(new Quaternion(0, 0, rotation.getZ(), false));
		poseStack.multiply(new Quaternion(0, rotation.getY(), 0, false));
		poseStack.multiply(new Quaternion(rotation.getX(), 0, 0, false));
	}

	public static void scaleMatrixForBone(MatrixStack poseStack, GeoBone bone) {
		poseStack.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
	}

	public static void translateToPivotPoint(MatrixStack poseStack, GeoCube cube) {
		Vec3f pivot = cube.pivot;
		poseStack.translate(pivot.getX() / 16f, pivot.getY() / 16f, pivot.getZ() / 16f);
	}

	public static void translateToPivotPoint(MatrixStack poseStack, GeoBone bone) {
		poseStack.translate(bone.rotationPointX / 16f, bone.rotationPointY / 16f, bone.rotationPointZ / 16f);
	}

	public static void translateAwayFromPivotPoint(MatrixStack poseStack, GeoCube cube) {
		Vec3f pivot = cube.pivot;
		poseStack.translate(-pivot.getX() / 16f, -pivot.getY() / 16f, -pivot.getZ() / 16f);
	}

	public static void translateAwayFromPivotPoint(MatrixStack poseStack, GeoBone bone) {
		poseStack.translate(-bone.rotationPointX / 16f, -bone.rotationPointY / 16f, -bone.rotationPointZ / 16f);
	}

	public static void translateAndRotateMatrixForBone(MatrixStack poseStack, GeoBone bone) {
		translateToPivotPoint(poseStack, bone);
		rotateMatrixAroundBone(poseStack, bone);
	}

	public static void prepMatrixForBone(MatrixStack poseStack, GeoBone bone) {
		translateMatrixToBone(poseStack, bone);
		translateToPivotPoint(poseStack, bone);
		rotateMatrixAroundBone(poseStack, bone);
		scaleMatrixForBone(poseStack, bone);
		translateAwayFromPivotPoint(poseStack, bone);
	}

	/**
	 * Gets the actual dimensions of a texture resource from a given path.<br>
	 * Not performance-efficient, and should not be relied upon
	 * @param texture The path of the texture resource to check
	 * @return The dimensions (width x height) of the texture, or null if unable to find or read the file
	 */
	@Nullable
	public static IntIntPair getTextureDimensions(Identifier texture) {
		if (texture == null)
			return null;

		AbstractTexture originalTexture = null;
		MinecraftClient mc = MinecraftClient.getInstance();

		try {
			originalTexture = mc.submit(() -> mc.getTextureManager().getTexture(texture)).get();
		}
		catch (Exception e) {
			GeckoLib.LOGGER.warn("Failed to load image for id {}", texture);
			e.printStackTrace();
		}

		if (originalTexture == null)
			return null;

		NativeImage image = null;

		try {
			image = originalTexture instanceof NativeImageBackedTexture dynamicTexture ? dynamicTexture.getImage()
					: NativeImage.read(mc.getResourceManager().getResource(texture).getInputStream());
		}
		catch (Exception e) {
			GeckoLib.LOGGER.error("Failed to read image for id {}", texture);
			e.printStackTrace();
		}

		return image == null ? null : IntIntImmutablePair.of(image.getWidth(), image.getHeight());
	}

	public static Matrix4f invertAndMultiplyMatrices(Matrix4f baseMatrix, Matrix4f inputMatrix) {
		inputMatrix = inputMatrix.copy();

		inputMatrix.invert();
		inputMatrix.multiply(baseMatrix);

		return inputMatrix;
	}
}
