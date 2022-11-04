package software.bernie.geckolib3.util;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.animatable.model.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.model.provider.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoObjectRenderer;

import javax.annotation.Nullable;

/**
 * Helper class for various methods and functions useful while rendering
 */
public final class RenderUtils {
	public static void translateMatrixToBone(PoseStack poseStack, GeoBone bone) {
		poseStack.translate(-bone.getPositionX() / 16f, bone.getPositionY() / 16f, bone.getPositionZ() / 16f);
	}

	public static void rotateMatrixAroundBone(PoseStack poseStack, GeoBone bone) {
		if (bone.getRotationZ() != 0)
			poseStack.mulPose(Vector3f.ZP.rotation(bone.getRotationZ()));

		if (bone.getRotationY() != 0)
			poseStack.mulPose(Vector3f.YP.rotation(bone.getRotationY()));

		if (bone.getRotationX() != 0)
			poseStack.mulPose(Vector3f.XP.rotation(bone.getRotationX()));
	}

	public static void rotateMatrixAroundCube(PoseStack poseStack, GeoCube cube) {
		Vector3f rotation = cube.rotation;

		poseStack.mulPose(new Quaternion(0, 0, rotation.z(), false));
		poseStack.mulPose(new Quaternion(0, rotation.y(), 0, false));
		poseStack.mulPose(new Quaternion(rotation.x(), 0, 0, false));
	}

	public static void scaleMatrixForBone(PoseStack poseStack, GeoBone bone) {
		poseStack.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
	}

	public static void translateToPivotPoint(PoseStack poseStack, GeoCube cube) {
		Vector3f pivot = cube.pivot;
		poseStack.translate(pivot.x() / 16f, pivot.y() / 16f, pivot.z() / 16f);
	}

	public static void translateToPivotPoint(PoseStack poseStack, GeoBone bone) {
		poseStack.translate(bone.getPivotX() / 16f, bone.getPivotY() / 16f, bone.getPivotZ() / 16f);
	}

	public static void translateAwayFromPivotPoint(PoseStack poseStack, GeoCube cube) {
		Vector3f pivot = cube.pivot;

		poseStack.translate(-pivot.x() / 16f, -pivot.y() / 16f, -pivot.z() / 16f);
	}

	public static void translateAwayFromPivotPoint(PoseStack poseStack, GeoBone bone) {
		poseStack.translate(-bone.getPivotX() / 16f, -bone.getPivotY() / 16f, -bone.getPivotZ() / 16f);
	}

	public static void translateAndRotateMatrixForBone(PoseStack poseStack, GeoBone bone) {
		translateToPivotPoint(poseStack, bone);
		rotateMatrixAroundBone(poseStack, bone);
	}

	public static void prepMatrixForBone(PoseStack poseStack, GeoBone bone) {
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
	public static IntIntPair getTextureDimensions(ResourceLocation texture) {
		if (texture == null)
			return null;

		AbstractTexture originalTexture = null;
		Minecraft mc = Minecraft.getInstance();

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
			image = originalTexture instanceof DynamicTexture dynamicTexture ? dynamicTexture.getPixels()
					: NativeImage.read(mc.getResourceManager().getResource(texture).get().open());
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

	public static double getCurrentSystemTick() {
		return System.nanoTime() / 1E6 / 50d;
	}

	@Nullable
	public static GeoModel<?> getGeoModelForEntity(Entity entity) {
		EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);

		return renderer instanceof GeoObjectRenderer<?> geoRenderer ? (GeoModel<?>)geoRenderer.geoGeoModel() : null;
	}

	@Nullable
	public static GeoModel<?> getGeoModelForItem(Item item) {
		if (IClientItemExtensions.of(item).getCustomRenderer() instanceof GeoObjectRenderer<?> geoRenderer)
			return (GeoModel<?>)geoRenderer.geoGeoModel();

		return null;
	}

	@Nullable
	public static GeoModel<?> getGeoModelForBlock(BlockEntity blockEntity) {
		BlockEntityRenderer<?> renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(blockEntity);

		return renderer instanceof GeoObjectRenderer<?> geoRenderer ? (GeoModel<?>)geoRenderer.geoGeoModel() : null;
	}
}
