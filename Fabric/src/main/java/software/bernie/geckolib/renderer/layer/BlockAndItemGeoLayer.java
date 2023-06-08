package software.bernie.geckolib.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.util.RenderUtils;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

/**
 * {@link GeoRenderLayer} for rendering {@link net.minecraft.world.level.block.state.BlockState BlockStates}
 * or {@link net.minecraft.world.item.ItemStack ItemStacks} on a given {@link GeoAnimatable}
 */
public class BlockAndItemGeoLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {
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
    protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, T animatable) {
        return ItemDisplayContext.NONE;
    }

    /**
     * This method is called by the {@link GeoRenderer} for each bone being rendered.<br>
     * This is a more expensive call, particularly if being used to render something on a different buffer.<br>
     * It does however have the benefit of having the matrix translations and other transformations already applied from render-time.<br>
     * It's recommended to avoid using this unless necessary.<br>
     * <br>
     * The {@link GeoBone} in question has already been rendered by this stage.<br>
     * <br>
     * If you <i>do</i> use it, and you render something that changes the {@link VertexConsumer buffer}, you need to reset it back to the previous buffer
     * using {@link MultiBufferSource#getBuffer} before ending the method
     */
    @Override
    public void renderForBone(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource,
                              VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
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

        buffer = bufferSource.getBuffer(renderType);

        poseStack.popPose();
    }

    /**
     * Render the given {@link ItemStack} for the provided {@link GeoBone}.
     */
    protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, T animatable, MultiBufferSource bufferSource,
                                      float partialTick, int packedLight, int packedOverlay) {
        if (animatable instanceof LivingEntity livingEntity) {
            Minecraft.getInstance().getItemRenderer().renderStatic(livingEntity, stack,
                    getTransformTypeForStack(bone, stack, animatable), false, poseStack, bufferSource, livingEntity.level(),
                    packedLight, packedOverlay, livingEntity.getId());
        } else {
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, getTransformTypeForStack(bone, stack, animatable),
                    packedLight, packedOverlay, poseStack, bufferSource, Minecraft.getInstance ().level, (int) this.renderer.getInstanceId(animatable));
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
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
