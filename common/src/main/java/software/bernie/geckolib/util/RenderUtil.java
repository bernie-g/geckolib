package software.bernie.geckolib.util;

import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.cache.model.GeoBone;
import software.bernie.geckolib.cache.model.GeoQuad;
import software.bernie.geckolib.cache.model.cuboid.GeoCube;
import software.bernie.geckolib.renderer.*;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.RenderPassInfo;

import java.util.List;

/**
 * Helper class for various methods and functions useful while rendering
 */
public final class RenderUtil {
    /**
     * Transform a PoseStack to match a bone's render position.
     * <p>
     * Can only be used inside a {@link RenderPassInfo#renderPosed} call
     */
    public static void transformToBone(PoseStack poseStack, GeoBone bone) {
        final List<GeoBone> boneQueue = new ObjectArrayList<>();
        GeoBone parent = bone;

        boneQueue.add(bone);

        while ((parent = parent.parent()) != null) {
            boneQueue.add(parent);
        }

        for (GeoBone bone2 : boneQueue) {
            prepMatrixForBone(poseStack, bone2);
        }
    }

	public static void translateAndRotateMatrixForBone(PoseStack poseStack, GeoBone bone) {
        bone.translateToPivotPoint(poseStack);

        float xRot = bone.baseRotX();
        float yRot = bone.baseRotY();
        float zRot = bone.baseRotZ();

        if (bone.frameSnapshot != null) {
            xRot += bone.frameSnapshot.getRotX();
            yRot += bone.frameSnapshot.getRotY();
            zRot += bone.frameSnapshot.getRotZ();
        }

        if (zRot != 0)
            poseStack.mulPose(Axis.ZP.rotation(zRot));

        if (yRot != 0)
            poseStack.mulPose(Axis.YP.rotation(yRot));

        if (xRot != 0)
            poseStack.mulPose(Axis.XP.rotation(xRot));
	}

    /**
     * Make the necessarily manipulations of the {@link PoseStack} to position a bone based on its current snapshot and state
     */
	public static void prepMatrixForBone(PoseStack poseStack, GeoBone bone) {
        prepMatrixForBoneAndUpdateListeners(poseStack, bone, null);
	}

    /**
     * Make the necessarily manipulations of the {@link PoseStack} to position a bone based on its current snapshot and state
     * <p>
     * Additionally update the RenderPassInfo's {@link RenderPassInfo.BonePositionListener}s, if applicable
     */
	public static void prepMatrixForBoneAndUpdateListeners(PoseStack poseStack, GeoBone bone, @Nullable RenderPassInfo<?> renderPassInfo) {
        if (bone.frameSnapshot != null)
            bone.frameSnapshot.translate(poseStack);

        translateAndRotateMatrixForBone(poseStack, bone);

        if (bone.frameSnapshot != null)
            bone.frameSnapshot.scale(poseStack);

        if (renderPassInfo != null)
            bone.updateBonePositionListeners(poseStack, renderPassInfo);

        bone.translateAwayFromPivotPoint(poseStack);
	}

    /**
     * Convert a {@link Matrix4fc} pose to a three-dimensional vector position, multiplying it by input values
     * to allow for inline transformations
     */
    public static Vec3 renderPoseToPosition(Matrix4fc pose, float xScale, float yScale, float zScale) {
        final Vector4f position = pose.transform(new Vector4f(0, 0, 0, 1));

        return new Vec3(position.x() * xScale, position.y() * yScale, position.z() * zScale);
    }

    /**
     * Extract the relative pose of an input matrix from a base matrix
     */
	public static Matrix4f extractPoseFromRoot(Matrix4fc baseMatrix, Matrix4f inputMatrix) {
		inputMatrix = new Matrix4f(inputMatrix);
		
		inputMatrix.invert();
		inputMatrix.mul(baseMatrix);

		return inputMatrix;
	}

    /**
     * Directly translate a Matrix pose by a given position
     */
    public static Matrix4f addPosToMatrix(Matrix4f baseMatrix, Vec3 pos) {
        baseMatrix.m30(baseMatrix.m30() + (float)pos.x)
                .m31(baseMatrix.m31() + (float)pos.y)
                .m32(baseMatrix.m32() + (float)pos.z);

        return baseMatrix;
    }
	
	/**
     * Translates the provided {@link PoseStack} to face towards the given {@link Entity}'s rotation
	 * <p>
     * Usually used for rotating projectiles towards their trajectory, in an {@link GeoRenderer#preRenderPass} override
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
	public static IntIntPair getTextureDimensions(Identifier texture) {
		GpuTexture gpuTexture = Minecraft.getInstance().getTextureManager().getTexture(texture).getTexture();

		return IntIntPair.of(gpuTexture.getWidth(0), gpuTexture.getHeight(0));
	}

	/**
	 * If a {@link GeoCube} is a 2d plane and the {@link GeoQuad Quad's} normal is inverted on an intersecting plane,
     * it can cause issues with shaders and other lighting tasks
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
	 * Helper method to create the glowmask resource location for a given input texture
	 */
	public static Identifier getEmissiveResource(Identifier textureLocation) {
		return textureLocation.withPath(path -> path.replace(".png", "_glowmask.png"));
	}

    /**
     * Gets a registered {@link GeoReplacedEntityRenderer} for a given {@link Entity} if it has had its renderer replaced
     *
     * @param entityType The {@link EntityType} to retrieve the replaced renderer for
     * @return The GeckoLib replaced renderer for the given entity, or null if not applicable
     */
    public static @Nullable GeoReplacedEntityRenderer<?, ?, ?> getReplacedEntityRenderer(EntityType<?> entityType) {
        return Minecraft.getInstance().getEntityRenderDispatcher().renderers.get(entityType) instanceof GeoReplacedEntityRenderer<?, ?, ?> replacedEntityRenderer ? replacedEntityRenderer : null;
    }

    /**
     * Gets a registered {@link GeoItemRenderer} for a given {@link Item}, if applicable
     *
     * @param item The item to retrieve the renderer for
     * @return The GeoItemRenderer instance, or null if not applicable
     */
    public static @Nullable GeoItemRenderer<?> getGeckoLibItemRenderer(Item item) {
        return GeoRenderProvider.of(item).getGeoItemRenderer();
    }

    /**
     * Gets a registered {@link GeoEntityRenderer} for a given {@link EntityType}, if applicable
     *
     * @param entityType The {@code EntityType} to retrieve the renderer for
     * @return The {@code GeoEntityRenderer} instance, or null if not applicable
     */
    public static @Nullable GeoEntityRenderer<?, ?> getGeckoLibEntityRenderer(EntityType<?> entityType) {
        return Minecraft.getInstance().getEntityRenderDispatcher().renderers.get(entityType) instanceof GeoEntityRenderer<?, ?> geoEntityRenderer ? geoEntityRenderer : null;
    }

    /**
     * Gets a registered {@link GeoBlockRenderer} for a given {@link BlockEntityType}, if applicable
     *
     * @param blockEntityType The {@code BlockEntityType} to retrieve the renderer for
     * @return The {@code GeoBlockRenderer} instance, or null if not applicable
     */
    public static @Nullable GeoBlockRenderer<?, ?> getGeckoLibBlockRenderer(BlockEntityType<?> blockEntityType) {
        return Minecraft.getInstance().getBlockEntityRenderDispatcher().renderers.get(blockEntityType) instanceof GeoBlockRenderer<?, ?> geoBlockRenderer ? geoBlockRenderer : null;
    }

    /**
     * Gets a registered {@link GeoArmorRenderer} for a given {@link Item}, if applicable
     *
     * @param item The {@code Item} to retrieve the renderer for
     * @return The {@code GeoArmorRenderer} instance, or null if not applicable
     * @see #getGeckoLibArmorRenderer(ItemStack, EquipmentSlot)
     */
    @SuppressWarnings({"DataFlowIssue", "ConstantValue"})
    public static @Nullable GeoArmorRenderer<?, ?> getGeckoLibArmorRenderer(Item item) {
        final ItemStack stack = item.getDefaultInstance();
        final Equippable equippable = stack.getOrDefault(DataComponents.EQUIPPABLE, null);

        if (equippable == null)
            return null;

        return getGeckoLibArmorRenderer(stack, equippable.slot());
    }

    /**
     * Gets a registered {@link GeoArmorRenderer} for a given {@link ItemStack}, if applicable
     *
     * @param stack The {@code ItemStack} to retrieve the renderer for
     * @param slot The {@link EquipmentSlot} to retrieve the renderer for
     * @return The {@code GeoArmorRenderer} instance, or null if not applicable
     */
    public static @Nullable GeoArmorRenderer<?, ?> getGeckoLibArmorRenderer(ItemStack stack, EquipmentSlot slot) {
        return GeoRenderProvider.of(stack).getGeoArmorRenderer(stack, slot);
    }

    /**
     * Gets a GeoAnimatable instance that has been registered as the replacement renderer for a given {@link EntityType}
     *
     * @param entityType The {@code EntityType} to retrieve the replaced {@link GeoAnimatable} for
     * @return The {@code GeoAnimatable} instance, or null if one isn't found
     */
    public static @Nullable GeoAnimatable getReplacedAnimatable(EntityType<?> entityType) {
        final GeoReplacedEntityRenderer<?, ?, ?> replacedEntityRenderer = getReplacedEntityRenderer(entityType);

        return replacedEntityRenderer == null ? null : replacedEntityRenderer.getAnimatable();
    }

    private RenderUtil() {}
}
