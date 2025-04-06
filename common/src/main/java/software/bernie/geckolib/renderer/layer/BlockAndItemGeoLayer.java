package software.bernie.geckolib.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.util.RenderUtil;

import java.util.function.BiFunction;

/**
 * {@link GeoRenderLayer} for rendering {@link BlockState BlockStates}
 * or {@link ItemStack ItemStacks} on a given {@link GeoAnimatable}
 */
public class BlockAndItemGeoLayer<T extends GeoAnimatable, O, R extends GeoRenderState> extends GeoRenderLayer<T, O, R> {
    protected final BiFunction<GeoBone, GeoRenderState, ItemStack> stackForBone;
    protected final BiFunction<GeoBone, GeoRenderState, BlockState> blockForBone;

    public BlockAndItemGeoLayer(GeoRenderer<T, O, R> renderer) {
        this(renderer, (bone, animatable) -> null, (bone, animatable) -> null);
    }

    public BlockAndItemGeoLayer(GeoRenderer<T, O, R> renderer, BiFunction<GeoBone, GeoRenderState, ItemStack> stackForBone, BiFunction<GeoBone, GeoRenderState, BlockState> blockForBone) {
        super(renderer);

        this.stackForBone = stackForBone;
        this.blockForBone = blockForBone;
    }

    /**
     * Return an ItemStack relevant to this bone for rendering, or null if no ItemStack to render
     */
    @Nullable
    protected ItemStack getStackForBone(GeoBone bone, R renderState) {
        return this.stackForBone.apply(bone, renderState);
    }

    /**
     * Return a BlockState relevant to this bone for rendering, or null if no BlockState to render
     */
    @Nullable
    protected BlockState getBlockForBone(GeoBone bone, R renderState) {
        return this.blockForBone.apply(bone, renderState);
    }

    /**
     * Return a specific TransFormType for this {@link ItemStack} render for this bone.
     */
    protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, R renderState) {
        return ItemDisplayContext.NONE;
    }

    /**
     * This method is called by the {@link GeoRenderer} for each bone being rendered
     * <p>
     * You would use this to render something at or for a given GeoBone's position and orientation.
     * <p>
     * You <b><u>MUST NOT</u></b> perform any rendering operations here, and instead must contain all your functionality in the returned Runnable
     */
    @Nullable
    @Override
    public Runnable createPerBoneRender(R renderState, PoseStack poseStack, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource) {
        ItemStack stack = getStackForBone(bone, renderState);
        BlockState blockState = getBlockForBone(bone, renderState);

        if (stack == null && blockState == null)
            return null;

        return () -> {
            poseStack.pushPose();
            RenderUtil.translateAndRotateMatrixForBone(poseStack, bone);

            int packedLight = renderState.getGeckolibData(DataTickets.PACKED_LIGHT);
            int packedOverlay = renderState.getGeckolibData(DataTickets.PACKED_OVERLAY);

            if (stack != null)
                renderStackForBone(poseStack, bone, stack, renderState, bufferSource, packedLight, packedOverlay);

            if (blockState != null)
                renderBlockForBone(poseStack, bone, blockState, renderState, bufferSource, packedLight, packedOverlay);

            poseStack.popPose();
        };
    }

    /**
     * Render the given {@link ItemStack} for the provided {@link GeoBone}.
     */
    protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, R renderState, MultiBufferSource bufferSource,
                                      int packedLight, int packedOverlay) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, getTransformTypeForStack(bone, stack, renderState), packedLight, packedOverlay,
                                                               poseStack, bufferSource, Minecraft.getInstance ().level, renderState.getGeckolibData(DataTickets.ANIMATABLE_INSTANCE_ID).intValue());
    }

    /**
     * Render the given {@link BlockState} for the provided {@link GeoBone}.
     */
    protected void renderBlockForBone(PoseStack poseStack, GeoBone bone, BlockState state, R renderState, MultiBufferSource bufferSource,
                                      int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(-0.25f, -0.25f, -0.25f);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
