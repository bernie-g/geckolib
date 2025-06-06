package software.bernie.geckolib.util;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoReplacedEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderer;

/**
 * Helper class for various methods and functions useful while rendering
 */
public final class RenderUtil {
	public static void translateMatrixToBone(PoseStack poseStack, GeoBone bone) {
		poseStack.translate(-bone.getPosX() / 16f, bone.getPosY() / 16f, bone.getPosZ() / 16f);
	}

	public static void rotateMatrixAroundBone(PoseStack poseStack, GeoBone bone) {
		if (bone.getRotZ() != 0)
			poseStack.mulPose(Axis.ZP.rotation(bone.getRotZ()));

		if (bone.getRotY() != 0)
			poseStack.mulPose(Axis.YP.rotation(bone.getRotY()));

		if (bone.getRotX() != 0)
			poseStack.mulPose(Axis.XP.rotation(bone.getRotX()));
	}

	public static void rotateMatrixAroundCube(PoseStack poseStack, GeoCube cube) {
		Vec3 rotation = cube.rotation();

		poseStack.mulPose(new Quaternionf().rotationXYZ(0, 0, (float)rotation.z()));
		poseStack.mulPose(new Quaternionf().rotationXYZ(0, (float)rotation.y(), 0));
		poseStack.mulPose(new Quaternionf().rotationXYZ((float)rotation.x(), 0, 0));
	}

	public static void scaleMatrixForBone(PoseStack poseStack, GeoBone bone) {
		poseStack.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
	}

	public static void translateToPivotPoint(PoseStack poseStack, GeoCube cube) {
		Vec3 pivot = cube.pivot();
		poseStack.translate(pivot.x() / 16f, pivot.y() / 16f, pivot.z() / 16f);
	}

	public static void translateToPivotPoint(PoseStack poseStack, GeoBone bone) {
		poseStack.translate(bone.getPivotX() / 16f, bone.getPivotY() / 16f, bone.getPivotZ() / 16f);
	}

	public static void translateAwayFromPivotPoint(PoseStack poseStack, GeoCube cube) {
		Vec3 pivot = cube.pivot();

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
	
	public static Matrix4f invertAndMultiplyMatrices(Matrix4f baseMatrix, Matrix4f inputMatrix) {
		inputMatrix = new Matrix4f(inputMatrix);
		
		inputMatrix.invert();
		inputMatrix.mul(baseMatrix);

		return inputMatrix;
	}
	
	/**
     * Translates the provided {@link PoseStack} to face towards the given {@link Entity}'s rotation
	 * <p>
     * Usually used for rotating projectiles towards their trajectory, in an {@link GeoRenderer#preRender} override
	 */
	public static void faceRotation(PoseStack poseStack, Entity animatable, float partialTick) {
		poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) - 90));
		poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot())));
	}

	/**
	 * Add a positional vector to a matrix
	 * <p>
	 * This is specifically implemented to act as a translation of an x/y/z coordinate triplet to a render matrix
	 */
	public static Matrix4f translateMatrix(Matrix4f matrix, Vector3f vector) {
		return matrix.add(new Matrix4f().m30(vector.x).m31(vector.y).m32(vector.z));
	}
	
	/**
	 * Gets the actual dimensions of a texture resource from a given path
	 *
	 * @param texture The path of the texture resource to check
	 * @return The dimensions (width x height) of the texture
	 */
	public static IntIntPair getTextureDimensions(ResourceLocation texture) {
		GpuTexture gpuTexture = Minecraft.getInstance().getTextureManager().getTexture(texture).getTexture();

		return IntIntPair.of(gpuTexture.getWidth(0), gpuTexture.getHeight(0));
	}

	public static double getCurrentSystemTick() {
		return System.nanoTime() / 1E6 / 50d;
	}

	/**
	 * Returns the current time (in ticks) that the {@link org.lwjgl.glfw.GLFW GLFW} instance has been running
	 * <p>
	 * This is effectively a permanent timer that counts up since the game was launched.
	 */
	public static double getCurrentTick() {
		return Blaze3D.getTime() * 20d;
	}

	/**
	 * Returns a float equivalent of a boolean
	 * <p>
	 * Output table:
	 * <ul>
	 *     <li>true -> 1</li>
	 *     <li>false -> 0</li>
	 * </ul>
	 */
	public static float booleanToFloat(boolean input) {
		return input ? 1f : 0f;
	}

	/**
	 * Converts a given double array to its {@link Vec3} equivalent
	 */
	public static Vec3 arrayToVec(double[] array) {
		return new Vec3(array[0], array[1], array[2]);
	}

	/**
	 * Rotates a {@link GeoBone} to match a provided {@link ModelPart}'s rotations
	 * <p>
	 * Usually used for items or armor rendering to match the rotations of other non-geo model parts
	 */
	public static void matchModelPartRot(ModelPart from, GeoBone to) {
		to.updateRotation(-from.xRot, -from.yRot, from.zRot);
	}

	/**
	 * If a {@link GeoCube} is a 2d plane the {@link software.bernie.geckolib.cache.object.GeoQuad Quad's}
	 * normal is inverted in an intersecting plane,it can cause issues with shaders and other lighting tasks
	 * <p>
	 * This performs a pseudo-ABS function to help resolve some of those issues
	 */
	public static void fixInvertedFlatCube(GeoCube cube, Vector3f normal) {
		if (normal.x() < 0 && (cube.size().y() == 0 || cube.size().z() == 0))
			normal.mul(-1, 1, 1);

		if (normal.y() < 0 && (cube.size().x() == 0 || cube.size().z() == 0))
			normal.mul(1, -1, 1);

		if (normal.z() < 0 && (cube.size().x() == 0 || cube.size().y() == 0))
			normal.mul(1, 1, -1);
	}

	/**
	 * Converts a {@link Direction} to a rotational float for rotation purposes
	 */
	public static float getDirectionAngle(Direction direction) {
		return switch(direction) {
			case SOUTH -> 90f;
			case NORTH -> 270f;
			case EAST -> 180f;
			default -> 0f;
		};
	}

	/**
	 * Special helper function for lerping yaw.
	 * <p>
	 * This exists because yaw in Minecraft handles its yaw a bit strangely, and can cause incorrect results if lerped without accounting for special-cases
	 */
	public static double lerpYaw(double delta, double start, double end) {
		start = Mth.wrapDegrees(start);
		end = Mth.wrapDegrees(end);
		double diff = start - end;
		end = diff > 180 || diff < -180 ? start + Math.copySign(360 - Math.abs(diff), diff) : end;

		return Mth.lerp(delta, start, end);
	}

	/**
	 * Helper method to create the glowmask resource location for a given input texture
	 */
	public static ResourceLocation getEmissiveResource(ResourceLocation textureLocation) {
		return textureLocation.withPath(path -> path.replace(".png", "_glowmask.png"));
	}

	/**
	 * Gets a {@link GeoModel} instance from a given {@link EntityType}
	 * <p>
	 * This only works if you're calling this method for an EntityType known to be using a {@link GeoRenderer GeckoLib Renderer}
	 * <p>
	 * Generally speaking you probably shouldn't be calling this method at all.
	 *
	 * @param entityType The {@code EntityType} to retrieve the GeoModel for
	 * @return The GeoModel, or null if one isn't found
	 */
	@Nullable
	public static GeoModel<?> getGeoModelForEntityType(EntityType<?> entityType) {
		return Minecraft.getInstance().getEntityRenderDispatcher().renderers.get(entityType) instanceof GeoRenderer<?, ?, ?> geoRenderer ? geoRenderer.getGeoModel() : null;
	}

	/**
	 * Gets a GeoAnimatable instance that has been registered as the replacement renderer for a given {@link EntityType}
	 *
	 * @param entityType The {@code EntityType} to retrieve the replaced {@link GeoAnimatable} for
	 * @return The {@code GeoAnimatable} instance, or null if one isn't found
	 */
	@Nullable
	public static GeoAnimatable getReplacedAnimatable(EntityType<?> entityType) {
		return Minecraft.getInstance().getEntityRenderDispatcher().renderers.get(entityType) instanceof GeoReplacedEntityRenderer<?, ?, ?> replacedEntityRenderer ? replacedEntityRenderer.getAnimatable() : null;
	}

	/**
	 * Gets a {@link GeoModel} instance from a given {@link Entity}
	 * <p>
	 * This only works if you're calling this method for an Entity known to be using a {@link GeoRenderer GeckoLib Renderer}
	 * <p>
	 * Generally speaking you probably shouldn't be calling this method at all.
	 *
	 * @param entity The {@code Entity} to retrieve the GeoModel for
	 * @return The GeoModel, or null if one isn't found
	 */
	@Nullable
	public static GeoModel<?> getGeoModelForEntity(Entity entity) {
		return Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity) instanceof GeoRenderer<?, ?, ?> geoRenderer ? geoRenderer.getGeoModel() : null;
	}

	/**
	 * Gets a {@link GeoModel} instance from a given {@link Item}
	 * <p>
	 * This only works if you're calling this method for an Item known to be using a {@link GeoRenderer GeckoLib Renderer}
	 * <p>
	 * Generally speaking you probably shouldn't be calling this method at all.
	 *
	 * @param item The {@code ItemStack} to retrieve the GeoModel for
	 * @return The GeoModel, or null if one isn't found
	 */
	@Nullable
	public static GeoModel<?> getGeoModelForItem(ItemStack item) {
		return GeckoLibServices.Client.ITEM_RENDERING.getGeoModelForItem(item);
	}

	/**
	 * Gets a {@link GeoModel} instance from a given {@link BlockEntity}
	 * <p>
	 * This only works if you're calling this method for a BlockEntity known to be using a {@link GeoRenderer GeckoLib Renderer}
	 * <p>
	 * Generally speaking you probably shouldn't be calling this method at all
	 *
	 * @param blockEntity The {@code BlockEntity} to retrieve the GeoModel for
	 * @return The GeoModel, or null if one isn't found
	 */
	@Nullable
	public static GeoModel<?> getGeoModelForBlock(BlockEntity blockEntity) {
		BlockEntityRenderer<?> renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(blockEntity);

		return renderer instanceof GeoRenderer<?, ?, ?> geoRenderer ? geoRenderer.getGeoModel() : null;
	}

	/**
	 * Gets a {@link GeoModel} instance from a given {@link Item}
	 * <p>
	 * This only works if you're calling this method for an Item known to be using a {@link software.bernie.geckolib.renderer.GeoArmorRenderer GeoArmorRenderer}
	 * <p>
	 * Generally speaking you probably shouldn't be calling this method at all
	 *
	 * @param stack The ItemStack to retrieve the GeoModel for
	 * @param slot The equipment slot the stack would be equipped in
	 * @param type The equipment model type to retrieve
	 * @return The GeoModel, or null if one isn't found
	 */
	@Nullable
	public static GeoModel<?> getGeoModelForArmor(ItemStack stack, EquipmentSlot slot, EquipmentClientInfo.LayerType type) {
		return GeckoLibServices.Client.ITEM_RENDERING.getGeoModelForArmor(stack, slot, type);
	}
}
