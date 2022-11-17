package software.bernie.geckolib3.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import software.bernie.geckolib3.cache.object.BakedGeoModel;
import software.bernie.geckolib3.cache.object.GeoBone;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.renderer.GeoRenderer;
import software.bernie.geckolib3.util.RenderUtils;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

/**
 * {@link GeoRenderLayer} for rendering {@link net.minecraft.world.level.block.state.BlockState BlockStates}
 * or {@link net.minecraft.world.item.ItemStack ItemStacks} on a given {@link GeoAnimatable}
 */
public abstract class BlockAndItemGeoLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {
	protected final BiFunction<GeoBone, T, ItemStack> stackForBone;
	protected final BiFunction<GeoBone, T, BlockState> blockForBone;

	public BlockAndItemGeoLayer(GeoRenderer<T> renderer) {
		this(renderer, (bone, animatable) -> null, (bone, animatable) -> null);
	}

	public BlockAndItemGeoLayer(GeoRenderer<T> renderer, BiFunction<GeoBone, T, ItemStack> stackForBone, BiFunction<GeoBone, T, BlockState> blockForBone) {
		super(renderer);

		this.stackForBone = stackForBone;
		this.blockForBone = blockForBone;
	}

	/**
	 * Return an ItemStack relevant to this bone for rendering, or null if no ItemStack to render
	 */
	@Nullable
	protected ItemStack getStackForBone(GeoBone bone, T animatable) {
		return this.stackForBone.apply(bone, animatable);
	}

	/**
	 * Return a BlockState relevant to this bone for rendering, or null if no BlockState to render
	 */
	@Nullable
	protected BlockState getBlockForBone(GeoBone bone, T animatable) {
		return this.blockForBone.apply(bone, animatable);
	}

	/**
	 * Return a specific TransFormType for this {@link ItemStack} render for this bone.
	 */
	protected ItemTransforms.TransformType getTransformTypeForStack(GeoBone bone, ItemStack stack, T animatable) {
		return ItemTransforms.TransformType.NONE;
	}

	/**
	 * This is the method that is actually called by the render for your render layer to function.<br>
	 * This is called <i>after</i> the animatable has been rendered, but before supplementary rendering like nametags.
	 */
	@Override
	public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource,
					   VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
		for (GeoBone bone : bakedModel.topLevelBones()) {
			checkChildBones(poseStack, bone, animatable, bufferSource, partialTick, packedLight, packedOverlay);
		}
	}

	private void checkChildBones(PoseStack poseStack, GeoBone parentBone, T animatable, MultiBufferSource bufferSource,
								 float partialTick, int packedLight, int packedOverlay) {
		poseStack.pushPose();
		RenderUtils.prepMatrixForBone(poseStack, parentBone);

		tryRenderForBone(poseStack, parentBone, animatable, bufferSource, partialTick, packedLight, packedOverlay);

		for (GeoBone bone : parentBone.getChildBones()) {
			checkChildBones(poseStack, bone, animatable, bufferSource, partialTick, packedLight, packedOverlay);
		}

		poseStack.popPose();
	}

	/**
	 * This method is called for each bone in the model.<br>
	 * Check whether the bone is relevant and render as needed.
	 */
	protected void tryRenderForBone(PoseStack poseStack, GeoBone bone, T animatable, MultiBufferSource bufferSource,
									float partialTick, int packedLight, int packedOverlay) {
		ItemStack stack = getStackForBone(bone, animatable);
		BlockState blockState = getBlockForBone(bone, animatable);

		if (stack == null && blockState == null)
			return;

		poseStack.pushPose();
		RenderUtils.translateAndRotateMatrixForBone(poseStack, bone);

		if (stack != null)
			renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);

		if (blockState != null)
			renderBlockForBone(poseStack, bone, blockState, animatable, bufferSource, partialTick, packedLight, packedOverlay);

		poseStack.popPose();
	}

	/**
	 * Render the given {@link ItemStack} for the provided {@link GeoBone}.
	 */
	protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, T animatable, MultiBufferSource bufferSource,
									  float partialTick, int packedLight, int packedOverlay) {
		if (animatable instanceof LivingEntity livingEntity) {
			Minecraft.getInstance().getItemRenderer().renderStatic(livingEntity, stack,
					getTransformTypeForStack(bone, stack, animatable), false, poseStack, bufferSource, livingEntity.level,
					packedLight, packedOverlay, livingEntity.getId());
		}
		else {
			Minecraft.getInstance().getItemRenderer().renderStatic(stack, getTransformTypeForStack(bone, stack, animatable),
					packedLight, packedOverlay, poseStack, bufferSource, (int)this.renderer.getInstanceId(animatable));
		}
	}

	/**
	 * Render the given {@link BlockState} for the provided {@link GeoBone}.
	 */
	protected void renderBlockForBone(PoseStack poseStack, GeoBone bone, BlockState state, T animatable, MultiBufferSource bufferSource,
									  float partialTick, int packedLight, int packedOverlay) {
		poseStack.pushPose();
		poseStack.translate(-0.25f, -0.25f, -0.25f);
		poseStack.scale(0.5f, 0.5f, 0.5f);
		Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, poseStack, bufferSource, packedLight, packedOverlay, ModelData.EMPTY, null);
		poseStack.popPose();
	}
}
