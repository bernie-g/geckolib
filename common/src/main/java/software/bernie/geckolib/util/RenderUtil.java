package software.bernie.geckolib.util;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.GeoReplacedEntityRenderer;
import software.bernie.geckolib.util.helper.LevelRendererGeckoLibAccess;

import java.io.InputStream;

/**
 * Helper class for various methods and functions useful while rendering
 */
public final class RenderUtil {
	public static boolean isRenderingInWorld() {
		return Minecraft.getInstance().levelRenderer instanceof LevelRendererGeckoLibAccess access && access.geckolib$isRenderingInWorld();
	}

	public static void translateMatrixToBone(PoseStack poseStack, GeoBone bone) {
		poseStack.translate(-bone.getPosX() / 16f, bone.getPosY() / 16f, bone.getPosZ() / 16f);
	}

	public static void rotateMatrixAroundBone(PoseStack poseStack, GeoBone bone) {
		RenderUtil.optionalRotateZYX(poseStack, bone.getRotZ(), bone.getRotY(), bone.getRotX());
	}

	public static void rotateMatrixAroundCube(PoseStack poseStack, GeoCube cube) {
		final Vec3 rotation = cube.rotation();

		optionalRotateZYX(poseStack, rotation.z(), rotation.y(), rotation.x());
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

		if (bone.getRotX() == 0 && bone.getRotY() == 0 && bone.getRotZ() == 0 &&
			bone.getScaleX() == 1 && bone.getScaleY() == 1 && bone.getScaleZ() == 1)
			return;

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
		return matrix.translateLocal(vector);
	}
	
	/**
	 * Gets the actual dimensions of a texture resource from a given path
	 * <p>
	 * Not performance-efficient, and should not be relied upon
	 *
	 * @param texture The path of the texture resource to check
	 * @return The dimensions (width x height) of the texture, or null if unable to find or read the file
	 */
	@Nullable
	public static IntIntPair getTextureDimensions(@Nullable ResourceLocation texture) {
		if (texture == null)
			return null;

		final Minecraft mc = Minecraft.getInstance();
		final AbstractTexture originalTexture;
		final NativeImage image;

		try {
			originalTexture = mc.submit(() -> mc.getTextureManager().getTexture(texture)).get();

			if (originalTexture instanceof DynamicTexture dynamicTexture) {
				image = dynamicTexture.getPixels();

				if (image == null)
					return null;
			}
			else {
				final Resource resource = mc.getResourceManager().getResource(texture).orElse(null);

				if (resource == null)
					return null;

				try (InputStream stream = resource.open()) {
					image = NativeImage.read(stream);
				}
			}
		}
		catch (Exception ex) {
			GeckoLibConstants.LOGGER.warn("Failed to load image for id {}", texture, ex);

			return null;
		}

		final IntIntPair dimensions = IntIntImmutablePair.of(image.getWidth(), image.getHeight());

		if (!(originalTexture instanceof DynamicTexture))
			image.close();

		return dimensions;
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
		final double x;
		final double y;
		final double z;

		if (array.length == 1) {
			x = y = z = array[0];
		}
		else {
			x = array[0];
			y = array[1];
			z = array[2];
		}

		if (x == 0 && y == 0 && z == 0)
			return Vec3.ZERO;

		return new Vec3(x, y, z);
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
	 * Scale a {@link GeoBone} to match a provided {@link ModelPart}'s scale
	 * <p>
	 * Usually used for items or armor rendering to match the scale of other non-geo model parts
	 */
	public static void matchModelPartScale(ModelPart from, GeoBone to) {
		to.updateScale(from.xScale, from.yScale, from.zScale);
	}

	/**
	 * If a {@link GeoCube} is a 2d plane the {@link software.bernie.geckolib.cache.object.GeoQuad Quad's}
	 * normal is inverted in an intersecting plane,it can cause issues with shaders and other lighting tasks
	 * <p>
	 * This performs a pseudo-ABS function to help resolve some of those issues
	 */
	public static void fixInvertedFlatCube(GeoCube cube, Vector3f normal) {
		if (normal.x() < 0 && cube.size().x() == 0)
			normal.mul(-1, 1, 1);

		if (normal.y() < 0 && cube.size().y() == 0)
			normal.mul(1, -1, 1);

		if (normal.z() < 0 && cube.size().z() == 0)
			normal.mul(1, 1, -1);
	}

	/// @return `true` if the given quad is the face of a 0-thickness cube
	public static boolean isFlatQuad(GeoCube cube, GeoQuad quad) {
		return cube.size().get(quad.direction().getAxis()) == 0;
	}

	/// @return `true` if the given normal is facing towards the camera (or forwards, if in GUI rendering)
	public static boolean isFacingCamera(Vector3f normal, Vector3f position) {
		if (RenderSystem.getVertexSorting() == VertexSorting.ORTHOGRAPHIC_Z)
			return normal.z() >= 0;

		return normal.dot(position) <= 0;
	}

	/**
	 * Rotate a [PoseStack] around a given z/y/x axis, reducing the amount of work performed to the minimal required for the given values
	 */
	public static void optionalRotateZYX(PoseStack poseStack, double z, double y, double x) {
		if (z == 0 && y == 0 && x == 0)
			return;

		final Quaternionf quat = new Quaternionf();

		if (x == 0 && y == 0) {
			quat.rotationZ((float)z);
		}
		else if (x == 0 && z == 0) {
			quat.rotationY((float)y);
		}
		else if (y == 0 && z == 0) {
			quat.rotationX((float)x);
		}
		else {
			quat.rotationZYX((float)z, (float)y, (float)x);
		}

		poseStack.mulPose(quat);
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
		EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().renderers.get(entityType);

		return renderer instanceof GeoRenderer<?> geoRenderer ? geoRenderer.getGeoModel() : null;
	}

	/**
	 * Gets a GeoAnimatable instance that has been registered as the replacement renderer for a given {@link EntityType}
	 *
	 * @param entityType The {@code EntityType} to retrieve the replaced {@link GeoAnimatable} for
	 * @return The {@code GeoAnimatable} instance, or null if one isn't found
	 */
	@Nullable
	public static GeoAnimatable getReplacedAnimatable(EntityType<?> entityType) {
		EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().renderers.get(entityType);

		return renderer instanceof GeoReplacedEntityRenderer<?, ?> replacedEntityRenderer ? replacedEntityRenderer.getAnimatable() : null;
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
		EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);

		return renderer instanceof GeoRenderer<?> geoRenderer ? geoRenderer.getGeoModel() : null;
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

		return renderer instanceof GeoRenderer<?> geoRenderer ? geoRenderer.getGeoModel() : null;
	}

	/**
	 * Gets a {@link GeoModel} instance from a given {@link Item}
	 * <p>
	 * This only works if you're calling this method for an Item known to be using a {@link software.bernie.geckolib.renderer.GeoArmorRenderer GeoArmorRenderer}
	 * <p>
	 * Generally speaking you probably shouldn't be calling this method at all
	 *
	 * @param stack The ItemStack to retrieve the GeoModel for
	 * @return The GeoModel, or null if one isn't found
	 */
	@Nullable
	public static GeoModel<?> getGeoModelForArmor(ItemStack stack) {
		return GeckoLibServices.Client.ITEM_RENDERING.getGeoModelForArmor(stack);
	}
}
